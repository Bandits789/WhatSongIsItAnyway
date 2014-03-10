package com.android.whatsongisitanyway;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.android.whatsongisitanyway.models.Game;
import com.android.whatsongisitanyway.models.Music;

public class MainActivity extends Activity {
	private Game game;
	private MediaPlayer mediaPlayer = null;
	private Thread timerThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		if (mediaPlayer == null) {
			game = new Game(getResources());
			initTimerThread();
		} else {
			// penalty for skipping
			game.skipPenalty();
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
				// game.resume();
			}
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
				game.resume();

				// while we have time left
				while (game.timeLeft() > 0) {
					// TextView timerLabel = (TextView)
					// findViewById(R.id.timer);
					// Log.d("timerLabel is", timerLabel.getText().toString());
					// Log.d("timeLeft is", game.timeLeft() + "");
					// timerLabel.setText(game.timeLeft());
				}

				// TODO: we're done, now what?
				mediaPlayer.pause();
				game.pause();

			}
		});

		timerThread.start();
	}

}
