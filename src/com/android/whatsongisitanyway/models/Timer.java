package com.android.whatsongisitanyway.models;

public class Timer {
	private final int time;
	private double time_passed;
	private boolean done;
	private int score;

	public Timer(int time) {
		this.time = time;
		this.done = false;
		this.time_passed = 0;
		if (time == 10) {
			this.score = 50;
		} else {
			this.score = 0;
		}
	}

	/**
	 * Starts the timer. THIS CAN ONLY BE CALLED FROM INSIDE ANOTHER THREAD or
	 * you'll get into a loop until this finishes
	 * 
	 * @return whether or not this is done yet
	 */
	public boolean run() {
		double startTime = System.currentTimeMillis() / 1000.0;
		while (time - time_passed > 0) {
			double currentTime = (System.currentTimeMillis() / 1000.0);
			time_passed = currentTime - startTime;
			if (time == 10) {
				score = 50 - (int) (50 * (time_passed) / ((double) time));
			}
			done = false;
		}
		done = true;
		time_passed = (double) time;

		return done;
	}

	public boolean isDone() {
		return done;
	}

	public int getTimeLeft() {
		return (int) (this.getTime() - this.getTimePassed());
	}

	public int getTime() {
		return time;
	}

	public double getTimePassed() {
		return time_passed;
	}

	/**
	 * Pause the timer
	 */
	public void pause() {
		// TODO
	}

	/**
	 * Decrement the timer by a number of seconds (used for penalties)
	 * 
	 * @param seconds
	 *            seconds to decrement by
	 */
	public void decrement(int seconds) {
		// TODO
	}

	public int getScore() {
		return score;
	}

	public void reset() {
		time_passed = 0;
		done = false;
		if (time == 10) {
			this.score = 50;
		} else {
			this.score = 0;
		}
	}
}