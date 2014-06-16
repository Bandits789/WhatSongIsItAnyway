package com.whatsongisitanyway;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.whatsongisitanyway.Analytics.TrackerName;
import com.whatsongisitanyway.database.GameDatabaseHelper;

/**
 * Activity that displays overall game stats (accuracy, avgGuessTime,
 * gamesPlayed, songsPlayed)
 */
public class StatsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stats);

		// get stats from database
		GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
		// accuracy, avgGuessTime, gamesPlayed, songsPlayed
		float[] stats = dbHelper.getOverallStats();
		String mostGuessedSong = dbHelper.getMostGuessedSong();

		// set all the labels
		TextView accuracy = (TextView) findViewById(R.id.stats1Val);
		TextView avgGuessTime = (TextView) findViewById(R.id.stats2Val);
		TextView gamesPlayed = (TextView) findViewById(R.id.stats3Val);
		TextView songsPlayed = (TextView) findViewById(R.id.stats4Val);
		TextView mostGuessed = (TextView) findViewById(R.id.stats5Val);

		String roundedAccuracy = String.format(Locale.getDefault(), "%.3g",
				stats[0] * 100);
		String roundedGuessTime = String.format(Locale.getDefault(), "%.3g",
				stats[1]);

		accuracy.setText(roundedAccuracy);
		avgGuessTime.setText(roundedGuessTime);
		gamesPlayed.setText((int) stats[2] + "");
		songsPlayed.setText((int) stats[3] + "");
		mostGuessed.setText(mostGuessedSong);

		// analytics stuff, send screen view
		Tracker tracker = ((Analytics) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		tracker.setScreenName("com.whatsongisitanyway.StatsActivity");
		tracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Button listener that opens the high score menu when pressed
	 * 
	 * @param view
	 */
	public void highScoreButton(View view) {
		Intent intent = new Intent(this, HighScoreActivity.class);
		startActivity(intent);
	}

	/**
	 * Button listener that opens the main menu when pressed
	 * 
	 * @param view
	 */
	public void mainMenuButton(View view) {
		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
	}

}
