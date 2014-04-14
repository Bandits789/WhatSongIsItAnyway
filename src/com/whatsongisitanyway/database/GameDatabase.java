package com.whatsongisitanyway.database;

import android.provider.BaseColumns;

/**
 * Defines all the tables and columns for the WSIIA database
 */
public final class GameDatabase {

	public GameDatabase() {
		// to prevent stupid things from happening
	}

	/**
	 * OverallData table. Has all the overall stats.
	 */
	public static abstract class OverallData implements BaseColumns {
		public static final String TABLE_NAME = "overalldata";
		public static final String COLUMN_NAME_GAMES_PLAYED = "gamesplayed";
		public static final String COLUMN_NAME_AVG_GUESS_TIME = "avgguesstime";
		public static final String COLUMN_NAME_ACCURACY = "accuracy";
		public static final String COLUMN_NAME_SONGS_PLAYED = "songsplayed";
	}

	/**
	 * GameData table. Has all the info for each game
	 */
	public static abstract class GameData implements BaseColumns {
		public static final String TABLE_NAME = "gamedata";
		public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
		public static final String COLUMN_NAME_AVG_GUESS_TIME = "avgguesstime";
		public static final String COLUMN_NAME_ACCURACY = "accuracy";
		public static final String COLUMN_NAME_SCORE = "score";
		public static final String COLUMN_NAME_SONGS_PLAYED = "songsplayed";
	}

	/**
	 * Settings table. Has all the user settings
	 */
	public static abstract class SettingsData implements BaseColumns {
		public static final String TABLE_NAME = "settings";
		public static final String COLUMN_NAME_GAME_DURATION = "gameduration";
		public static final String COLUMN_NAME_SONG_DURATION = "songduration";
	}
}
