package com.android.whatsongisitanyway.models;

/**
 * Class that represents a music object
 * 
 */
public class Music {
	private final int id;
	private final String duration; // TODO: random song chunks
	private final String title;
	private final String artist;
	private final String genre;

	private int playCount;
	private int timesCorrect;
	private float avgGuessTime;

	private final Timer timer; // TODO: do something with timer
	private final int playDuration = 5; // secs

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
		// TODO: something to do with timer
		return 1;
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
	 * @param guess
	 *            the string of the title guessed
	 * @return the score for the guess
	 */
	public int guess(String guess) {
		// TODO: fuzzy matching
		if (guess.equals(title)) {
			avgGuessTime = (float) ((avgGuessTime * timesCorrect + timer
					.getTimeLeft()) / (timesCorrect + 1.0));
			timesCorrect += 1;

			return getScore();
		}

		return 0;
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
	 * called when song is going to be played
	 */
	public void playSong() {
		playCount += 1;
		timer.run();
	}
}
