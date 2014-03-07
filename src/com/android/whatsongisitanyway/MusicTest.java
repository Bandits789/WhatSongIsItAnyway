package com.android.whatsongisitanyway;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MusicTest {

	Music song = new Music(42, "do i wanna know");

	@Test
	public void IDTest() {
		assertEquals("do i wanna know", song.getID());

	}

}
