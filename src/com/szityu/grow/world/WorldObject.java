package com.szityu.grow.world;

public interface WorldObject {
	public void update(long msDeltaT);
	public void draw(GraphicsObject g);
}
