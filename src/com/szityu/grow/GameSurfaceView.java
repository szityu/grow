package com.szityu.grow;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Callback {
	public World world;
	
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);		
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
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		if (action == MotionEvent.ACTION_DOWN) {
			world.actionClick(event.getX(), event.getY());
			return true;
		} else if (action == MotionEvent.ACTION_MOVE) {
			world.actionMoveTo(event.getX(), event.getY());
			return true;			
		} else if (action == MotionEvent.ACTION_UP ||
				action == MotionEvent.ACTION_CANCEL) {
			world.actionRelease();
			return true;
		}
		return super.onTouchEvent(event);
	}

	
}
