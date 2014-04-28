package com.whatsongisitanyway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.whatsongisitanyway.database.GameDatabaseHelper;

/**
 * Activity that displays the highest scores overall
 */
public class HighScoreActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.high_scores);

		// get stats from database
		GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
		// get 5 highest scores
		int[] scores = dbHelper.getHighScores(5);

		// set all the labels
		TextView high1 = (TextView) findViewById(R.id.high1Val);
		TextView high2 = (TextView) findViewById(R.id.high2Val);
		TextView high3 = (TextView) findViewById(R.id.high3Val);
		TextView high4 = (TextView) findViewById(R.id.high4Val);
		TextView high5 = (TextView) findViewById(R.id.high5Val);

		high1.setText(String.valueOf(scores[0]));
		high2.setText(String.valueOf(scores[1]));
		high3.setText(String.valueOf(scores[2]));
		high4.setText(String.valueOf(scores[3]));
		high5.setText(String.valueOf(scores[4]));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Button listener that opens the stats menu when pressed
	 * 
	 * @param view
	 */
	public void statsButton (View view) {
		Intent intent = new Intent(this, StatsActivity.class);
		startActivity(intent);
	}
	
	 /** Button listener that opens the main menu when pressed
	 * 
	 * @param view
	 */
	public void mainMenuButton (View view) {
		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
	}

}
