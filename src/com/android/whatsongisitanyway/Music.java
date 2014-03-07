package com.android.whatsongisitanyway;

/**
 * Represents a music object.
 * 
 * TODO: actually write this
 * 
 */
public class Music {
	private final int id;
	private final String duration; 
	private final String title;
	private final String artist;
	private final String genre; 


	public Music(int id, String title, String artist, String duration, String genre) { 
	    this.id = id; 
	    this.title = title;
	    this.artist = artist;
	    this.duration = duration; 
	    this.genre = genre; 
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
	 * Gets the music title
	 * @return the name of the music 
	 */
	public String getTitle() { 
	    return this.title; 
	}
	
	/**
	 * Gets the music artist 
	 * @return the name of the artist
	 */
	public String getArtist() { 
	    return this.artist; 
	}
	
	/** 
	 * Gets the music genre
	 * @return the genre of the music 
	 */
	public String getGenre() { 
	    return this.genre; 
	}
}
