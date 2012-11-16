package com.szityu.grow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GameSurfaceView surfaceView;
	public TextView debugTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		surfaceView = (GameSurfaceView) findViewById(R.id.surfaceView1);
		debugTextView = (TextView) findViewById(R.id.debugTextView);
		surfaceView.gameThread.parentActivity = this;
		if (!getResources().getBoolean(R.bool.onscreen_debug_info)) {
			debugTextView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
