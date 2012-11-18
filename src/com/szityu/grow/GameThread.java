package com.szityu.grow;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.szityu.grow.World.Baddy;

public class GameThread extends Thread {

	public boolean isRunning = false;
	public SurfaceHolder surfaceHolder;
	public World world;

	public MainActivity parentActivity;
	public DebugStats statsUpdateTime;
	public Timer debugInfoUpdateTimer;
	private float fps;

	public GameThread() {
		super();
		statsUpdateTime = new DebugStats(0.05f);
		debugInfoUpdateTimer = new Timer(1000);
	}

	@Override
	public void run() {
		long now = System.currentTimeMillis();
		long previous;
		while (isRunning) {
			previous = now;
			now = System.currentTimeMillis();
			// Log.i("game", String.format("dt: %d ms", now-startTime));
			world.updatePhysics(now - previous);
			updateGraphics();
			updateDebugInfo(now - previous);
		}
	}

	private void updateGraphics() {
		Canvas c = null;
		try {
			c = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				if (c != null) {
					updateCanvas(c);
				}
			}
		} finally {
			if (c != null) {
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}

	}

	private void updateCanvas(Canvas c) {
		c.drawRGB(50, 50, 50);
		Paint paint = new Paint();

		paint.setColor(android.graphics.Color.GREEN);
		paint.setStyle(Style.STROKE);
		c.drawRect(0f, 0f, world.mmWidth * world.pixelPerMm - 1, world.mmHeight
				* world.pixelPerMm - 1, paint);

		paint.setColor(android.graphics.Color.WHITE);
		paint.setStyle(Style.FILL);
		c.drawCircle(world.hero.x * world.pixelPerMm, world.hero.y
				* world.pixelPerMm, 50, paint);

		paint.setColor(android.graphics.Color.MAGENTA);
		paint.setStyle(Style.FILL);
		for (int i = 0; i < world.numBaddies; i++) {
			Baddy b = world.baddies[i];
			c.drawCircle(b.x * world.pixelPerMm, b.y * world.pixelPerMm, b.size
					* world.pixelPerMm, paint);
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
