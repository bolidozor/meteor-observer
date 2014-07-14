package cz.expaobserver;

import java.lang.Math;

public class Vector3 {
	public double x, y, z;

	public Vector3() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vector3 a) {
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
	}

	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	public void scale(double a) {
		x *= a;
		y *= a;
		z *= a;
	}

	public void add(Vector3 a) {
		x += a.x;
		y += a.y;
		z += a.z;
	}

	public static Vector3 cross(Vector3 a, Vector3 b) {
		return new Vector3(a.y * b.z - a.z * b.y,
    				a.z * b.x - a.x * b.z,
    				a.x * b.y - a.y * b.x);
	}

	public static float dot(Vector3 a, Vector3 b) {
		return (float) (a.x * b.x + a.y * b.y + a.z * b.z);
	}

	public float length() {
		return (float) Math.sqrt(dot(this, this));
	}

	public void normalize() {
		scale(1.0f / length());
	}
}
