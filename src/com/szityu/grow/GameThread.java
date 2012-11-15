package com.szityu.grow;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;

import com.szityu.grow.World.Baddy;

public class GameThread extends Thread {

	public boolean isRunning = false;
	public SurfaceHolder surfaceHolder;
	public World world; 


	public GameThread() {
		super();
	}

	@Override
	public void run() {
		long now = System.currentTimeMillis();
		long previous;
		while (isRunning) {
			previous = now;
			now = System.currentTimeMillis();
//			Log.i("game", String.format("dt: %d ms", now-startTime));
			world.updatePhysics(now - previous);
			updateGraphics();
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
		c.drawRect(0f, 0f, world.mmWidth * world.pixelPerMm - 1, world.mmHeight * world.pixelPerMm - 1, paint);

		paint.setColor(android.graphics.Color.WHITE);
		paint.setStyle(Style.FILL);
		c.drawCircle(world.hero.x * world.pixelPerMm, world.hero.y * world.pixelPerMm, 50, paint);
		
		paint.setColor(android.graphics.Color.MAGENTA);
		paint.setStyle(Style.FILL);
		for (int i = 0; i < world.numBaddies; i++) {
			Baddy b = world.baddies[i];
			c.drawCircle(b.x * world.pixelPerMm, b.y * world.pixelPerMm, b.size * world.pixelPerMm, paint);
		}
	}
}
