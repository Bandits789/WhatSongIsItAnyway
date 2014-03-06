package com.android.whatsongisitanyway;

import android.R.integer;

/**
 * Represents a music object.
 * 
 * TODO: actually write this
 * 
 */
public class Music {
	private int id;
	private String name;
	private int playCount;
	private int timesCorrect;
	private float avgGuessTime;

	public Music(int id, String name) {
		this.id = id;
		this.name = name;
		this.playCount = playCount;
		this.timesCorrect = timesCorrect;
		this.avgGuessTime = avgGuessTime;
	}

	/**
	 * ID is the location of the music (currently R.raw.<something>)
	 * 
	 * @return the id of the music
	 */
	public int getID() {
		return id;
	}
	
	public int getPlayCount() {
		return playCount;
	}
	
	public void setPlayCount(int count) {
		this.playCount = count;
	}
	
	public int getTimesCorrect() {
		return timesCorrect;
	}
	
	public void setTimesCorrect(int count) {
		this.timesCorrect = count;
	}
	
	public float getAvgGuessTime() {
		return this.avgGuessTime;
	}
	
	public void setAvgGuessTime(float f) {
		this.avgGuessTime = f;
	}
	
	public void playSong() {
		this.setPlayCount(this.getPlayCount() + 1);
	}
	
	public void guessCorrectly(int time) {
		this.setAvgGuessTime((this.getAvgGuessTime() * this.getTimesCorrect() + time)/((float) (this.getTimesCorrect()+1)));
		this.setTimesCorrect(this.getTimesCorrect() + 1);
		
	}
	
}
