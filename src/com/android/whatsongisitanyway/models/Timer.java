package com.android.whatsongisitanyway.models;

public class Timer {
	private final int time;
	private double time_passed;
	private boolean done;
	private Thread timerThread = null;

	public Timer(int time) {
		this.time = time;
		this.done = false;
		this.time_passed = 0;
	}

	/**
	 * Starts the timer inside the timerThread
	 */
	public void run() {
		// THIS MUST ALWAYS BE IN A THREAD
		// otherwise, it won't return until time is up
		timerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				double startTime = System.currentTimeMillis() / 1000.0;
				while (time - time_passed > 0) {
					double currentTime = (System.currentTimeMillis() / 1000.0);
					time_passed = currentTime - startTime;
					done = false;
				}
				done = true;
				time_passed = (double) time;
			}
		});

		timerThread.start();
	}

	public boolean isDone() {
		return done;
	}

	public int getTimeLeft() {
		return (int) (getTime() - getTimePassed());
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
		// TODO: make sure to pause inside the thread
	}

	/**
	 * Decrement the timer by a number of seconds (used for penalties)
	 * 
	 * @param seconds
	 *            seconds to decrement by
	 */
	public void decrement(int seconds) {
		// TODO: also make sure inside thread
	}

	public void reset() {
		time_passed = 0;
		done = false;
	}

	/**
	 * Stops the timer
	 */
	public void stop() {
		time_passed = time;
	}
}