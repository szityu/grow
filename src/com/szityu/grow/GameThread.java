package com.szityu.grow;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import com.szityu.grow.world.GraphicsObject;
import com.szityu.grow.world.World;

public class GameThread extends Thread {

	public boolean isRunning = false;
	public boolean isPaused = false;
	public SurfaceHolder surfaceHolder;
	public World world;
	public GraphicsObject g;

	public MainActivity parentActivity;
	public DebugStats statsUpdateTime;
	public Timer debugInfoUpdateTimer;
	private float fps;
	private long currentTime;
	private long previousTime;

	public GameThread() {
		super();
		statsUpdateTime = new DebugStats(0.05f);
		debugInfoUpdateTimer = new Timer(1000);
	}
	
	// Not an event handler of Thread, just a propagated function from the activity.
	public void onPause() {
		isPaused = true;
		Log.d("game", String.format("Paused."));
	}
	
	// Not an event handler of Thread, just a propagated function from the activity.
	public void onResume() {
		isPaused = false;		
		currentTime = System.currentTimeMillis();
		Log.d("game", String.format("Resumed."));
	}
	
	@Override
	public void run() {
		currentTime = System.currentTimeMillis();
		while (isRunning) {
			if (isPaused) {
				yield();
			} else {
				previousTime = currentTime;
				currentTime = System.currentTimeMillis();
				// Log.i("game", String.format("dt: %d ms", now-startTime));
				world.update(currentTime - previousTime);
				updateGraphics();
				updateDebugInfo(currentTime - previousTime);
			}
		}
	}

	private void updateGraphics() {
		Canvas c = null;
		try {
			c = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				if (c != null) {
					g.canvas = c;
					world.draw(g);
				}
			}
		} finally {
			if (c != null) {
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}

	}

	private void updateDebugInfo(long msDeltaT) {
		statsUpdateTime.addData(msDeltaT);
		debugInfoUpdateTimer.update(msDeltaT);
		fps = statsUpdateTime.avg > 0 ? 1000 / statsUpdateTime.avg : 0;
		if (debugInfoUpdateTimer.triggered()) {
			final String debugText = String.format("fps: %.1f", fps);
			Log.i("game", debugText);
			if (parentActivity.debugTextView != null &&
					parentActivity.showDebugInfo) {
				// view object must be set from the UI thread.
				parentActivity.runOnUiThread(new Runnable() {
					public void run() {
						parentActivity.debugTextView.setText(debugText);
					}
				});
			}
		}
	}
}
