package com.szityu.grow.world;

import android.graphics.Paint.Style;

import com.szityu.grow.Base;

public class Hero implements WorldObject {
	
	// x and y coordinates are only set through the heroHandle.
	public float x;
	public float y;
	public float size;
	
	public Hero() {
		size = 3.0f;
	}
	
	public boolean isTouching(Baddy b) {
		return (Base.dist(b.x, b.y, x, y) < b.size + size);
	}
	
	public void eventEatsBaddy() {
		size *= 1.1f;
	}

	public void eventGetsEaten() {
		size /= 1.1f;
	}

	public void draw(GraphicsObject g) {
		// draw hero
		g.paint.setColor(android.graphics.Color.WHITE);
		g.paint.setStyle(Style.FILL);
		g.canvas.drawCircle(x * g.pixelPerMm, y * g.pixelPerMm, size * g.pixelPerMm, g.paint);
	}

	public void update(long msDeltaT) {
	}
	
}
