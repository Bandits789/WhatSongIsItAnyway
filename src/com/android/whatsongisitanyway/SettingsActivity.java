package com.android.whatsongisitanyway;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.NumberPicker;

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

		NumberPicker gameSec = (NumberPicker) findViewById();
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
	
	/**
	 * On click on the save button, save the settings to the database and go
	 * back to main menu
	 * 
	 * @param view
	 */
	public void saveButton(View view) {
		int gameDuration = 0;
		int songDuration = 0;

		// dbHelper.updateSettings(gameDuration, songDuration);

		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
	}
}
