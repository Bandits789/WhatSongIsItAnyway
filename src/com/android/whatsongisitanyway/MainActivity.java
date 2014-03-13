package com.android.whatsongisitanyway;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.android.whatsongisitanyway.models.Game;
import com.android.whatsongisitanyway.models.Music;

public class MainActivity extends Activity {
	private Game game;
	private MediaPlayer mediaPlayer = null;
	private Thread timerThread = null;
	private boolean running = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// add enter listener
		TextView songBox = (TextView) findViewById(R.id.songTextbox);
		songBox.setOnEditorActionListener(submitListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Either starts up media player if it is null, or skips to the next song if
	 * a song is already playing
	 * 
	 * @param view
	 */
	public void skip(View view) {
		if (running) {
			// penalty for skipping
			game.skipPenalty();
		} else {
			running = true;
			game = new Game(getResources());
			initTimerThread();
		}

		goToNextSong();
	}

	/**
	 * Pauses/resumes song play
	 * 
	 * @param view
	 */
	public void pause(View view) {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				game.pause();
			} else {
				mediaPlayer.start();
				game.resume();
			}
		}
	}

	/**
	 * Submits the guess to the game, updates the score
	 * 
	 * @param view
	 */
	public void submit(View view) {
		TextView songBox = (TextView) findViewById(R.id.songTextbox);
		TextView scoreLabel = (TextView) findViewById(R.id.score);

		// if the score hasn't changed, it returns 0
		int score = game.guess(songBox.getText().toString());
		songBox.setText("");

		// if they got it right, update score and skip songs
		if (score > 0) {
			scoreLabel.setText("Score: " + score);
			goToNextSong();
		}
	}

	/**
	 * Set the music player to go to the next song, if there are any left.
	 * Release the player if we're done.
	 */
	public void goToNextSong() {
		Music nextSong = game.getNextSong();
		TextView textView = (TextView) findViewById(R.id.title);

		// if we still have music to play
		if (nextSong != null) {
			try {
				// set the new title
				textView.setText(nextSong.getID());

				// stop old music player
				if (mediaPlayer != null) {
					mediaPlayer.stop();
				}

				// create new music player
				mediaPlayer = MediaPlayer.create(textView.getContext(),
						nextSong.getID());

				// make sure rest of songs play
				mediaPlayer
						.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mediaPlayer) {
								goToNextSong();
							}
						});

				// actually start!
				mediaPlayer.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// we're done!
			// TODO: do something to alert user here...
			mediaPlayer.release();
			mediaPlayer = null;
			running = false;
			textView.setText("What Song Is It Anyway?");
		}
	}

	/**
	 * Initialize the timer loop thread
	 * 
	 */
	private void initTimerThread() {
		timerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				game.start();
				TextView timerLabel = (TextView) findViewById(R.id.timer);

				// while we have time left
				while (running && game.timeLeft() > 0) {

					updateTimerLabel(timerLabel);

					// to avoid updating too often, sleep for .2 secs
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				// TODO: we're done, now what?
				running = false;
				updateTimerLabel(timerLabel);

				if (mediaPlayer != null) {
					mediaPlayer.pause();
				}
			}
		});

		timerThread.start();
	}

	/**
	 * Updates the timer label to display the time left on the timer
	 * 
	 * @param timerLabel
	 *            the timer label TextView
	 */
	private void updateTimerLabel(final TextView timerLabel) {

		// update UI on its own thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				timerLabel.setText(game.timeLeft() + "");
			}
		});
	}

	/**
	 * This is the on enter listener- it submits the entered text
	 */
	TextView.OnEditorActionListener submitListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				submit(view);
			}
			return true;
		}
	};

}
