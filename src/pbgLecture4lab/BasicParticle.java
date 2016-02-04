package pbgLecture4lab;

import static pbgLecture4lab.BasicPhysicsEngine.DELTA_T;
import static pbgLecture4lab.BasicPhysicsEngine.GRAVITY;

import java.awt.Color;
import java.awt.Graphics2D;

public class BasicParticle {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	public final int SCREEN_RADIUS;

	private Vector2D pos;
	private Vector2D vel;
	private Vector2D totalForceThisTimeStep;
	private final double radius;
	private final double mass;
	private final double rollingFriction;
	public final Color col;

	private final boolean improvedEuler;

	

	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col, double mass, double rollingFriction) {
		setPos(new Vector2D(sx,sy));
		setVel(new Vector2D(vx,vy));
		this.radius=radius;
		this.rollingFriction=rollingFriction;
		this.mass=mass;
		this.improvedEuler=improvedEuler;
		this.SCREEN_RADIUS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
		this.totalForceThisTimeStep=new Vector2D();
	}

	public void update() {
		Vector2D acc;
		// Apply forces that always exist on particle:
		applyParticleWeight();
		if (rollingFriction!=0)
			// this particle has been told to slow down gradually due to rolling friction 
			applyBasicRollingFriction(rollingFriction);
		//calculate Acceleration using Newton's second law.
		acc=new Vector2D(0,0);
		acc.addScaled(totalForceThisTimeStep, 1/mass);// using a=F/m from Newton's Second Law
		
		
		if (improvedEuler) {
			Vector2D vel2=new Vector2D(getVel());
			Vector2D pos2=new Vector2D(getPos());
			pos2.addScaled(getVel(), DELTA_T);
			vel2.addScaled(acc, DELTA_T);
			Vector2D acc2=new Vector2D(acc);//assuming acceleration is constant
			// Note acceleration is NOT CONSTANT for distance dependent forces such as 
			// Hooke's law or newton's law of gravity, so this is BUG  
			// in this Improved Euler implementation.  
			// The whole program structure needs changing to fix this problem properly!
			vel2.add(getVel());
			vel2.mult(0.5);
			acc2.add(acc);
			acc2.mult(0.5);
			getPos().addScaled(vel2, DELTA_T);
			getVel().addScaled(acc2, DELTA_T);
		} else {
			// basic Euler
			getPos().addScaled(getVel(), DELTA_T);
			getVel().addScaled(acc, DELTA_T);
		}
	}

	private void applyParticleWeight() {
		Vector2D weightVector=new Vector2D(0,-GRAVITY*mass);// using formula weight = mass * 9.8, downwards
		applyForceToParticle(weightVector);
	}

	private void applyBasicRollingFriction(double amountOfRollingFriction) {
		Vector2D rollingFrictionForce=new Vector2D(getVel());
		rollingFrictionForce.mult(-amountOfRollingFriction*mass);
		applyForceToParticle(rollingFrictionForce);
	}

	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
		int y = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);
		g.setColor(col);
		g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
	}

	public double getRadius() {
		return radius;
	}

	public Vector2D getPos() {
		return pos;
	}

	public void setPos(Vector2D pos) {
		this.pos = pos;
	}

	public Vector2D getVel() {
		return vel;
	}

	public void setVel(Vector2D vel) {
		this.vel = vel;
	}

	public boolean collidesWith(BasicParticle p2) {
		Vector2D vecFrom1to2 = Vector2D.minus(p2.getPos(), getPos());
		boolean movingTowardsEachOther = Vector2D.minus(p2.getVel(), getVel()).scalarProduct(vecFrom1to2)<0;
		return vecFrom1to2.mag()<getRadius()+p2.getRadius() && movingTowardsEachOther;
	}

	public static void implementElasticCollision(BasicParticle p1, BasicParticle p2, double e) {
		if (!p1.collidesWith(p2)) throw new IllegalArgumentException();
		Vector2D vec1to2 = Vector2D.minus(p2.getPos(), p1.getPos());
		vec1to2.normalise();
		Vector2D tangentDirection=new Vector2D(vec1to2.y, -vec1to2.x);
		double v1n=p1.getVel().scalarProduct(vec1to2);
		double v2n=p2.getVel().scalarProduct(vec1to2);
		double v1t=p1.getVel().scalarProduct(tangentDirection);
		double v2t=p2.getVel().scalarProduct(tangentDirection);
		double approachSpeed=v2n-v1n;
		double j=p1.mass*p2.mass*(1+e)*-approachSpeed/(p1.mass+p2.mass);
		Vector2D v1=p1.getVel();
		v1.addScaled(vec1to2, -j/p1.mass);
		p1.setVel(v1);
		Vector2D v2=p2.getVel();
		v2.addScaled(vec1to2, j/p2.mass);
		p2.setVel(v2);
		
	}
	
	public void applyForceToParticle(Vector2D force) {
		// To calculate F_net, as used in Newton's Second Law,
		// we need to accumulate all of the forces and add them up
		totalForceThisTimeStep.add(force);
	}
	
	public void resetTotalForce() {
		totalForceThisTimeStep.set(0,0);
	}
	
}
