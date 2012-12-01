package com.szityu.grow.world;

import android.graphics.Paint.Style;

import com.szityu.grow.Base;

public class HeroHandle implements WorldObject {
	public final static int HANDLE_X_OFFSET = 20;
	public final static int HANDLE_RADIUS = 5;

	private float x;
	private float y;
	public Hero hero;
	
	public HeroHandle(Hero hero) {
		this.hero = hero;
		setCoord(0, 0);
	}
	
	public void setCoord(float mmX, float mmY) {
		x = mmX;
		y = mmY;
		hero.x = x + HANDLE_X_OFFSET;
		hero.y = y;
	}
	
	public boolean isWithin(float mmX, float mmY) {
		if (Base.dist(mmX, mmY, x, y) < HANDLE_RADIUS) {
			return true;
		}
		return false;
	}

	public void draw(GraphicsObject g) {
		// draw hero handle
		g.paint.setColor(android.graphics.Color.YELLOW);
		g.paint.setStyle(Style.STROKE);
		g.canvas.drawCircle(x * g.pixelPerMm, y * g.pixelPerMm, HANDLE_RADIUS * g.pixelPerMm, g.paint);
	}

	public void update(long msDeltaT) {
	}

}	
