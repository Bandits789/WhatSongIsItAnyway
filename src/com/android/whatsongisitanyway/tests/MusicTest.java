package com.android.whatsongisitanyway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.android.whatsongisitanyway.models.Music;

public class MusicTest {

	@Test
	public void songInfoTest() {
		Music song = new Music(42, "do i wanna know", "arctic monkeys", "180",
				"rock");
		assertTrue(42 == song.getID());
		assertEquals("do i wanna know", song.getTitle());
		assertEquals("arctic monkeys", song.getArtist());
		assertEquals("rock", song.getGenre());
	}

	@Test
	public void playCountTest() {
		Music song = new Music(42, "do i wanna know", "arctic monkeys", "180",
				"rock");
		assertEquals(0, song.getPlayCount());
		song.playSong();
		song.playSong();
		assertEquals(2, song.getPlayCount());
	}

	@Test
	public void guessingTest() {
		Music song = new Music(42, "do i wanna know", "arctic monkeys", "180",
				"rock");

		// times correct is initially 0
		assertEquals(0, song.getTimesCorrect());
		song.guess(song.getTitle());
		assertEquals(1, song.getTimesCorrect());

		// guess incorrectly
		song.guess("nope");
		assertEquals(1, song.getTimesCorrect());
	}

	@Test
	public void scoreTest() {
		Music song = new Music(42, "do i wanna know", "arctic monkeys", "180",
				"rock");

		// guess incorrectly
		assertEquals(0, song.guess("nope"));

		// now it's a function of time
		assertTrue(song.guess(song.getTitle()) > 0);
	}
}
