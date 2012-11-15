package com.szityu.grow;

public class Timer {
	private long t;
	private long period;
	
	public Timer(long msPeriod) {
		t = 0;
		period = msPeriod;
	}
	
	public void update(long dt) {
		t += dt;
	}
	
	public boolean triggered() {
		if (t > period) {
			t = t % period;
			return true;
		}
		return false;
	}
}
