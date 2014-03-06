package com.android.whatsongisitanyway;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.android.helloworld.R;

/**
 * ADT that represents a single game of WSIIA. Has the songs list, and can
 * switch to the next song. Plays music from /res/raw (eventually sdcard)
 * 
 */
public class Game {
	private List<Music> songsList;
	private int currentSongIndex;

	/**
	 * Creates a new Game, grabs information about what songs are available to
	 * be played
	 */
	public Game() {
		currentSongIndex = -1;
		songsList = populateSongs();
	}

	/**
	 * Get the song information and populate the songsList (in random order)
	 * 
	 * @return the list of Music objects
	 */
	private List<Music> populateSongs() {
		List<Music> songs = new ArrayList<Music>();

		Field[] fields = R.raw.class.getFields();

		// add the music object to the list
		for (int i = 0; i < fields.length; i++) {
			try { // to please eclipse
				Music music = new Music(fields[i].getInt(fields[i]),
						"songTitle"); // TODO: find song title
				songs.add(music);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// randomize!
		long seed = System.nanoTime();
		Collections.shuffle(songs, new Random(seed));

		return songs;
	}

	/**
	 * Finds the next Music object to play, or null if we have reached the end
	 * 
	 * @return the next Music to play or null
	 */
	public Music getNextSong() {
		// we're going to the next song
		++currentSongIndex;
		Log.d("index", currentSongIndex + "");

		// make sure there are still songs available
		if (currentSongIndex >= songsList.size()) {
			return null;
		}

		return songsList.get(currentSongIndex);
	}

}
