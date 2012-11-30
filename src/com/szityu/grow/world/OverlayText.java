package com.szityu.grow.world;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.Log;

public class OverlayText implements WorldObject {
	public String text;
	public boolean visible;
	public long msCountDown;
	public float x;
	public float y;
		
	public OverlayText() {
		visible = false;
		msCountDown = 0;
	}
	
	public void showText(String text, float x, float y, long msDisplayTime) {
		Log.i("text", "Showing: " + text);
		this.text = text;
		visible = true;
		this.x = x;
		this.y = y;
		msCountDown = msDisplayTime;
	}
	
	public void draw(GraphicsObject g) {
		if (!visible) return;
		g.paint.setStyle(Paint.Style.FILL);
		g.paint.setColor(Color.WHITE);
		g.paint.setTextAlign(Align.CENTER);
		g.paint.setTypeface(Typeface.DEFAULT_BOLD);
		g.paint.setTextSize(10 * g.pixelPerMm);
		g.canvas.drawText(text, x * g.pixelPerMm, y * g.pixelPerMm, g.paint);
	}
	
	public void update(long msDeltaT) {
		if (!visible) return;
		if (msCountDown <= 0) visible = false;
		msCountDown -= msDeltaT;
	}
}
