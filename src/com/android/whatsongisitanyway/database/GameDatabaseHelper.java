package com.android.whatsongisitanyway.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.whatsongisitanyway.database.GameDatabase.GameData;
import com.android.whatsongisitanyway.database.GameDatabase.OverallData;
import com.android.whatsongisitanyway.database.GameDatabase.SettingsData;

/**
 * Bridges the gap between code and sqlite, helps with important things such as
 * creation and deletion of the database, and updating the game and stat info
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "WSIIAData.db";

	/**
	 * Makes a database helper
	 * 
	 * @param context
	 *            the context the game is in
	 */
	public GameDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// first create the games table
		String createGames = "CREATE TABLE " + GameData.TABLE_NAME + " ("
				+ GameData._ID + " INTEGER PRIMARY KEY,"
				+ GameData.COLUMN_NAME_TIMESTAMP
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ GameData.COLUMN_NAME_ACCURACY + " FLOAT,"
				+ GameData.COLUMN_NAME_AVG_GUESS_TIME + " FLOAT,"
				+ GameData.COLUMN_NAME_SONGS_PLAYED + " INTEGER,"
				+ GameData.COLUMN_NAME_SCORE + " INTEGER)";

		// now create the overall stats table
		String createOverall = "CREATE TABLE " + OverallData.TABLE_NAME + " ("
				+ OverallData._ID + " INTEGER PRIMARY KEY,"
				+ OverallData.COLUMN_NAME_ACCURACY + " FLOAT,"
				+ OverallData.COLUMN_NAME_AVG_GUESS_TIME + " FLOAT,"
				+ OverallData.COLUMN_NAME_GAMES_PLAYED + " INTEGER,"
				+ OverallData.COLUMN_NAME_SONGS_PLAYED + " INTEGER)";

		// and then settings table
		String createSettings = "CREATE TABLE " + SettingsData.TABLE_NAME
				+ " (" + SettingsData._ID + " INTEGER PRIMARY KEY,"
				+ SettingsData.COLUMN_NAME_GAME_DURATION + " INTEGER,"
				+ SettingsData.COLUMN_NAME_SONG_DURATION + " INTEGER)";

		db.execSQL(createGames);
		db.execSQL(createOverall);
		db.execSQL(createSettings);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for data, so its upgrade policy
		// is to simply to discard the data and startgs over
		db.execSQL("DROP TABLE IF EXISTS " + OverallData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GameData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SettingsData.TABLE_NAME);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	/**
	 * Update the settings
	 * 
	 * @param gameDuration
	 *            how long should the game play for (seconds)
	 * @param songDuration
	 *            how long should the songs play for (seconds)
	 */
	public void updateSettings(int gameDuration, int songDuration) {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { SettingsData._ID };

		Cursor cursor = db.query(SettingsData.TABLE_NAME, projection, null,
				null, null, null, null);

		// get all the values
		cursor.moveToFirst();

		// get id
		float id = cursor.getInt(0);

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(SettingsData.COLUMN_NAME_GAME_DURATION, gameDuration);
		values.put(SettingsData.COLUMN_NAME_SONG_DURATION, songDuration);

		String selection = SettingsData._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };

		// update!
		int rows = db.update(SettingsData.TABLE_NAME, values, selection,
				selectionArgs);
		Log.d("rows affected with settings update", rows + "");
	}

	/**
	 * Inserts the stats of the most recently played game into the database
	 * 
	 * @param score
	 *            score of the most recent game
	 * @param averageGuessTime
	 *            average amount of time to guess the song
	 * @param accuracy
	 *            accuracy of guessing in the most recent game
	 * @param songsPlayed
	 *            songs played until end of most recent game
	 */
	public void insertGameStats(int score, float averageGuessTime,
			float accuracy, int songsPlayed) {
		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(GameData.COLUMN_NAME_SCORE, score);
		values.put(GameData.COLUMN_NAME_AVG_GUESS_TIME, averageGuessTime);
		values.put(GameData.COLUMN_NAME_ACCURACY, accuracy);
		values.put(GameData.COLUMN_NAME_SONGS_PLAYED, songsPlayed);

		// insert the new row!
		db.insert(GameData.TABLE_NAME, null, values);
	}

	/**
	 * Update the overall stats in the database
	 * 
	 * @param score
	 *            score of the most recent game
	 * @param averageGuessTime
	 *            average amount of time to guess the song
	 * @param accuracy
	 *            accuracy of guessing in the most recent game
	 * @param songsPlayed
	 *            songs played until end of most recent game
	 */
	public void updateOverallStats(int score, float averageGuessTime,
			float accuracy, int songsPlayed) {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { OverallData._ID,
				OverallData.COLUMN_NAME_ACCURACY,
				OverallData.COLUMN_NAME_AVG_GUESS_TIME,
				OverallData.COLUMN_NAME_GAMES_PLAYED,
				OverallData.COLUMN_NAME_SONGS_PLAYED };

		Cursor cursor = db.query(OverallData.TABLE_NAME, projection, null,
				null, null, null, null);

		// get all the values
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			Log.d("stats first time", "yes");
			initialOverallStats(score, averageGuessTime, accuracy, songsPlayed);
			return;
		}
		float id = cursor.getInt(0);
		float oldAccuracy = cursor.getFloat(1);
		float oldAvgGuessTime = cursor.getFloat(2);
		int oldGamesPlayed = cursor.getInt(3);
		int oldSongsPlayed = cursor.getInt(4);

		// find new values
		int newSongsPlayed = oldSongsPlayed + songsPlayed;
		float newAccuracy = (oldAccuracy * oldSongsPlayed + accuracy
				* songsPlayed)
				/ (float) newSongsPlayed;
		float newAvgGuessTime = (float) (oldAvgGuessTime * oldGamesPlayed + averageGuessTime
				* songsPlayed)
				/ (float) newSongsPlayed;

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(OverallData.COLUMN_NAME_ACCURACY, newAccuracy);
		values.put(OverallData.COLUMN_NAME_AVG_GUESS_TIME, newAvgGuessTime);
		values.put(OverallData.COLUMN_NAME_GAMES_PLAYED, oldGamesPlayed + 1);
		values.put(OverallData.COLUMN_NAME_SONGS_PLAYED, newSongsPlayed);

		String selection = OverallData._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };

		// update!
		int rows = db.update(OverallData.TABLE_NAME, values, selection,
				selectionArgs);
		Log.d("rows affected with stats update", rows + "");
	}

	/**
	 * Initially when there are no overall stats, insert the first game stats
	 * into overall
	 * 
	 * @param score
	 *            score of the most recent game
	 * @param averageGuessTime
	 *            average amount of time to guess the song
	 * @param accuracy
	 *            accuracy of guessing in the most recent game
	 * @param songsPlayed
	 *            songs played until end of most recent game
	 */
	private void initialOverallStats(int score, float averageGuessTime,
			float accuracy, int songsPlayed) {
		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(OverallData.COLUMN_NAME_ACCURACY, accuracy);
		values.put(OverallData.COLUMN_NAME_AVG_GUESS_TIME, averageGuessTime);
		values.put(OverallData.COLUMN_NAME_GAMES_PLAYED, 1);
		values.put(OverallData.COLUMN_NAME_SONGS_PLAYED, songsPlayed);

		// insert the new row!
		db.insert(OverallData.TABLE_NAME, null, values);
	}

	/**
	 * Initially when there are no settings, insert the default settings (2mins
	 * for game, 10 secs for song)
	 */
	private void initialSettings() {
		Log.d("settings first time", "yes");
		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(SettingsData.COLUMN_NAME_GAME_DURATION, 2 * 60);
		values.put(SettingsData.COLUMN_NAME_SONG_DURATION, 10);

		// insert the new row!
		db.insert(SettingsData.TABLE_NAME, null, values);
	}

	/**
	 * Gets the overall stats in a float array
	 * 
	 * @return a float array of accuracy, avgGuessTime, gamesPlayed, songsPlayed
	 */
	public float[] getOverallStats() {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { OverallData.COLUMN_NAME_ACCURACY,
				OverallData.COLUMN_NAME_AVG_GUESS_TIME,
				OverallData.COLUMN_NAME_GAMES_PLAYED,
				OverallData.COLUMN_NAME_SONGS_PLAYED };

		Cursor cursor = db.query(OverallData.TABLE_NAME, projection, null,
				null, null, null, null);

		// get all the values
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			// nothing to show yet!
			float[] nothing = { 0, 0, 0, 0 };
			return nothing;
		}

		float accuracy = cursor.getFloat(0);
		float avgGuessTime = cursor.getFloat(1);
		int gamesPlayed = cursor.getInt(2);
		int songsPlayed = cursor.getInt(3);

		float[] stats = { accuracy, avgGuessTime, gamesPlayed, songsPlayed };

		return stats;
	}

	/**
	 * Gets the settings, set them to default if the table doesn't exist
	 * 
	 * @return an int array of gameDuration, songDuration
	 */
	public int[] getSettings() {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { SettingsData.COLUMN_NAME_GAME_DURATION,
				SettingsData.COLUMN_NAME_SONG_DURATION };

		Cursor cursor = db.query(SettingsData.TABLE_NAME, projection, null,
				null, null, null, null);

		// get all the values
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			// insert defaults
			initialSettings();
			int[] defaultSettings = { 2 * 60, 10 };
			return defaultSettings;
		}

		int gameDuration = cursor.getInt(0);
		int songDuration = cursor.getInt(1);

		int[] settings = { gameDuration, songDuration };

		return settings;
	}

	/**
	 * Gets the top high scores in descending order
	 * 
	 * @param numScores
	 *            the number of high scores to retrieve
	 * @return an int array of the specified number of highest scores in
	 *         descending order
	 */
	public int[] getHighScores(int numScores) {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { GameData.COLUMN_NAME_SCORE };
		// order by highest score
		String orderBy = GameData.COLUMN_NAME_SCORE + " DESC";

		Cursor cursor = db.query(GameData.TABLE_NAME, projection, null, null,
				null, null, orderBy);

		int[] scores = new int[numScores];

		// get all the values we can, rest are zeroes by default
		cursor.moveToFirst();
		for (int i = 0; i < numScores; ++i) {
			if (!cursor.isAfterLast()) {
				scores[i] = cursor.getInt(0);
			}
		}

		return scores;
	}

}
