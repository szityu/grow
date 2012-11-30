package com.szityu.grow.world;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GraphicsObject {
	public float pixelPerMm;
	float pxpsHeroSpeed;
	float pxMaxX;
	float pxMaxY;
	public Paint paint;
	public Canvas canvas;

	GraphicsObject() {
		paint = new Paint();
	}
}
