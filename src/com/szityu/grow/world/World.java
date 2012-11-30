package com.szityu.grow.world;

import java.util.Random;

import com.szityu.grow.Timer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.Log;


public class World {
	public enum State {
		BEFORE_START,
		RUNNING,
		GAME_OVER		
	}
	public State state;
	
	private static final float EPSILON = 0.01f;
	private static final int MAX_BADDIES = 20;
	private Random rnd;
	
	public Hero hero;
	public HeroHandle heroHandle;
	public Baddy[] baddies;
	public OverlayText mainText;
	public Timer baddyCreationTimer;
	public int numBaddies;
	public float targetX;
	public float targetY;
	
	public float mmpsHeroSpeed;
	public float mmWidth;
	public float mmHeight;
	public boolean metrics_initialized;
	
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
		mainText = new OverlayText();
		baddyCreationTimer = new Timer(2000);
		rnd = new Random();
		metrics_initialized = false;
		state = State.BEFORE_START;
		startGame();
	}
	
	// Start a new game, initialize the world
	public void startGame() {
		Log.i("game", "New game started.");
		numBaddies = 0;
		setHeroCoord(30, 20);
		hero.size = 3.0f;
		mainText.showText("Go!", mmWidth / 2, mmHeight / 2, 1000);
		
		mmpsHeroSpeed = 5000.0f;				
		control_locked = false;
	}

	public class Hero implements WorldObject {
		
		// x and y coordinates are only set through the heroHandle.
		public float x;
		public float y;
		public float size;
		
		public Hero() {
			size = 3.0f;
		}
		
		public boolean isTouching(Baddy b) {
			return (dist(b.x, b.y, x, y) < b.size + size);
		}
		
		public void eventEatsBaddy() {
			size *= 1.1f;
		}

		public void eventGetsEaten() {
			size /= 1.1f;
		}

		public void draw(GraphicsObject g) {
			// draw hero
			paint.setColor(android.graphics.Color.WHITE);
			paint.setStyle(Style.FILL);
			g.canvas.drawCircle(x * pixelPerMm, y * pixelPerMm, size * pixelPerMm, paint);
		}

		public void update(long msDeltaT) {
		}
		
	}
	
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
			if (dist(mmX, mmY, x, y) < HANDLE_RADIUS) {
				return true;
			}
			return false;
		}

		public void draw(GraphicsObject g) {
			// draw hero handle
			paint.setColor(android.graphics.Color.YELLOW);
			paint.setStyle(Style.STROKE);
			g.canvas.drawCircle(x * pixelPerMm, y * pixelPerMm, HANDLE_RADIUS * pixelPerMm, paint);
		}

		public void update(long msDeltaT) {
		}

	}
	
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
			paint.setColor(android.graphics.Color.MAGENTA);
			if (beingEaten) {
				paint.setColor(android.graphics.Color.RED);				
			}
			paint.setStyle(Style.FILL);
			g.canvas.drawCircle(x * pixelPerMm, y * pixelPerMm, size * pixelPerMm, paint);
		}
		
		@Override
		public String toString() {
			return "Baddy [x=" + x + ", y=" + y + ", size=" + size + "]";
		}
	}
	
	
	public void draw(GraphicsObject g) {
		// draw world
		g.canvas.drawRGB(50, 50, 50);
		paint.setColor(android.graphics.Color.GREEN);
		paint.setStyle(Style.STROKE);
		g.canvas.drawRect(0f, 0f, mmWidth * pixelPerMm - 1, mmHeight * pixelPerMm - 1, paint);
		
		switch (state) {
		case RUNNING:	
			hero.draw(g);		
			heroHandle.draw(g);
			for (int i = 0; i < numBaddies; i++) {
				baddies[i].draw(g);
			}
		default: {}
		}
		mainText.draw(g);
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
		b.beingEaten = false;
		b.dead = false;
//		Log.i("game", String.format("Baddy #%d created.", numBaddies));
		numBaddies++;
		return true;
	}
	
	public void killGoneBaddies() {
		for (int i = numBaddies - 1; i >= 0; i--) {
			if ((baddies[i].x < -baddies[i].size) 
					|| baddies[i].dead) {
				for (int j = i; j < numBaddies-1; j++) {
					baddies[j].copyFrom(baddies[j+1]);
				}
//				Log.i("game", String.format("Baddy #%d deleted. %d left", i, numBaddies-1));				
				numBaddies--;
			}
		}
	}
	
	public void update(long msDeltaT) {
		if (!metrics_initialized) return;
		
		mainText.update(msDeltaT);
		switch (state) {
		case BEFORE_START:
			startGame();
			state = State.RUNNING;
			return;
		case RUNNING:
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
	
			targetX = Math.min(mmWidth - HeroHandle.HANDLE_X_OFFSET - hero.size - 0.1f, targetX);
			targetX = Math.max(0, targetX);
			targetY = Math.min(mmHeight, targetY);
			targetY = Math.max(0, targetY);
			
			heroHandle.setCoord(targetX, targetY);
			
			// move baddies
			for (int i = 0; i < numBaddies; i++) {
				Baddy b = baddies[i];
				b.x += b.vx * msDeltaT / 1000;
				b.y += b.vy * msDeltaT / 1000;
			}
			
			// Collision handling.
			for (int i = 0; i < numBaddies; i++) {
				Baddy b = baddies[i];
				if (hero.isTouching(b)) {
					if ((hero.size > b.size) && !b.beingEaten) {
						hero.eventEatsBaddy();
						b.eventGetsEaten();
					} 
					if (hero.size <= b.size) {
						hero.eventGetsEaten();
						b.eventEatsHero();
						if (hero.size < 1.5f) {
							state = State.GAME_OVER;
							mainText.showText("Game over", mmWidth / 2, mmHeight / 2, 1000 * 86400);
						}
					}
				}
			}
			
			killGoneBaddies();
			// create new baddies
			baddyCreationTimer.update(msDeltaT);
			if (baddyCreationTimer.triggered()) {
				addBaddy();
			}
			
			for (int i = 0; i < numBaddies; i++) {
				baddies[i].update(msDeltaT);
			}
			return;
		case GAME_OVER:
			return;
		}
	}
	
	private float dist(float x1, float y1, float x2, float y2) {
		return android.util.FloatMath.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	private void setHeroCoord(float mmX, float mmY) {
		targetX = mmX;
		targetY = mmY;
		heroHandle.setCoord(mmX, mmY);
	}
	
	public void setMetrics(float mmWidth, float mmHeight, float pixelPerMm) {
		this.mmWidth = mmWidth;
		this.mmHeight = mmHeight;
		this.pixelPerMm = pixelPerMm;
		
		pxpsHeroSpeed = mmpsHeroSpeed * pixelPerMm;  // calculated from pixel density, do not modify directly.
		metrics_initialized = true;
	}

	public void actionClick(float rawX, float rawY) {
		float mmX = rawX / pixelPerMm;
		float mmY = rawY / pixelPerMm;
		if (heroHandle.isWithin(mmX, mmY)) {
			control_locked = true;
			setTarget(rawX / pixelPerMm, rawY / pixelPerMm);
		}
	}
	
	public void actionRelease() {
		control_locked = false;
	}
	
	public void actionMoveTo(float rawX, float rawY) {
		if (control_locked) {
			setTarget(rawX / pixelPerMm, rawY / pixelPerMm);
		}
	}
	
	public void setTarget(float tx, float ty) {
		targetX = tx;
		targetY = ty;
	}
}
