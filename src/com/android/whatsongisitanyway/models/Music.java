package com.android.whatsongisitanyway.models;

import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

/**
 * Class that represents a music object. Has guessing, skipping, and scoring
 * functionality. Has a timer for each song.
 */
public class Music {
	private final String path;
	private final String title;
	private final int duration; // milliseconds
	private final String artist;
	private final String album;
	private final int size;

	private final Timer timer;
	private int timeGuessedIn;
	private final int playDuration; // secs
	// don't play this many milliseconds from the end
	private final int msecondsFromEnd = 30 * 1000;

	/**
	 * Make a new Music object
	 * 
	 * @param id
	 *            the path to the song location
	 * @param title
	 *            title of the song
	 * @param duration
	 *            in milliseconds
	 * @param artist
	 *            artist of the song (could be unknown)
	 * @param album
	 *            album of the song (could be unknown)
	 * @param size
	 *            size of the file, in bytes
	 * @param playDuration
	 *            duration of the song clip (in seconds)
	 */
	public Music(String id, String title, int duration, String artist,
			String album, int size, int playDuration) {
		this.path = id;
		this.title = title;
		this.duration = duration;
		this.artist = artist;
		this.album = album;
		this.size = size;
		this.playDuration = playDuration;

		timeGuessedIn = playDuration;
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
	 * Gets the path to the location of the music
	 * 
	 * @return the path to the music
	 */
	public String getPath() {
		return path;
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

		String cleanedTitle = cleanTitle();
		guess = guess.toLowerCase(Locale.getDefault());
		double accuracy = StringUtils.getJaroWinklerDistance(guess,
				cleanedTitle);

		if (accuracy > 0.8) {
			timeGuessedIn = playDuration - timer.getTimeLeft();
			return getScore();
		}

		return 0;
	}

	/**
	 * Cleans the song title into something easier to type (ie. without feat...
	 * or song track numbers)
	 * 
	 * @return the cleaned song title
	 */
	private String cleanTitle() {

		// Step one: take out all parens and brackets and '
		String result = title.replaceAll("[\\[\\(\\{\\}\\)\\]\\']", "");

		// If title starts with a number followed by a period, like "2.", take
		// it out.

		if (Character.isDigit(result.charAt(0))) {
			result = result.replaceAll("[0-9]+\\.", "");
			result = result.trim();
		}

		result = result.toLowerCase(Locale.getDefault());

		int feat = result.indexOf("feat");
		int ft = result.indexOf("ft");

		if (feat != -1) {
			result = result.substring(0, feat);
			result = result.trim();
		}

		if (ft != -1) {
			result = result.substring(0, ft);
			result = result.trim();
		}

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
	 * Gets the music duration
	 * 
	 * @return the duration of the music
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Gets the music artist
	 * 
	 * @return the artist of the song
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * Gets the album
	 * 
	 * @return the album of the song
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * Gets the size of the song in bytes
	 * 
	 * @return the size of the song
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Starts the song's timer and increments the song play count, should be
	 * called when song is going to be played.
	 */
	public void playSong() {
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
	 * Gets the time the song was guessed in (or the total duration of the clip
	 * if the song wasn't guessed correctly)
	 * 
	 * @return the time the song was guessed in
	 */
	public int timeGuessedIn() {
		return timeGuessedIn;
	}

	/**
	 * Returns a randomized start time for the song.
	 * 
	 * @return the random start time for the song in milliseconds
	 */
	public int getRandomStart() {
		// this is the range we can select times from
		int selectDuration = duration - msecondsFromEnd - (playDuration * 1000);
		int randomStart = new Random().nextInt(selectDuration);

		return randomStart;
	}
}
