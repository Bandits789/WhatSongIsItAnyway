package com.android.whatsongisitanyway;

import android.app.Activity;

public class Timer extends Activity {
	private int time;
	private int time_passed;
	private boolean done;

	public Timer(int time) {
		this.time = time;
		this.done = false;
		this.time_passed = 0;
	}

	public boolean run() {
		int startTime = (int) (System.currentTimeMillis() / 1000);
		int time_passed = 0;
		while (time - time_passed > 0) {
			int currentTime = (int) (System.currentTimeMillis() / 1000);
			time_passed = currentTime - startTime;
			done = false;
		}
		this.done = true;
		return this.done;
	}

	public boolean isDone() {
		return done;
	}

	public int getTime() {
		return time - time_passed;
	}

}