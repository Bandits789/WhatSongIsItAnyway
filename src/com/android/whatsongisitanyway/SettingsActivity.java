package com.android.whatsongisitanyway;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.android.whatsongisitanyway.database.GameDatabaseHelper;

/**
 * Page to set settings!
 */
public class SettingsActivity extends Activity {
	private GameDatabaseHelper dbHelper;
	private int[] settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);

		// get settings
		dbHelper = new GameDatabaseHelper(this);
		settings = dbHelper.getSettings();
	}
	
	@Override
	public void onBackPressed() {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
