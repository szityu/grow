package com.szityu.grow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GameSurfaceView surfaceView;
	public TextView debugTextView;
	public boolean showDebugInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Unfortunately, we need the Action bar for the menu button.
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_main);
		surfaceView = (GameSurfaceView) findViewById(R.id.surfaceView1);
		debugTextView = (TextView) findViewById(R.id.debugTextView);
		surfaceView.gameThread.parentActivity = this;

		showDebugInfo = false;
		// Views on surfaceView have buggy behavior. This must be GONE, not INVISIBLE, otherwise it will never reappear.
		debugTextView.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("game", "menu item selected: " + item.toString());
		switch (item.getItemId()) {
		case R.id.menu_debug:
			showDebugInfo = !showDebugInfo;
			debugTextView.setVisibility(showDebugInfo ? View.VISIBLE
					: View.GONE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_debug);
		item.setTitle(showDebugInfo ? 
				R.string.menu_debug_checked : R.string.menu_debug_unchecked);
		item.setChecked(showDebugInfo);
		return true;
	}

}
