package com.android.whatsongisitanyway.models;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

/**
 * Class that represents a music object
 * 
 */
public class Music {
	private final int id;
	private final String duration;
	private final String title;
	private final String artist;
	private final String genre;

	private int playCount;
	private int timesCorrect;
	private float avgGuessTime;

	private final Timer timer;
	private final int playDuration = 5; // secs
	// don't play this many seconds from the end
	private final int secondsFromEnd = 30;

	public Music(int id, String title, String artist, String duration,
			String genre) {
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.duration = duration;
		this.genre = genre;

		timer = new Timer(playDuration);
	}

	/**
	 * Calculates the score based off the timer
	 * 
	 * @return score for the song
	 */
	private int getScore() {
		return timer.getTimeLeft() * 5;
	}

	/**
	 * ID is the location of the music (currently R.raw.<something>)
	 * 
	 * @return the id of the music
	 */
	public int getID() {
		return id;
	}

	/**
	 * Guess a song title, get the points for the guess (0 if wrong)
	 * 
	 * Uses Apache commons Lang library and the Jaro Winkler Distance algorithm
	 * for fuzzy matching
	 * 
	 * @param guess
	 *            the string of the title guessed
	 * @return the score for the guess
	 */
	public int guess(String guess) {
		// TODO: fuzzy matching

		double distance = StringUtils.getJaroWinklerDistance("", "");

		if (guess.equals(title)) {
			avgGuessTime = (float) ((avgGuessTime * timesCorrect + timer
					.getTimeLeft()) / (timesCorrect + 1.0));
			timesCorrect += 1;

			return getScore();
		}
		return 0;
	}

	public String cleanTitle() {
		String result = "";
		String noParens = title.replaceAll("[()]", "");

		return result;
	}

	/**
	 * Gets the music title
	 * 
	 * @return the name of the music
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the music artist
	 * 
	 * @return the name of the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * Gets the music genre
	 * 
	 * @return the genre of the music
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Gets total play count
	 * 
	 * @return play count
	 */
	public int getPlayCount() {
		return playCount;
	}

	/**
	 * Gets number of times the user guessed the song correctly
	 * 
	 * @return times guess correctly
	 */
	public int getTimesCorrect() {
		return timesCorrect;
	}

	/**
	 * Gets the average time it took the user to correctly guess the song
	 * 
	 * @return average time to correctly guess song
	 */
	public float getAvgGuessTime() {
		return avgGuessTime;
	}

	/**
	 * Starts the song's timer and increments the song play count, should be
	 * called when song is going to be played.
	 */
	public void playSong() {
		playCount += 1;
		timer.run();
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
	 * Pause the song and the timer
	 */
	public void pause() {
		timer.pause();
	}

	/**
	 * Resume the song and the timer
	 */
	public void resume() {
		timer.resume();
	}

	/**
	 * Returns a randomized start time for the song.
	 * 
	 * @return the random start time for the song in *milliseconds*
	 */
	public int getRandomStart() {
		// this is the range we can select times from
		int durationInt = 100; // TODO: get ints not Strings
		int selectDuration = durationInt - secondsFromEnd - playDuration;
		int randomStart = new Random().nextInt(selectDuration);

		return randomStart * 1000;
	}
}
