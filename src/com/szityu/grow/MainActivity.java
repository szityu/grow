package com.szityu.grow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceView;

public class MainActivity extends Activity {

	private SurfaceView surfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
