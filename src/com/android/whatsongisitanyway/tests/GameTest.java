package com.android.whatsongisitanyway.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.android.whatsongisitanyway.models.Game;
import com.android.whatsongisitanyway.models.Music;

/**
 * Tests getting next song, guessing, and skipping songs. Timer stuff left to
 * TimerTest.
 * 
 * This only works if you comment out the call to the database in Game (Context
 * is null and the database doesn't like it)
 */
public class GameTest {
	
	@Test
	public void getNextSongTest() {
		Music song = new Music("here", "do i wanna know", 12345,
				"arctic monkeys", "AM", 10000, 10);
		Game game = new Game(Arrays.asList(song), null, 60);
		assertEquals("do i wanna know", game.getNextSong().getTitle());
	}

	@Test
	public void guessingTest() {
		Music song = new Music("here", "do i wanna know", 12345,
				"arctic monkeys", "AM", 10000, 10);
		Game game = new Game(Arrays.asList(song), null, 60);
		game.getNextSong();

		// starting out
		assertEquals(1, game.getMultiplier());
		assertEquals(0, game.getStreak());

		// guess correctly
		game.guess(song.getTitle());
		assertEquals(1, game.getStreak());

		// guess correctly
		game.guess(song.getTitle());
		assertEquals(2, game.getMultiplier());
		assertEquals(2, game.getStreak());

		// guess wrong
		game.guess("no");
		assertEquals(1, game.getMultiplier());
		assertEquals(0, game.getStreak());
	}

	@Test
	public void skippingTest() {
		Music song = new Music("here", "do i wanna know", 12345,
				"arctic monkeys", "AM", 10000, 10);
		Game game = new Game(Arrays.asList(song), null, 60);
		game.getNextSong();

		// starting out
		assertEquals(1, game.getMultiplier());
		assertEquals(0, game.getStreak());

		// guess correctly
		game.guess(song.getTitle());
		assertEquals(1, game.getStreak());

		// skip
		game.skipPenalty();
		assertEquals(1, game.getMultiplier());
		assertEquals(0, game.getStreak());
	}
}
