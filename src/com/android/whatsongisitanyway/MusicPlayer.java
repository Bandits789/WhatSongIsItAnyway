package com.android.whatsongisitanyway;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.android.helloworld.R;

/**
 * Plays music from /res/raw (eventually sdcard)
 * 
 * 
 */
public class MusicPlayer {
	private List<Music> songsList;

	/**
	 * Creates a new MusicPlayer, grabs information about what songs are
	 * available to be played
	 */
	public MusicPlayer() {
		songsList = populateSongs();
	}

	/**
	 * Get the song information and populate the songsList
	 * 
	 * @return the list of Music objects
	 */
	private List<Music> populateSongs() {
		List<Music> songs = new ArrayList<Music>();

		Field[] fields = R.raw.class.getFields();

		// add the music object to the list
		for (int i = 0; i < fields.length; i++) {
			try { // to please eclipse
				Music music = new Music(fields[i].getInt(fields[i]));
				songs.add(music);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return songs;
	}

	/**
	 * Picks a specific number of songs randomly from the songs list
	 * 
	 * @param numSongs
	 *            number of desired songs, >= number of songs available
	 * @return list of size numSongs with songs randomly chosen from the songs
	 *         list
	 */
	protected List<Music> pickRandomSongs(int numSongs) {
		List<Music> randomSongs = new ArrayList<Music>();

		// add random songs until we have the right number
		while (randomSongs.size() < numSongs) {
			int rand = new Random().nextInt(songsList.size());
			Music song = songsList.get(rand);

			// make sure we don't add duplicates
			if (!songsList.contains(song)) {
				randomSongs.add(songsList.get(rand));
			}
		}

		return randomSongs;
	}
}
