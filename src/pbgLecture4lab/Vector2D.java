package pbgLecture4lab;

import java.io.Serializable;

public final class Vector2D implements Serializable {
	// mutable 2D vectors

	public double x, y;

	// create a null vector
	public Vector2D() {
		this(0, 0);
	}

	// create vector with given coordinates
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	// create new vector that is a copy of the argument
	public Vector2D(Vector2D v) {
		this(v.x, v.y);
	}

	public void set(Vector2D v) {
		this.x = v.x;
		this.y = v.y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o) {
		if (o instanceof Vector2D) {
			Vector2D v = (Vector2D) o;
			return x == v.x && y == v.y;
		} else
			return false;
	}

	public double mag() {
		return Math.hypot(x, y);
	}

	public double angle() {
		return Math.atan2(y, x);
	}

	// angle of difference vector between this vector and other vector
	public double angle(Vector2D other) {
		return Math.atan2(other.y - y, other.x - x);
	}

	public String toString() {
		return "(" + String.format("%.01f", x) + "," + String.format("%.01f", y)
				+ ")";
	}

	public void add(Vector2D v) {
		this.x += v.x;
		this.y += v.y;
	}

	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}

	// scaled addition - surprisingly useful
	// note: vector subtraction can be expressed as scaled addition with factor
	// (-1)
	public void addScaled(Vector2D v, double fac) {
		this.x += v.x * fac;
		this.y += v.y * fac;
	}



	public void mult(double fac) {
		this.x *= fac;
		this.y *= fac;
	}

	public void rotate(double angle) {
		// add code here of your own to do a rotation, using the usual rotation matrix product (cos, -sin; sin, cos)(x;y)
		// Try not to copy this code from another source - try and implement it yourself by hand (ask for help if you need it).
		// We assume "angle" is in radians already!
                double c = Math.cos(angle);
                double s = Math.sin(angle);
                this.x = x*c - y*s;
                this.y = x*s + y*c;
	}

	public double scalarProduct(Vector2D v) {
		return x * v.x + y * v.y;
	}

	public double dist(Vector2D v) {
		return Math.hypot(x - v.x, y - v.y);
	}


	public void normalise() {
		double len = mag();
		x /= len;
		y /= len;
	}



	public static Vector2D minus(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x - v2.x, v1.y - v2.y);
	}


	public Vector2D rotate90degreesAnticlockwise() {
		return new Vector2D(-y,x);
	}
}
