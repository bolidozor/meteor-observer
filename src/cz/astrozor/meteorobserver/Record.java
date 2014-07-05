package cz.astrozor.meteorobserver;

import android.text.format.DateFormat;
import java.util.Date;
import java.lang.String;
import java.lang.Math;

public class Record
{
	public Record()
	{
		id = -1;
		locLat = Double.NaN;
		locLong = Double.NaN;
	}

	private static String getAngles(Vector3 v) {
		double azimuth = Math.atan2(v.y, v.x) * 180.0 / Math.PI;
		double xyLength = Math.sqrt(v.x * v.x + v.y * v.y);
		double elevation = Math.atan2(v.z, xyLength) * 180.0 / Math.PI;
		return String.format("%.2f\u00B0, %.2f\u00B0", azimuth, elevation);
	}

	public String toString()
	{
		return ((String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss",
																new java.util.Date(time)))
				+ "\nStart: " + getAngles(trailBeg) + "\nEnd: " + getAngles(trailEnd);
	}

	public long id;
	public long time;
	public Vector3 trailBeg;
	public Vector3 trailEnd;
	public double locLat;
	public double locLong;
}
