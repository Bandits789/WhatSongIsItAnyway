package com.android.whatsongisitanyway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MusicTest {

	@Test
	public void IDTest() {
		Music song = new Music(42, "do i wanna know", "arctic monkeys", "180",
				"rock");
		assertEquals("do i wanna know", song.getID());

	}

	@Test
	public void playCountTest() {
		Music song = new Music(42, "do i wanna know", "arctic monkeys", "180",
				"rock");
		assertTrue(song.getPlayCount() == 0);
		song.playSong();
		song.playSong();
		assertTrue(song.getPlayCount() == 2);

		// times correct is initially 0
		assertTrue(song.getTimesCorrect() == 0);
		song.guessCorrectly(5);
		assertTrue(song.getAvgGuessTime() == 5.0);
		assertTrue(song.getTimesCorrect() == 1);

		song.playSong();
		assertTrue(song.getPlayCount() == 3);
		song.guessCorrectly(2);
		assertTrue(song.getAvgGuessTime() == 3.5);
	}
}
