package com.android.whatsongisitanyway;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.whatsongisitanyway.database.GameDatabaseHelper;
import com.android.whatsongisitanyway.models.Game;
import com.android.whatsongisitanyway.models.Music;

/**
 * This is the activity where the game actually lies. This makes a music player,
 * and allows user guessing of each song played. It goes to the score screen
 * after either we run out of songs or the game timer runs out of time.
 */
public class PlayActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private Game game;
	private MediaPlayer mediaPlayer;
	private Music currentSong = null;
	private int score = 0;

	private boolean running = false;
	private boolean paused = false;

	private GameDatabaseHelper dbHelper;
	private int[] settings;

	private final List<Music> songsList = new ArrayList<Music>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// loading song stuff
		getLoaderManager().initLoader(1, null, this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);

		// get settings
		dbHelper = new GameDatabaseHelper(this);
		settings = dbHelper.getSettings();

		// make a new media player
		mediaPlayer = new MediaPlayer();

		// add enter listener
		TextView songBox = (TextView) findViewById(R.id.songTextbox);
		songBox.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					submit(view);
					return true;
				}
				return false;
			}
		});

	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onPause() {
		super.onPause();
		pause(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// only get things that are music
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		// path, title, duration, artist, album, size
		String[] projection = { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.SIZE };

		return new CursorLoader(this,
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// load all the shit
		cursor.moveToFirst();
		int songDuration = settings[1];

		while (!cursor.isAfterLast()) {
			int duration = cursor.getInt(2);

			// if longer than a minute, add it
			if (duration > 60 * 1000) {
				// path, title, duration, artist, album, size
				songsList.add(new Music(cursor.getString(0), cursor
						.getString(1), duration, cursor.getString(3), cursor
						.getString(4), cursor.getInt(5), songDuration));
			}

			cursor.moveToNext();
		}

		// start!
		startGame();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// so bad. many sad. wow.
	}

	/**
	 * Starts the game! Makes a game, starts timer, plays music.
	 */
	private void startGame() {
		running = true;

		// make a new game (duration is from saved settings)
		int gameDuration = settings[0];
		game = new Game(songsList, this, gameDuration);

		// set max of timer to be max of game duration
		ProgressBar timerBar = (ProgressBar) findViewById(R.id.scoreBar);
		timerBar.setMax(game.getDuration());
		timerBar.setProgress(0);

		// remove pause overlay
		setPauseOverlay(false);

		// show keyboard
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);

		// start timer and play the first song
		initTimerThread();
		goToNextSong();
	}

	/**
	 * Either starts up media player if it is null, or skips to the next song if
	 * a song is already playing
	 * 
	 * @param view
	 */
	public void skip(View view) {
		// do nothing if paused
		if (paused) {
			return;
		}

		if (running) {
			// penalty for skipping
			game.skipPenalty();
			// multiplier and streak are lost
			updateUILabel(R.id.streak, "Streak: 0");
			updateUILabel(R.id.multiplier, "Multiplier: 1");

			// empty text box
			updateUILabel(R.id.songTextbox, "");

			// show the song they missed
			Toast toast = Toast.makeText(this, currentSong.getTitle(),
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}

		goToNextSong();
	}

	/**
	 * Pauses/resumes song play
	 * 
	 * @param view
	 */
	public void pause(View view) {
		if (running) {
			if (!paused) {
				// if running and not paused, pause it
				paused = true;
				mediaPlayer.pause();
				game.pause();
				// hide play view and show resume overlay
				setPauseOverlay(true);
			}
		}
	}

	/**
	 * Button listener for resume button to resume game
	 * 
	 * @param View
	 */
	public void resume(View view) {
		if (running) {
			// hide resume view
			setPauseOverlay(false);

			// resume game
			paused = false;
			mediaPlayer.start();
			game.resume();
		}
	}

	/**
	 * Submits the guess to the game, updates the score
	 * 
	 * @param view
	 */
	public void submit(View view) {
		TextView songBox = (TextView) findViewById(R.id.songTextbox);

		// if the score hasn't changed, it returns 0
		int points = game.guess(songBox.getText().toString());
		updateUILabel(R.id.songTextbox, "");

		// if they got it right, skip to the next song
		if (points > 0) {
			// show the score!
			Toast toast = Toast
					.makeText(this, "+" + points, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();

			score += points;
			goToNextSong();
			updateUILabel(R.id.score, "Score: " + score);
		}

		// empty text box
		songBox.setText("");
		// update scores
		updateUILabel(R.id.streak, "Streak: " + game.getStreak());
		updateUILabel(R.id.multiplier, "Multiplier: " + game.getMultiplier());
	}

	/**
	 * Set the music player to go to the next song, if there are any left.
	 * Release the player if we're done.
	 */
	public void goToNextSong() {
		currentSong = game.getNextSong();

		// if we still have music to play
		if (currentSong != null && running) {
			try {

				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.reset();
				}

				File file = new File(currentSong.getPath());
				FileInputStream fis = new FileInputStream(file);
				mediaPlayer.setDataSource(fis.getFD());
				fis.close();

				// actually start!
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer.seekTo(currentSong.getRandomStart());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// we're done!
			gameOver();
		}
	}

	/**
	 * Initialize the timer loop thread. It keeps track of the game's timer,
	 * updating the timer label and then quitting the game when the timer runs
	 * out.
	 */
	private void initTimerThread() {
		Thread timerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				game.start();
				initSongTimerThread();

				// while we have time left
				while (running && game.timeLeft() > 0) {
					updateProgress(game.timeLeftString(), game.timeLeft());

					// to avoid updating too often, sleep for .2 secs
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				// we ran out of time
				gameOver();
			}
		});

		timerThread.start();
	}

	/**
	 * Initialize the song timer loop thread. It will keep track of each song's
	 * timer and skip when that runs out.
	 */
	private void initSongTimerThread() {
		Thread songTimerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// while we have songs left
				while (currentSong != null && running) {
					// once the timer runs out, skip
					if (currentSong.timeLeft() == 0) {
						goToNextSong();
					}
				}
			}
		});

		songTimerThread.start();
	}

	/**
	 * Updates a TextView to some text on the UI thread
	 * 
	 * @param id
	 *            the id of the TextView to update
	 * @param text
	 *            the text to set the TextView to
	 */
	private void updateUILabel(final int id, final String text) {

		// update UI on its own thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView = (TextView) findViewById(id);
				textView.setText(text);
			}
		});
	}

	/**
	 * Updates the progress bar and timer label in the UI thread
	 * 
	 * @param timeString
	 *            the amount of time left as a string (mm:ss)
	 * @param progress
	 *            the amount of time left in seconds
	 */
	private void updateProgress(final String timeString, final int progress) {
		// update UI on its own thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView timer = (TextView) findViewById(R.id.timer);
				timer.setText(timeString);
				ProgressBar timerBar = (ProgressBar) findViewById(R.id.scoreBar);
				timerBar.setProgress(progress);
			}
		});
	}

	/**
	 * Set the pause overlay screen visible or not on its own UI thread, as well
	 * as setting guessing box to be enabled or not (the opposite of visible
	 * variable)
	 * 
	 * @param visible
	 *            true for visible, false for invisible
	 */
	private void setPauseOverlay(final boolean visible) {
		// update UI on its own thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				View resumeView = findViewById(R.id.resumeOverlay);
				TextView songBox = (TextView) findViewById(R.id.songTextbox);
				songBox.setEnabled(!visible);

				if (visible) {
					resumeView.setVisibility(View.VISIBLE);
				} else {
					resumeView.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * Release the media player and go to the score screen
	 */
	private void gameOver() {
		running = false;
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}

		float[] stats = { 0, 0, 0, 0 };

		if (game != null) {
			stats = game.endGame();
		}

		// Go to the score screen
		Intent intent = new Intent(PlayActivity.this, GameScoreActivity.class)
				.putExtra("stats", stats);

		startActivity(intent);
	}

	/**
	 * Called when the player clicks on the Give Up button.
	 */
	public void giveUp(View view) {
		gameOver();
	}
}
