package com.szityu.grow.world;

import android.graphics.Paint.Style;

public class Baddy implements WorldObject {
	// in mm
	public float x;
	public float y;
	public float size;
	// in mm/s
	public float vx;
	public float vy;
	
	public boolean beingEaten;
	public boolean dead;
	private static final float DEAD_AT_SIZE = 0.5f;

	private float vShrink;
	
	public void copyFrom(Baddy b) {
		x = b.x;
		y = b.y;
		size = b.size;
		vx = b.vx;
		vy = b.vy;
		beingEaten = b.beingEaten;
		dead = b.dead;
	}
	
	public void eventEatsHero() {
	}

	public void eventGetsEaten() {
		beingEaten = true;
		vShrink = size / 0.3f;
	}

	public void update(long msDeltaT) {
		if (beingEaten) {
			size -= vShrink * msDeltaT / 1000;
			if (size < DEAD_AT_SIZE) {
				dead = true;
			}
		}
	}
	
	public void draw(GraphicsObject g) {
		// draw baddy
		g.paint.setColor(android.graphics.Color.MAGENTA);
		if (beingEaten) {
			g.paint.setColor(android.graphics.Color.RED);				
		}
		g.paint.setStyle(Style.FILL);
		g.canvas.drawCircle(x * g.pixelPerMm, y * g.pixelPerMm, size * g.pixelPerMm, g.paint);
	}
	
	@Override
	public String toString() {
		return "Baddy [x=" + x + ", y=" + y + ", size=" + size + "]";
	}
}
