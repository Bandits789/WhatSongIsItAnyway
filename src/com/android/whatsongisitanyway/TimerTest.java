package com.android.whatsongisitanyway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimerTest {

	@Test
	public void basicTimerTest() {
		Timer timer = new Timer(10);
		assertTrue(timer.getTimeLeft() == 10);
		assertTrue(!timer.isDone());
		timer.run();
		assertEquals(timer.getTimeLeft(), 0); // WHY IS THIS 10
		assertTrue(timer.isDone());
		assertEquals(timer.getScore(), 0);

		timer.reset();
		assertTrue(timer.getTimeLeft() == 10);
		assertTrue(timer.getScore() == 50);
	}
}
