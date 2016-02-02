package cz.expaobserver.model;

import android.os.Parcel;

/**
 * Created by pechanecjr on 28. 11. 2014.
 */
public class StampedVector3 extends Vector3 {

  long mTime;

  public StampedVector3(Vector3 vector, long time) {
    super(vector);
    this.mTime = time;
  }

  public StampedVector3(StampedVector3 stampedVector) {
    this(stampedVector, stampedVector.time());
  }

  public long time() {
    return mTime;
  }

  public StampedVector3 time(long time) {
    this.mTime = time;
    return this;
  }

  @Override
  public int describeContents() { return 0; }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeLong(this.mTime);
  }

  private StampedVector3(Parcel in) {
    super(in);
    this.mTime = in.readLong();
  }

  public static final Creator<StampedVector3> CREATOR = new Creator<StampedVector3>() {
    public StampedVector3 createFromParcel(Parcel source) {return new StampedVector3(source);}

    public StampedVector3[] newArray(int size) {return new StampedVector3[size];}
  };
}
