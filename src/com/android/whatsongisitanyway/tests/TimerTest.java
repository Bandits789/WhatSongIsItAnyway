package com.android.whatsongisitanyway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.android.whatsongisitanyway.models.Timer;

/**
 * Tests the timer running, pausing, and decrementing time. Warning: these tests
 * take a while
 */
public class TimerTest {

	@Test
	public void basicTimerTest() {
		Timer timer = new Timer(10);
		assertEquals(10, timer.getTimeLeft());
		timer.run();

		// sleep for 1 second
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// could be 8 or 9, depending on timing
		assertTrue(timer.getTimeLeft() <= 9);
	}

	@Test
	public void pauseTest() {
		Timer timer = new Timer(10);
		timer.run();
		timer.pause();

		// we run this for some time (getTimeLeft needs to be called in order
		// for pause to take effect)
		for (int i = 0; i < 2000; ++i) {
			timer.getTimeLeft();
		}

		timer.resume();
		// will be 9 or 10 depending on timing
		assertTrue(timer.getTimeLeft() >= 9);
	}

	@Test
	public void decrementTest() {
		Timer timer = new Timer(10);
		timer.run();
		timer.decrement(2);
		// this could be 7 or 8, depending on how long this took
		assertTrue(timer.getTimeLeft() <= 8);

		// decrement to zero
		timer.decrement(8);
		assertEquals(0, timer.getTimeLeft());

		// should still say zero (not -2)
		timer.decrement(2);
		assertEquals(0, timer.getTimeLeft());
	}
}
