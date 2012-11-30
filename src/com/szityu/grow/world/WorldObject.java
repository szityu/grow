package com.szityu.grow.world;

import android.graphics.Canvas;

public interface WorldObject {
	public void update(long msDeltaT);
	public void draw(Canvas c);
}
