package com.android.whatsongisitanyway;

/**
 * Represents a music object.
 * 
 * TODO: actually write this
 * 
 */
public class Music {
	private int id;
	private String name;

	public Music(int id) {
		this.id = id;
	}

	/**
	 * ID is the location of the music (currently R.raw.<something>)
	 * 
	 * @return the id of the music
	 */
	public int getID() {
		return id;
	}

}
