package cz.expaobserver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Vector3 implements Parcelable {

  public static final Creator<Vector3> CREATOR = new Creator<Vector3>() {
    public Vector3 createFromParcel(Parcel source) {return new Vector3(source);}

    public Vector3[] newArray(int size) {return new Vector3[size];}
  };

  private double x, y, z;

  private boolean normalized = false;

  public Vector3() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Vector3(Parcel in) {
    x = in.readDouble();
    y = in.readDouble();
    z = in.readDouble();
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

  public Vector3 cross(Vector3 that) {
    return cross(this, that);
  }

  public static Vector3 cross(Vector3 a, Vector3 b) {
    return new Vector3(a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x);
  }

  public Vector3 x(double x) {
    this.x = x;

    normalized = false;
    return this;
  }

  public Vector3 y(double y) {
    this.y = y;

    normalized = false;
    return this;
  }

  public Vector3 z(double z) {
    this.z = z;

    normalized = false;
    return this;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

  public String toString() {
    return "[" + x + ", " + y + ", " + z + "]";
  }

  public Vector3 add(Vector3 a) {
    x += a.x;
    y += a.y;
    z += a.z;

    normalized = false;
    return this;
  }

  public Vector3 normalize() {
    if (!normalized) {
      forceNormalize();
    }

    return this;
  }

  private void forceNormalize() {
    scale(1.0d / length());
    normalized = true;
  }

  public Vector3 scale(double a) {
    x *= a;
    y *= a;
    z *= a;

    normalized = false;
    return this;
  }

  public float length() {
    return (float) Math.sqrt(dot(this, this));
  }

  public static float dot(Vector3 a, Vector3 b) {
    return (float) (a.x * b.x + a.y * b.y + a.z * b.z);
  }

  public float dot(Vector3 that) {
    return dot(this, that);
  }

  public boolean isNormalized() {
    return normalized;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(x);
    dest.writeDouble(y);
    dest.writeDouble(z);
  }
}
