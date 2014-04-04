package com.android.whatsongisitanyway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * This screen shows right after a game is over and shows the user's score/stats
 * for the game
 */
public class GameScoreActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_score);
		
		// score, avgGuessTime, accuracy, songsPlayed
		float[] stats = getIntent().getExtras().getFloatArray("stats");
		TextView score = (TextView) findViewById(R.id.gameScoreScore);
		
		if ((int) stats[0] != -1) {
			score.setText((int) stats[0] + "");
		} else {
			score.setText("You gave up!");
		}
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
	 * Button listener that opens the stats menu when pressed
	 * 
	 * @param view
	 */
	public void statsButton (View view) {
		Intent intent = new Intent(this, StatsActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Button listener that opens the high score menu when pressed
	 * 
	 * @param view
	 */
	public void highScoreButton (View view) {
		Intent intent = new Intent(this, HighScoreActivity.class);
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
