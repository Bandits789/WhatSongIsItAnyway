package com.android.whatsongisitanyway.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.android.whatsongisitanyway.models.Music;

public class MusicTest {

	@Test
	public void songInfoTest() {
		Music song = new Music("here", "do i wanna know", 12345,
				"arctic monkeys", "AM", 10000);
		assertEquals("here", song.getPath());
		assertEquals("do i wanna know", song.getTitle());
		assertEquals(12345, song.getDuration());
		assertEquals("arctic monkeys", song.getArtist());
		assertEquals("AM", song.getAlbum());
		assertEquals(10000, song.getSize());
	}

	@Test
	public void scoreTest() {

		Music song = new Music("here", "do i wanna know", 12345,
				"arctic monkeys", "AM", 10000);

		// guess incorrectly
		assertEquals(0, song.guess("nope"));

		// now it's a function of time
		assertTrue(song.guess(song.getTitle()) > 0);
	}

	@Test
	public void matchingTest() {
		Music song = new Music(
				"here",
				"3''4{52}2. D(O I WA)NNA K[noW FE]]At{UR}IN}G. SAFWERASDFWER'' [] ",
				12345, "arctic monkeys", "AM", 10000);

		assertEquals(0, song.guess("awerwafasdf"));
		// case insensitive
		assertTrue(song.guess("DO I WANNA TO KNOW") > 0);
		// close enough - wanna matches
		assertTrue(song.guess("DO I WANT TO KNOW") > 0);
		assertTrue(song.guess("doi wnana know") > 0);
	}
}
