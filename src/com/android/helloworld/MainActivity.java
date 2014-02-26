package com.android.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** 
	 * Called when the user clicks the button!!1 
	 */
	public void switchText(View view) {
		// find the thing!
		TextView textView = (TextView) findViewById(R.id.textView1);
		// change the thing!
		textView.setText("Goodbye 21w.789");
	}

}
