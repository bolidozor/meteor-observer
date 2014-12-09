package cz.expaobserver.model;

import java.util.Locale;

import cz.expaobserver.util.DateUtils;

public class Record
{
	public Record()
	{
		id = -1;
		locLat = Double.NaN;
		locLong = Double.NaN;
	}

	private static String getAngles(Vector3 v) {
		double azimuth = Math.atan2(v.y(), v.x()) * 180.0 / Math.PI;
		double xyLength = Math.sqrt(v.x() * v.x() + v.y() * v.y());
		double elevation = Math.atan2(v.z(), xyLength) * 180.0 / Math.PI;
		return String.format(Locale.getDefault(), "%.2f\u00B0, %.2f\u00B0", azimuth, elevation);
	}

	public String toString()
	{
		return DateUtils.DATETIME_FORMATTER.print(time)
				+ "\nStart: " + getAngles(trailBeg) + "\nEnd: " + getAngles(trailEnd)
				+ (note == null ? "" : "\n" + note);
	}

	public long id;
	public long time;
	public Vector3 trailBeg;
	public Vector3 trailEnd;
	public double locLat;
	public double locLong;
	public String note;
}
