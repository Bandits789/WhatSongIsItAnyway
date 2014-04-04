package com.android.whatsongisitanyway;

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
		// [gameDuration, songDuration]
		settings = dbHelper.getSettings();

		// set defaults and max values
		NumberPicker gameSec = (NumberPicker) findViewById(R.id.settingsGameSec);
		NumberPicker gameMins = (NumberPicker) findViewById(R.id.settingsGameMin);
		NumberPicker songSec = (NumberPicker) findViewById(R.id.settingsSongSec);

		gameSec.setMaxValue(59);
		gameSec.setMinValue(1);
		gameMins.setMaxValue(9);
		gameMins.setMinValue(1);
		songSec.setMaxValue(20);

		int seconds = settings[0] % 60;
		int minutes = (settings[0] - seconds) / 60;

		gameSec.setValue(seconds);
		gameMins.setValue(minutes);
		songSec.setValue(settings[1]);
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
		NumberPicker gameSec = (NumberPicker) findViewById(R.id.settingsGameSec);
		NumberPicker gameMins = (NumberPicker) findViewById(R.id.settingsGameMin);
		NumberPicker songSec = (NumberPicker) findViewById(R.id.settingsSongSec);

		// get everything in secs
		int gameDuration = gameSec.getValue() + gameMins.getValue() * 60;
		int songDuration = songSec.getValue();

		dbHelper.updateSettings(gameDuration, songDuration);

		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
	}
}
