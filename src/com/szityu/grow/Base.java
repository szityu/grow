package com.szityu.grow;

public class Base {
	public static float dist(float x1, float y1, float x2, float y2) {
		return android.util.FloatMath.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}

}
