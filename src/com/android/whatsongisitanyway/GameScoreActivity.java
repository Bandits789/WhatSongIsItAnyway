package com.android.whatsongisitanyway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class GameScoreActivity extends Activity {
	
	//TODO: Do not let player press the back button to go back into the game.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_score);
	}
	
	@Override
	public void onBackPressed() {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

		// TODO: populate with the most recent timestamp'd game data
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

}
