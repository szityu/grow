package com.szityu.grow;

public class DebugStats {
	private float alpha;
	public float avg;
	
	public DebugStats(float alpha) {
		this.alpha = alpha;
		avg = 0f;
	}
	
	public void addData(float x) {
		avg *= 1-alpha;
		avg += alpha*x;
	}
}
