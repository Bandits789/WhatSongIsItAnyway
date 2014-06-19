package com.wsiia.database;

import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wsiia.database.GameDatabase.FirstTime;
import com.wsiia.database.GameDatabase.GameData;
import com.wsiia.database.GameDatabase.OverallData;
import com.wsiia.database.GameDatabase.SettingsData;
import com.wsiia.database.GameDatabase.SongData;

/**
 * Bridges the gap between code and sqlite, helps with important things such as
 * creation and deletion of the database, and updating the game and stat info
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "WSIIAData.db";
	private static final int defaultGameDuration = 3 * 60; // 3 min
	private static final int defaultSongDuration = 20; // 20 secs

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
				+ OverallData.COLUMN_NAME_UNIQUE_SONGS_PLAYED + " INTEGER,"
				+ OverallData.COLUMN_NAME_SONGS_PLAYED + " INTEGER)";

		// now create the songs table
		String createSongs = "CREATE TABLE " + SongData.TABLE_NAME + " ("
				+ SongData._ID + " INTEGER PRIMARY KEY,"
				+ SongData.COLUMN_NAME_TIMESTAMP
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ SongData.COLUMN_NAME_SONG_TITLE + " VARCHAR(200),"
				+ SongData.COLUMN_NAME_TIMES_GUESSED + " INTEGER,"
				+ SongData.COLUMN_NAME_TIMES_SKIPPED + " INTEGER,"
				+ SongData.COLUMN_NAME_TIMES_PLAYED + " INTEGER)";

		// and then settings table
		String createSettings = "CREATE TABLE " + SettingsData.TABLE_NAME
				+ " (" + SettingsData._ID + " INTEGER PRIMARY KEY,"
				+ SettingsData.COLUMN_NAME_GAME_DURATION + " INTEGER,"
				+ SettingsData.COLUMN_NAME_SONG_DURATION + " INTEGER)";

		// and now first time table
		String createFirstTime = "CREATE TABLE " + FirstTime.TABLE_NAME + " ("
				+ FirstTime._ID + " INTEGER PRIMARY KEY,"
				+ FirstTime.COLUMN_NAME_FIRST_TIME + " BIT)";

		db.execSQL(createGames);
		db.execSQL(createOverall);
		db.execSQL(createSongs);
		db.execSQL(createSettings);
		db.execSQL(createFirstTime);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for data, so its upgrade policy
		// is to simply to discard the data and startgs over
		db.execSQL("DROP TABLE IF EXISTS " + OverallData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GameData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SettingsData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SongData.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FirstTime.TABLE_NAME);
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

		String selection = SettingsData._ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };

		// update!
		db.update(SettingsData.TABLE_NAME, values, selection, selectionArgs);
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
	 * @param songMap
	 *            a map of the songs played in the most recent game (song title
	 *            -> [times guessed, times skipped])
	 */
	public void updateOverallStats(int score, float averageGuessTime,
			float accuracy, int songsPlayed, Map<String, int[]> songMap) {

		// get the number of *new* songs played and update the song database
		int uniqueSongsPlayed = updateSongs(songMap);

		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		float[] oldStats = getOverallStats();
		float oldAccuracy = oldStats[0];
		float oldAvgGuessTime = oldStats[1];
		int oldGamesPlayed = (int) oldStats[2];
		int oldSongsPlayed = (int) oldStats[3];
		int oldUniqueSongsPlayed = (int) oldStats[4];

		// find new values
		int newSongsPlayed = oldSongsPlayed + songsPlayed;
		float newAccuracy = (oldAccuracy * oldSongsPlayed + accuracy
				* songsPlayed)
				/ (float) newSongsPlayed;
		float newAvgGuessTime = (oldAvgGuessTime * oldGamesPlayed + averageGuessTime
				* songsPlayed)
				/ (float) newSongsPlayed;
		int newUniqueSongsPlayed = oldUniqueSongsPlayed + uniqueSongsPlayed;

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(OverallData.COLUMN_NAME_ACCURACY, newAccuracy);
		values.put(OverallData.COLUMN_NAME_AVG_GUESS_TIME, newAvgGuessTime);
		values.put(OverallData.COLUMN_NAME_GAMES_PLAYED, oldGamesPlayed + 1);
		values.put(OverallData.COLUMN_NAME_SONGS_PLAYED, newSongsPlayed);
		values.put(OverallData.COLUMN_NAME_UNIQUE_SONGS_PLAYED,
				newUniqueSongsPlayed);

		// if this is the first stats update, insert
		if (oldGamesPlayed == 0) {
			db.insert(OverallData.TABLE_NAME, null, values);
		} else {
			// otherwise we update the first column
			String selection = OverallData._ID + " = 1";
			db.update(OverallData.TABLE_NAME, values, selection, null);
		}
	}

	/**
	 * Initially when there are no settings, insert the default settings (2mins
	 * for game, 10 secs for song)
	 */
	private void initialSettings() {
		// gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(SettingsData.COLUMN_NAME_GAME_DURATION, defaultGameDuration);
		values.put(SettingsData.COLUMN_NAME_SONG_DURATION, defaultSongDuration);

		// insert the new row!
		db.insert(SettingsData.TABLE_NAME, null, values);
	}

	/**
	 * Gets the overall stats in a float array
	 * 
	 * @return a float array of accuracy, avgGuessTime, gamesPlayed,
	 *         songsPlayed, uniqueSongsPlayed
	 */
	public float[] getOverallStats() {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { OverallData.COLUMN_NAME_ACCURACY,
				OverallData.COLUMN_NAME_AVG_GUESS_TIME,
				OverallData.COLUMN_NAME_GAMES_PLAYED,
				OverallData.COLUMN_NAME_SONGS_PLAYED,
				OverallData.COLUMN_NAME_UNIQUE_SONGS_PLAYED };

		Cursor cursor = db.query(OverallData.TABLE_NAME, projection, null,
				null, null, null, null);

		// get all the values
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			// nothing to show yet!
			float[] nothing = { 0, 0, 0, 0, 0 };
			return nothing;
		}

		float accuracy = cursor.getFloat(0);
		float avgGuessTime = cursor.getFloat(1);
		int gamesPlayed = cursor.getInt(2);
		int songsPlayed = cursor.getInt(3);
		int uniqueSongsPlayed = cursor.getInt(4);

		float[] stats = { accuracy, avgGuessTime, gamesPlayed, songsPlayed,
				uniqueSongsPlayed };

		return stats;
	}

	/**
	 * Get the most guessed song in the database. If there is a tie, the most
	 * recently played song wins. If no song has been guessed correctly, None is
	 * returned
	 * 
	 * @return the most guessed song's title
	 */
	public String getMostGuessedSong() {
		// gets the data repository in read mode
		SQLiteDatabase db = getReadableDatabase();

		// columns we want
		String[] projection = { SongData.COLUMN_NAME_SONG_TITLE,
				SongData.COLUMN_NAME_TIMES_GUESSED };
		// first order by times guessed, then order by timestamp to break ties
		String orderBy = SongData.COLUMN_NAME_TIMES_GUESSED + " DESC, "
				+ SongData.COLUMN_NAME_TIMESTAMP + " DESC";

		Cursor cursor = db.query(SongData.TABLE_NAME, projection, null, null,
				null, null, orderBy);

		// get all the values
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			// nothing to show yet!
			return "None";
		}

		// if no songs have been guessed correctly
		if (cursor.getInt(1) == 0) {
			return "None";
		}

		return cursor.getString(0);
	}

	/**
	 * Update the SongData database. Given a map of the new songs and their
	 * data, we update/insert the necessary rows with new info.
	 * 
	 * @param songMap
	 *            a map of song title -> [times guessed, times skipped]
	 * @return the number of unique songs added to the database
	 */
	private int updateSongs(Map<String, int[]> songMap) {
		int uniqueSongs = 0;

		// gets the data repository in read/write mode
		SQLiteDatabase db = getWritableDatabase();

		// columns we want
		String[] projection = { SongData.COLUMN_NAME_TIMES_GUESSED,
				SongData.COLUMN_NAME_TIMES_SKIPPED,
				SongData.COLUMN_NAME_TIMES_PLAYED };

		// loop over all entries in map, insert/update rows in db
		for (Entry<String, int[]> song : songMap.entrySet()) {
			// we want to see if the song title is in the db (assuming song
			// titles are unique)
			String selection = SongData.COLUMN_NAME_SONG_TITLE + " = \""
					+ song.getKey() + "\"";

			Cursor cursor = db.query(SongData.TABLE_NAME, projection,
					selection, null, null, null, null);

			cursor.moveToFirst();
			// there was no such record, insert new row
			if (cursor.isAfterLast()) {
				++uniqueSongs;

				// create a new map of values, where column names are the keys
				ContentValues values = new ContentValues();
				values.put(SongData.COLUMN_NAME_SONG_TITLE, song.getKey());
				values.put(SongData.COLUMN_NAME_TIMES_GUESSED,
						song.getValue()[0]);
				values.put(SongData.COLUMN_NAME_TIMES_SKIPPED,
						song.getValue()[1]);
				values.put(SongData.COLUMN_NAME_TIMES_PLAYED, 1);

				// insert the new row!
				db.insert(SongData.TABLE_NAME, null, values);
			} else {
				// create a new map of values, where column names are the keys
				ContentValues values = new ContentValues();
				values.put(SongData.COLUMN_NAME_TIMES_GUESSED,
						song.getValue()[0] + cursor.getInt(0));
				values.put(SongData.COLUMN_NAME_TIMES_SKIPPED,
						song.getValue()[1] + cursor.getInt(1));
				values.put(SongData.COLUMN_NAME_TIMES_PLAYED,
						1 + cursor.getInt(2));

				// update!
				db.update(SongData.TABLE_NAME, values, selection, null);
			}
		}

		return uniqueSongs;
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
			int[] defaultSettings = { defaultGameDuration, defaultSongDuration };
			return defaultSettings;
		}

		int gameDuration = cursor.getInt(0);
		int songDuration = cursor.getInt(1);

		int[] settings = { gameDuration, songDuration };

		return settings;
	}

	/**
	 * Decides if it is the user's first time playing (after the first time this
	 * will be false)
	 * 
	 * @return whether or not it the user's first time playing
	 */
	public boolean isFirstTime() {
		// gets the data repository in read/write mode
		SQLiteDatabase db = getWritableDatabase();

		// columns we want
		String[] projection = { FirstTime.COLUMN_NAME_FIRST_TIME };

		Cursor cursor = db.query(FirstTime.TABLE_NAME, projection, null, null,
				null, null, null);

		// if there is something, then it is not the first time
		cursor.moveToFirst();
		// was first time, now make that false
		if (cursor.isAfterLast()) {
			// create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			values.put(FirstTime.COLUMN_NAME_FIRST_TIME, 1);

			// insert the new row!
			db.insert(FirstTime.TABLE_NAME, null, values);

			return true;
		}

		// there was already something there, so it's not the first time
		return false;
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
				cursor.moveToNext();
			}
		}

		return scores;
	}

}
