package com.android.whatsongisitanyway;

import java.io.FileDescriptor;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.helloworld.R;

public class MainActivity extends Activity {
	Game game;
	MediaPlayer mediaPlayer = null;

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
	public void play(View view) {
		if (mediaPlayer == null) {
			startPlayer(view);
		} else {
			goToNextSong();
		}
	}

	/**
	 * Starts up the mediaPlayer and starts playing the first song (if not
	 * stopped or skipped, will keep playing songs until done)
	 * 
	 * @param view
	 */
	public void startPlayer(View view) {
		game = new Game();
		TextView textView = (TextView) findViewById(R.id.title);

		// we can only skip from now on
		Button button = (Button) findViewById(R.id.playButton);
		button.setText("Skip");

		// start first song
		Music firstSong = game.getNextSong();
		textView.setText(firstSong.getID());
		mediaPlayer = MediaPlayer.create(view.getContext(), firstSong.getID());

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
	}

	/**
	 * Set the music player to go to the next song, if there are any left.
	 * Release the player if we're done.
	 * 
	 * @param mediaPlayer
	 *            the music player in question
	 */
	public void goToNextSong() {
		Music nextSong = game.getNextSong();
		TextView textView = (TextView) findViewById(R.id.title);

		// if we still have music to play
		if (nextSong != null) {
			try {
				// set the new title
				textView.setText(nextSong.getID());
				mediaPlayer.stop();
				mediaPlayer.reset();

				// get the music file
				 FileDescriptor fd = getResources().openRawResourceFd(
						nextSong.getID()).getFileDescriptor();
//				FileInputStream fdstream = res
//						.openRawResource(nextSong.getID());
//				FileDescriptor fd = fdstream.getFD();
				mediaPlayer.setDataSource(fd);
				Log.d("fielsdir", getFilesDir() + "");

				Log.d("media player", fd + "");

				// play it!
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// we're done!
			// TODO: do something to alert user here...
			mediaPlayer.release();

			// so user can't skip
			mediaPlayer = null;
			Button button = (Button) findViewById(R.id.playButton);
			button.setText("Play");
			textView.setText("What Song Is It Anyway?");
		}
	}

}
