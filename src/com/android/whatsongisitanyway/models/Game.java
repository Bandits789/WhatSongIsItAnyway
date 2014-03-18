package com.android.whatsongisitanyway.models;

import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.android.whatsongisitanyway.R;

/**
 * ADT that represents a single game of WSIIA. Has the songs list, and can
 * switch to the next song. Plays music from /res/raw (eventually sdcard)
 * 
 */
public class Game {
	private final List<Music> songsList;
	private int currentSongIndex;
	private Music currentSong = null;
	private final Timer timer;
	private final Resources res;

	// in seconds
	private final int duration = 30;
	private final int skipPenalty = 2;

	private int multiplier = 1;
	private int streak = 0;
	private int score;

	/**
	 * Creates a new Game, grabs information about what songs are available to
	 * be played
	 */
	public Game(Resources resources) {
		currentSongIndex = -1;
		res = resources;
		songsList = populateSongs();
		timer = new Timer(duration);
	}

	/**
	 * Get the song information and populate the songsList (in random order)
	 * 
	 * @return the list of Music objects
	 */

	private List<Music> populateSongs() {

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		List<Music> songs = new ArrayList<Music>();

		Field[] fields = R.raw.class.getFields();

		// add the music object to the list
		// and add song metadata to the music object
		for (int i = 0; i < fields.length; i++) {
			try { // to please eclipse

				// Dummy initializing for now
				String title = "0";
				String artist = "0";
				String genre = "0";
				String duration = "0";

				int musicID = fields[i].getInt(fields[i]);
				Log.d("musicID", "id: " + musicID);
				FileDescriptor fd = res.openRawResourceFd(musicID)
						.getFileDescriptor();
				retriever.setDataSource(fd);
				//
				// // Get song metadata and store
				// title =
				// retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
				//
				// artist =
				// retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
				//
				// genre =
				// retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
				//
				// duration =
				// retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				//
				Music music = new Music(musicID, title, artist, duration, genre);

				Log.d(" Title:", title + " Artist:" + artist + " Genre:"
						+ genre + " Duration:" + duration);

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
		Log.d(" Next song", "index:" + currentSongIndex);

		// make sure there are still songs available
		if (currentSongIndex >= songsList.size()) {
			return null;
		}

		currentSong = songsList.get(currentSongIndex);
		// start the timer
		currentSong.playSong();

		return currentSong;
	}

	/**
	 * Decrement the timer by skipPenalty number of seconds
	 */
	public void skipPenalty() {
		timer.decrement(skipPenalty);
		multiplier = 1;
		streak = 0;
	}

	/**
	 * Guess a song title, get the points for the guess (0 if wrong)
	 * 
	 * @param guess
	 *            the string of the title guessed
	 * @return the score for the guess
	 */
	public int guess(String guess) {
		int points = currentSong.guess(guess) * multiplier;
		score += points;

		// they guessed correctly
		if (points > 0) {
			streak += 1;
			// up the multiplier every 2 correct songs
			if (streak % 2 == 0) {
				multiplier += 1;
			}
			return score;
		}

		// they guessed wrong
		multiplier = 1;
		streak = 0;
		return 0;
	}

	/**
	 * Start game and timer
	 */
	public void start() {
		timer.run();
	}

	/**
	 * Pause the game and the timer
	 */
	public void pause() {
		timer.pause();
		currentSong.pause();
	}

	/**
	 * Resume the game and the timer
	 */
	public void resume() {
		timer.resume();
		currentSong.resume();
	}

	/**
	 * Return the amount of time left on the timer
	 * 
	 * @return time left in seconds
	 */
	public int timeLeft() {
		return timer.getTimeLeft();
	}

	/**
	 * Return the amount of time left on the timer in mm:ss format (unsupported
	 * for over 59:59)
	 * 
	 * @return time left string
	 */
	public String timeLeftString() {
		String time = timer.getTimeLeft() + "";
		int length = time.length();

		if (time.length() > 2) {
			// mm:ss
			time = time.substring(0, time.length() - 2) + ":"
					+ time.substring(length - 1, length);
		} else if (time.length() == 2) {
			// 0:ss
			time = "0:" + time;
		} else {
			// 0:0s
			time = "0:0" + time;
		}

		return time;
	}

	/**
	 * Return the score multiplier
	 * 
	 * @return multiplier
	 */
	public int getMultiplier() {
		return multiplier;
	}

	/**
	 * Return the number of songs the user got in a row
	 * 
	 * @return streak
	 */
	public int getStreak() {
		return streak;
	}

}
