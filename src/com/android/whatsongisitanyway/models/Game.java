package com.android.whatsongisitanyway.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;

import com.android.whatsongisitanyway.database.GameDatabaseHelper;

/**
 * ADT that represents a single game of WSIIA. Stores the song list, and has
 * skipping, guessing, and scoring functionality. Has a timer for the whole
 * game.
 */
public class Game {
	private final List<Music> songsList;
	private int currentSongIndex;
	private Music currentSong = null;
	private final Timer timer;

	private final GameDatabaseHelper dbHelper;
	private int songsCorrect;
	private int totalGuessTime;

	// in seconds
	private final int duration = 30;
	private final int skipPenalty = 2;

	private int multiplier = 1;
	private int streak = 0;
	private int score;

	/**
	 * Creates a new Game, grabs information about what songs are available to
	 * be played
	 * 
	 * @param songsList
	 *            the list of Music objects from the sdcard
	 * @param context
	 *            the Context the game is in
	 */
	public Game(List<Music> songsList, Context context) {
		currentSongIndex = -1;
		timer = new Timer(duration);
		dbHelper = new GameDatabaseHelper(context);

		this.songsList = new ArrayList<Music>(songsList);
		// shuffle songs
		long seed = System.nanoTime();
		Collections.shuffle(this.songsList, new Random(seed));

		songsCorrect = 0;
		totalGuessTime = 0;
	}

	/**
	 * Finds the next Music object to play, or null if we have reached the end
	 * 
	 * @return the next Music to play or null
	 */
	public Music getNextSong() {
		// we're going to the next song
		++currentSongIndex;

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
			totalGuessTime += currentSong.timeGuessedIn();
			++songsCorrect;

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

	/**
	 * End the game, write the recent game and overall stats into the database
	 */
	public void endGame() {
		// so database. very sql. wow.
		dbHelper.insertGameStats(score, totalGuessTime, songsCorrect,
				currentSongIndex + 1);
		dbHelper.updateOverallStats(score, totalGuessTime, songsCorrect,
				currentSongIndex + 1);
	}
}
