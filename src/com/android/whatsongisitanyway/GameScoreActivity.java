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
	
	//TODO: Do not let player press the back button to go back into the game.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_score);

		// score, avgGuessTime, accuracy, songsPlayed
		float[] stats = getIntent().getExtras().getFloatArray("stats");
		TextView score = (TextView) findViewById(R.id.gameScoreScore);
		score.setText((int) stats[0] + "");
		// TODO: set the rest of the things
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

}
