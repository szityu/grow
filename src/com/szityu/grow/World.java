package com.szityu.grow;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;


public class World {

	private static final float EPSILON = 0.01f;
	private static final int MAX_BADDIES = 20;
	private Random rnd;
	
	public Hero hero;
	public Baddy[] baddies;
	public Timer baddyCreationTimer;
	public int numBaddies;
	public float targetX;
	public float targetY;
	
	public float mmpsHeroSpeed;
	public float mmWidth;
	public float mmHeight;
	public float pixelPerMm;
	// pixel quantities, for drawing
	float pxpsHeroSpeed;
	float pxMaxX;
	float pxMaxY;
	
	Paint paint = new Paint();

	public World() {
		hero = new Hero();
		baddies = new Baddy[MAX_BADDIES];
		for (int i = 0; i < MAX_BADDIES; i++) {
			baddies[i] = new Baddy();
		}
		baddyCreationTimer = new Timer(2000);
		numBaddies = 0;
		rnd = new Random();
		targetX = hero.x;
		targetY = hero.y;
		mmpsHeroSpeed = 5000.0f;		
		
	}
	
	public class Hero {
		public final static int HANDLE_X_OFFSET = 20;
		
		public float x;
		public float y;
		
		public Hero() {
			x = 40;
			y = 30;
		}
		
		public void draw(Canvas c) {
			// draw hero
			paint.setColor(android.graphics.Color.WHITE);
			paint.setStyle(Style.FILL);
			c.drawCircle(x * pixelPerMm, y
					* pixelPerMm, 50, paint);
			paint.setColor(android.graphics.Color.YELLOW);
			paint.setStyle(Style.STROKE);
			c.drawCircle((x - HANDLE_X_OFFSET) * pixelPerMm, y * pixelPerMm, 5 * pixelPerMm, paint);
		}
	}
	
	public class Baddy {
		// in mm
		public float x;
		public float y;
		public float size;
		// in mm/s
		public float vx;
		public float vy;
		
		public void copyFrom(Baddy b) {
			x = b.x;
			y = b.y;
			size = b.size;
			vx = b.vx;
			vy = b.vy;
		}
		
		public void draw(Canvas c) {
			// draw baddy
			paint.setColor(android.graphics.Color.MAGENTA);
			paint.setStyle(Style.FILL);
			c.drawCircle(x * pixelPerMm, y * pixelPerMm, size * pixelPerMm, paint);
		}
		
		@Override
		public String toString() {
			return "Baddy [x=" + x + ", y=" + y + ", size=" + size + "]";
		}
	}
	
	public void draw(Canvas c) {
		// draw world
		c.drawRGB(50, 50, 50);

		paint.setColor(android.graphics.Color.GREEN);
		paint.setStyle(Style.STROKE);
		c.drawRect(0f, 0f, mmWidth * pixelPerMm - 1, mmHeight * pixelPerMm - 1, paint);

		hero.draw(c);		
		for (int i = 0; i < numBaddies; i++) {
			baddies[i].draw(c);
		}
	}

	public boolean addBaddy() {
		if (numBaddies >= MAX_BADDIES) {
			Log.w("game", "Too many baddies, cannot add more.");
			return false;
		}
		Baddy b = baddies[numBaddies];
		
		b.size = 5 + 2.5f * (rnd.nextFloat() - 0.5f);
		b.x = mmWidth + b.size;
		b.y = rnd.nextFloat() * mmHeight;
		b.vx = -10;
		b.vy = 0;
//		Log.i("game", String.format("Baddy #%d created.", numBaddies));
		numBaddies++;
		return true;
	}
	
	public void killGoneBaddies() {
		for (int i = numBaddies - 1; i >= 0; i--) {
			if (baddies[i].x < -baddies[i].size) {
				for (int j = i; j < numBaddies-1; j++) {
					baddies[j].copyFrom(baddies[j+1]);
				}
//				Log.i("game", String.format("Baddy #%d deleted. %d left", i, numBaddies-1));				
				numBaddies--;
			}
		}
	}
	
	public void updatePhysics(long msDeltaT) {
		float d = dist(hero.x, hero.y, targetX, targetY);
		if (d < EPSILON || d < mmpsHeroSpeed * msDeltaT / 1000) {
			//Log.i("game", String.format("<<<< %f, %f, %f", hero.x, hero.y, d));
			hero.x = targetX;
			hero.y = targetY;
		} else {
			//Log.i("game", String.format(">>>> %f, %f, %f", hero.x, hero.y, d));
			float dx = (targetX - hero.x) / d * mmpsHeroSpeed * msDeltaT / 1000;
			float dy = (targetY - hero.y) / d * mmpsHeroSpeed * msDeltaT / 1000;
			hero.x += dx;
			hero.y += dy;
		}
		
		// move baddies
		for (int i = 0; i < numBaddies; i++) {
			Baddy b = baddies[i];
			b.x += b.vx * msDeltaT / 1000;
			b.y += b.vy * msDeltaT / 1000;
		}
		
		killGoneBaddies();
		// create new baddies
		baddyCreationTimer.update(msDeltaT);
		if (baddyCreationTimer.triggered()) {
			addBaddy();
		}
	}
	
	private float dist(float x1, float y1, float x2, float y2) {
		return android.util.FloatMath.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	public void setMetrics(float mmWidth, float mmHeight, float pixelPerMm) {
		this.mmWidth = mmWidth;
		this.mmHeight = mmHeight;
		this.pixelPerMm = pixelPerMm;
		
		pxpsHeroSpeed = mmpsHeroSpeed * pixelPerMm;  // calculated from pixel density, do not modify directly.
		addBaddy();

	}
	
	public void setTarget(float tx, float ty) {
		targetX = tx / pixelPerMm;
		targetY = ty / pixelPerMm;
	}
}
