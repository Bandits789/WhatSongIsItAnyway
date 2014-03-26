package com.android.whatsongisitanyway.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.whatsongisitanyway.database.GameDatabase.GameData;
import com.android.whatsongisitanyway.database.GameDatabase.OverallData;

/**
 * Bridges the gap between code and sqlite, helps with important things such as
 * creation and deletion of the database
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
				+ GameData.COLUMN_NAME_SONGS_PLAYED + " INTEGER)";

		db.execSQL(createGames);
		db.execSQL(createOverall);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for data, so its upgrade policy
		// is to simply to discard the data and start over
		db.execSQL("DROP TABLE IF EXISTS " + OverallData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GameData.TABLE_NAME);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	/**
	 * Inserts the stats of the most recently played game into the database
	 * 
	 * @param score
	 *            score of the most recent game
	 * @param totalGuessTime
	 *            total amount of time spent guessing in the most recent game
	 * @param songsCorrect
	 *            number of songs guessed correctly in the most recent game
	 * @param songsPlayed
	 *            songs played until end of most recent game
	 */
	public void insertGameStats(int score, int totalGuessTime,
			int songsCorrect, int songsPlayed) {
		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(GameData.COLUMN_NAME_SCORE, score);
		if (songsCorrect != 0) {
			values.put(GameData.COLUMN_NAME_AVG_GUESS_TIME, totalGuessTime
					/ songsCorrect);
		} else {
			values.put(GameData.COLUMN_NAME_AVG_GUESS_TIME, 0);
		}
		values.put(GameData.COLUMN_NAME_ACCURACY, songsCorrect / songsPlayed);
		values.put(GameData.COLUMN_NAME_SONGS_PLAYED, songsPlayed);

		// insert the new row!
		db.insert(GameData.TABLE_NAME, null, values);
	}

	/**
	 * Update the overall stats in the database
	 * 
	 * @param score
	 *            score of the most recent game
	 * @param totalGuessTime
	 *            total amount of time spent guessing in the most recent game
	 * @param songsCorrect
	 *            number of songs guessed correctly in the most recent game
	 * @param songsPlayed
	 *            songs played until end of most recent game
	 */
	public void updateOverallStats(int score, int totalGuessTime,
			int songsCorrect, int songsPlayed) {
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
			initialOverallStats(score, totalGuessTime, songsCorrect,
					songsPlayed);
			return;
		}
		float id = cursor.getInt(0);
		float oldAccuracy = cursor.getFloat(1);
		float oldAvgGuessTime = cursor.getFloat(2);
		int oldGamesPlayed = cursor.getInt(3);
		int oldSongsPlayed = cursor.getInt(4);

		// find new values
		int newSongsPlayed = oldSongsPlayed + songsPlayed;
		float newAccuracy = (oldAccuracy * oldSongsPlayed + songsCorrect)
				/ newSongsPlayed;
		float newAvgGuessTime = (float) ((oldAvgGuessTime * oldGamesPlayed + totalGuessTime) / newSongsPlayed);

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(OverallData.COLUMN_NAME_ACCURACY, newAccuracy);
		values.put(OverallData.COLUMN_NAME_AVG_GUESS_TIME, newAvgGuessTime);
		values.put(OverallData.COLUMN_NAME_GAMES_PLAYED, oldGamesPlayed + 1);
		values.put(OverallData.COLUMN_NAME_SONGS_PLAYED, newSongsPlayed);

		String selection = OverallData._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(id) };

		// update!
		db.update(OverallData.TABLE_NAME, values, selection, selectionArgs);
	}

	/**
	 * Initially when there are no overall stats, insert the first game stats
	 * into overall
	 * 
	 * @param score
	 *            score of the most recent game
	 * @param totalGuessTime
	 *            total amount of time spent guessing in the most recent game
	 * @param songsCorrect
	 *            number of songs guessed correctly in the most recent game
	 * @param songsPlayed
	 *            songs played until end of most recent game
	 */
	private void initialOverallStats(int score, int totalGuessTime,
			int songsCorrect, int songsPlayed) {
		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(OverallData.COLUMN_NAME_ACCURACY, songsCorrect / songsPlayed);
		if (songsCorrect != 0) {
			values.put(OverallData.COLUMN_NAME_AVG_GUESS_TIME, totalGuessTime
					/ songsCorrect);
		} else {
			values.put(OverallData.COLUMN_NAME_AVG_GUESS_TIME, 0);
		}
		values.put(OverallData.COLUMN_NAME_ACCURACY, songsCorrect / songsPlayed);
		values.put(OverallData.COLUMN_NAME_SONGS_PLAYED, songsPlayed);

		// insert the new row!
		db.insert(OverallData.TABLE_NAME, null, values);
	}

}
