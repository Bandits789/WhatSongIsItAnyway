package com.android.helloworld;

import java.util.List;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	MusicPlayer musicPlayer;
	List<Music> musicFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		musicPlayer = new MusicPlayer();
		musicFiles = musicPlayer.pickRandomSongs(4);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Plays music
	 */
	public void play(View view) {
		for (Music song : musicFiles) {
			MediaPlayer mediaPlayer = MediaPlayer.create(view.getContext(),
					song.getID());
			mediaPlayer.start();
		}
	}

}
