package pbgLecture4lab;

import java.awt.Graphics2D;

public abstract class AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	public abstract Vector2D calculateVelocityAfterACollision(Vector2D pos, Vector2D vel);
	public abstract boolean isCircleCollidingBarrier(Vector2D circleCentre, double radius);
	public abstract void draw(Graphics2D g);

}