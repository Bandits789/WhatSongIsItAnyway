package com.android.whatsongisitanyway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * This activity pauses the game until play button is pressed, 
 * which resumes the game at the point of pausing  
 */
public class ResumeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resume);
	}
	
	@Override
	public void onBackPressed() {
	}
	
	/** 
	 * Button listener for resume button to resume game 
	 * @param View
	 */
	public void resume(View view) {
	    int result = 2;
	    Intent intent = new Intent(this, PlayActivity.class);
	    intent.putExtra("result", result); 
	    setResult(RESULT_OK,intent); 
	    finish();
	}
	
} 	


	