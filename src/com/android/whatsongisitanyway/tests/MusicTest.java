package com.android.whatsongisitanyway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.android.whatsongisitanyway.models.Music;

public class MusicTest {

	@Test
	public void songInfoTest() {
		Music song = new Music(42, "do i wanna know", 12345, 2, 3);
		assertEquals(42, song.getID());
		assertEquals("do i wanna know", song.getTitle());
		assertEquals(12345, song.getDuration());
		assertEquals(2, song.getArtist());
		assertEquals(3, song.getAlbum());
	}

	@Test
	public void playCountTest() {
		Music song = new Music(42, "do i wanna know", 12345, 2, 3);
		assertEquals(0, song.getPlayCount());
		song.playSong();
		song.playSong();
		assertEquals(2, song.getPlayCount());
	}

	@Test
	public void guessingTest() {
		Music song = new Music(42, "do i wanna know", 12345, 2, 3);

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
		Music song = new Music(42, "do i wanna know", 12345, 2, 3);

		// guess incorrectly
		assertEquals(0, song.guess("nope"));

		// now it's a function of time
		assertTrue(song.guess(song.getTitle()) > 0);
	}

	@Test
	public void matchingTest() {
		Music song = new Music(
				42,
				"3''4{52}2. D(O I WA)NNA K[noW FE]]At{UR}IN}G. SAFWERASDFWER'' [] ",
				12345, 1, 2);
		assertEquals(0, song.getTimesCorrect());
		song.guess("awerwafasdf");
		assertEquals(0, song.getTimesCorrect());
		song.guess("do i wanna know");
		assertEquals(1, song.getTimesCorrect());
		song.guess("do i want to know"); // check if fuzzy works
		assertEquals(2, song.getTimesCorrect());
		song.guess("DO I WANNA TO KNOW"); // close enough - wanna matches
		assertEquals(3, song.getTimesCorrect());
		song.guess("DO I WANT TO KNOW");
		assertEquals(4, song.getTimesCorrect()); // case insensitive
		song.guess("doi wnana know");
		assertEquals(5, song.getTimesCorrect());
	}
}
