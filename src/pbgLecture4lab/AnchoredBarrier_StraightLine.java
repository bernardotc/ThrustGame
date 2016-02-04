package pbgLecture4lab;


import java.awt.Color;
import java.awt.Graphics2D;

public class AnchoredBarrier_StraightLine extends AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */

	private Vector2D startPos,endPos,unitNormal,unitTangent;
	private final Color col;
	private final double barrierLength;
	private final Double barrierDepth;


	public AnchoredBarrier_StraightLine(double startx, double starty, double endx, double endy, Color col) {
		this(startx, starty, endx, endy, col, null);
	}

	public AnchoredBarrier_StraightLine(double startx, double starty, double endx, double endy, Color col, Double barrierWidth) {
		startPos=new Vector2D(startx,starty);
		endPos=new Vector2D(endx,endy);
		
		Vector2D temp=Vector2D.minus(endPos,startPos);
		this.barrierLength=temp.mag();
		temp.normalise();
		setUnitTangent(temp);
		setUnitNormal(getUnitTangent().rotate90degreesAnticlockwise());
		//this.SCREEN_RADIUS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
		this.barrierDepth=barrierWidth;
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(startPos.x);
		int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(startPos.y);
		int x2 = BasicPhysicsEngine.convertWorldXtoScreenX(endPos.x);
		int y2 = BasicPhysicsEngine.convertWorldYtoScreenY(endPos.y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
	}

	@Override	
	public boolean isCircleCollidingBarrier(Vector2D circleCentre, double circleRadius) {
		Vector2D ap=Vector2D.minus(circleCentre, startPos);
		double distOnCorrectSideOfBarrierToCentre=ap.scalarProduct(getUnitNormal());
		double distAlongBarrier=ap.scalarProduct(getUnitTangent());
		// Note barrierDepth is type Double declared in constructor.  
		// barrierDepth null indicates infinite barrierDepth
		// barrierLength is ||AB||, declared in constructor.
		return distOnCorrectSideOfBarrierToCentre<=circleRadius && (barrierDepth==null || distOnCorrectSideOfBarrierToCentre>=-(barrierDepth+circleRadius))
				&& distAlongBarrier>=0 && distAlongBarrier<=barrierLength;
	}

	@Override
	public Vector2D calculateVelocityAfterACollision(Vector2D pos, Vector2D vel) {
		double vParallel=vel.scalarProduct(getUnitTangent());
		double vNormal=vel.scalarProduct(getUnitNormal());
		if (vNormal<0) // assumes normal points AWAY from wall... 
			vNormal=-vNormal;
		Vector2D result=new Vector2D(getUnitTangent());
		result.mult(vParallel);
		result.addScaled(getUnitNormal(), vNormal);
		return result;
	}

	public Vector2D getUnitNormal() {
		return unitNormal;
	}

	public void setUnitNormal(Vector2D unitNormal) {
		this.unitNormal = unitNormal;
	}

	public Vector2D getUnitTangent() {
		return unitTangent;
	}

	public void setUnitTangent(Vector2D unitTangent) {
		this.unitTangent = unitTangent;
	}

}
