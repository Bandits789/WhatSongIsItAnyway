package com.whatsongisitanyway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.whatsongisitanyway.Analytics.TrackerName;

/**
 * The first screen the user sees - navigates to settings, high scores, and
 * playing activities
 */
public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		// analytics stuff, send screen view
		Tracker t = ((Analytics) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("com.whatsongisitanyway.MainMenuActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Button listener that opens the Main activity when pressed
	 * 
	 * @param view
	 */
	public void playButton(View view) {
		Intent intent = new Intent(this, PlayActivity.class);
		startActivity(intent);
	}

	/**
	 * Button listener that opens the playlist menu when pressed
	 * 
	 * @param view
	 */
	public void settingsButton(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * Button listener that opens the high Score menu when pressed
	 * 
	 * @param view
	 */
	public void highScoreButton(View view) {
		Intent intent = new Intent(this, HighScoreActivity.class);
		startActivity(intent);
	}

}
