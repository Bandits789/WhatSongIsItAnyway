package com.whatsongisitanyway;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.whatsongisitanyway.Analytics.TrackerName;
import com.whatsongisitanyway.database.GameDatabaseHelper;

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

		// sets the layout settings for the edittexts in the number pickers
		final EditText edText = (EditText) gameSec.getChildAt(1);
		edText.setBackgroundResource(R.drawable.number_box);
		edText.setTextColor(Color.parseColor("#FFFFFF"));
		LayoutParams params = edText.getLayoutParams();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;

		// creates a view change listener because the width is not created until
		// onCreate has finished
		ViewTreeObserver vto = edText.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				// immediately removes the listener to prevent redrawing
				edText.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				// gets the views
				NumberPicker gameSec = (NumberPicker) findViewById(R.id.settingsGameSec);
				NumberPicker gameMins = (NumberPicker) findViewById(R.id.settingsGameMin);
				NumberPicker songSec = (NumberPicker) findViewById(R.id.settingsSongSec);

				// Gets the scaled width and height
				int TEXT_WIDTH = edText.getMeasuredWidth();
				int TEXT_HEIGHT = edText.getMeasuredWidth();

				// sets the layout settings for the edittexts in the number
				// pickers
				EditText editText = (EditText) gameMins.getChildAt(1);
				editText.setBackgroundResource(R.drawable.number_box);
				editText.setTextColor(Color.parseColor("#FFFFFF"));
				LayoutParams params = editText.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				editText = (EditText) songSec.getChildAt(1);
				editText.setBackgroundResource(R.drawable.number_box);
				editText.setTextColor(Color.parseColor("#FFFFFF"));
				params = editText.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				// sets images and layout settings for the up buttons in the
				// number pickers and removes long click ability
				View upButton = gameSec.getChildAt(0);
				upButton.setLongClickable(false);
				upButton.setBackgroundResource(R.drawable.up_button);
				upButton.setScaleX(1f);
				params = upButton.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				upButton = gameMins.getChildAt(0);
				upButton.setLongClickable(false);
				upButton.setBackgroundResource(R.drawable.up_button);
				upButton.setScaleX(1f);
				params = upButton.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				upButton = songSec.getChildAt(0);
				upButton.setLongClickable(false);
				upButton.setBackgroundResource(R.drawable.up_button);
				upButton.setScaleX(1f);
				params = upButton.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				// sets images and layout settings for the down buttons in the
				// number pickers and removes long click ability
				View downButton = gameSec.getChildAt(2);
				downButton.setLongClickable(false);
				downButton.setBackgroundResource(R.drawable.down_button);
				downButton.setScaleX(1f);
				params = downButton.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				downButton = gameMins.getChildAt(2);
				downButton.setLongClickable(false);
				downButton.setBackgroundResource(R.drawable.down_button);
				downButton.setScaleX(1f);
				params = downButton.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

				downButton = songSec.getChildAt(2);
				downButton.setLongClickable(false);
				downButton.setBackgroundResource(R.drawable.down_button);
				downButton.setScaleX(1f);
				params = downButton.getLayoutParams();
				params.height = TEXT_WIDTH;
				params.width = TEXT_HEIGHT;

			}
		});

		gameSec.setMaxValue(59);
		gameMins.setMaxValue(9);
		gameMins.setMinValue(1);
		songSec.setMaxValue(30);
		songSec.setMinValue(1);

		// make secs look like 00 not 0
		songSec.setFormatter(new TwoDigitFormatter());
		gameSec.setFormatter(new TwoDigitFormatter());

		int seconds = settings[0] % 60;
		int minutes = (settings[0] - seconds) / 60;

		gameSec.setValue(seconds);
		gameMins.setValue(minutes);
		songSec.setValue(settings[1]);

		// analytics stuff, send screen view
		Tracker t = ((Analytics) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("com.whatsongisitanyway.SettingsActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onBackPressed() {
		saveButton(null);
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

		// removes focus so the getValue method can get updated information
		gameSec.clearFocus();
		gameMins.clearFocus();
		songSec.clearFocus();

		// get everything in secs
		int gameDuration = gameSec.getValue() + gameMins.getValue() * 60;
		int songDuration = songSec.getValue();

		dbHelper.updateSettings(gameDuration, songDuration);

		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
	}

	/**
	 * Makes a NumberPicker format that makes a single digit display as two
	 * digits (ie. 00)
	 */
	private class TwoDigitFormatter implements Formatter {
		public String format(int value) {
			return String.format(Locale.getDefault(), "%02d", value);
		}
	}

	public void numPickerPress(View view) {
		NumberPicker picker = (NumberPicker) view;

		// sets images for the up buttons in the number pickers
		View upButton = picker.getChildAt(0);
		upButton.setBackgroundResource(R.drawable.up_button);
		LayoutParams params = upButton.getLayoutParams();
		params.height = 150;
		params.width = 150;

		// sets images for the up buttons in the number pickers
		View downButton = picker.getChildAt(2);
		downButton.setBackgroundResource(R.drawable.down_button);
		params = downButton.getLayoutParams();
		params.height = 150;
		params.width = 150;
	}
}
