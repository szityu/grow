package com.szityu.grow;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Callback {

	private GameThread gameThread;
	private World world;
	
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gameThread = new GameThread();
		world = new World();
		gameThread.surfaceHolder = getHolder();
		getHolder().addCallback(this);
		gameThread.world = world;
		
		setFocusable(true);
 	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, r.getDisplayMetrics());
		world.setMetrics(width/px, height/px, px);
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		gameThread.isRunning = true;
        gameThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
    	gameThread.isRunning = false;
        boolean retry = true;
        while (retry) {
            try {
            	gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		if (action == MotionEvent.ACTION_DOWN ||
				action == MotionEvent.ACTION_MOVE) {
			world.hero.x = event.getX() / world.pixelPerMm;
			world.hero.y = event.getY() / world.pixelPerMm;
			//world.setTarget(event.getX(), event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}

	
}
