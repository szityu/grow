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
	public HeroHandle heroHandle;
	public Baddy[] baddies;
	public Timer baddyCreationTimer;
	public int numBaddies;
	public float targetX;
	public float targetY;
	
	public float mmpsHeroSpeed;
	public float mmWidth;
	public float mmHeight;
	
	
	// quantities for drawing
	public float pixelPerMm;
	float pxpsHeroSpeed;
	float pxMaxX;
	float pxMaxY;
	Paint paint = new Paint();

	// controller variables
	boolean control_locked;
	
	public World() {
		hero = new Hero();
		heroHandle = new HeroHandle(hero);
		baddies = new Baddy[MAX_BADDIES];
		for (int i = 0; i < MAX_BADDIES; i++) {
			baddies[i] = new Baddy();
		}
		baddyCreationTimer = new Timer(2000);
		rnd = new Random();
	}
	
	// Start a new game, initialize the world
	public void startGame() {
		numBaddies = 0;
		heroHandle.setCoord(30, 20);
		hero.size = 3.0f;
		
		mmpsHeroSpeed = 5000.0f;				
		control_locked = false;
	}

	public class Hero {
		
		// x and y coordinates are only set through the heroHandle.
		public float x;
		public float y;
		public float size;
		
		public Hero() {
			size = 3.0f;
		}
		
		public void draw(Canvas c) {
			// draw hero
			paint.setColor(android.graphics.Color.WHITE);
			paint.setStyle(Style.FILL);
			c.drawCircle(x * pixelPerMm, y * pixelPerMm, size * pixelPerMm, paint);
		}
		
	}
	
	public class HeroHandle {
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
			if (dist(mmX, mmY, x, y) < HANDLE_RADIUS) {
				return true;
			}
			return false;
		}

		public void draw(Canvas c) {
			// draw hero handle
			paint.setColor(android.graphics.Color.YELLOW);
			paint.setStyle(Style.STROKE);
			c.drawCircle(x * pixelPerMm, y * pixelPerMm, HANDLE_RADIUS * pixelPerMm, paint);
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
		heroHandle.draw(c);
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
		
		b.size = hero.size * (rnd.nextFloat() + 0.7f);
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
//		float d = dist(hero.x, hero.y, targetX, targetY);
//		if (d < EPSILON || d < mmpsHeroSpeed * msDeltaT / 1000) {
//			//Log.i("game", String.format("<<<< %f, %f, %f", hero.x, hero.y, d));
//			hero.x = targetX;
//			hero.y = targetY;
//		} else {
//			//Log.i("game", String.format(">>>> %f, %f, %f", hero.x, hero.y, d));
//			float dx = (targetX - hero.x) / d * mmpsHeroSpeed * msDeltaT / 1000;
//			float dy = (targetY - hero.y) / d * mmpsHeroSpeed * msDeltaT / 1000;
//			hero.x += dx;
//			hero.y += dy;
//		}

//		heroHandle.setCoord(targetX, targetY);
		
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

	public void actionClick(float rawX, float rawY) {
		float mmX = rawX / pixelPerMm;
		float mmY = rawY / pixelPerMm;
		if (heroHandle.isWithin(mmX, mmY)) {
			control_locked = true;
			heroHandle.setCoord(mmX, mmY);
		}
	}
	
	public void actionRelease() {
		control_locked = false;
	}
	
	public void actionMoveTo(float rawX, float rawY) {
		if (control_locked) {
			heroHandle.setCoord(rawX / pixelPerMm, rawY / pixelPerMm);
		}
	}
	
	public void setTarget(float tx, float ty) {
		targetX = tx / pixelPerMm;
		targetY = ty / pixelPerMm;
	}
}
