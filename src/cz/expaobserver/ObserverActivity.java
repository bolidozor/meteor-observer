package cz.expaobserver;

import java.lang.*;
import java.util.*;
import android.os.*;
import android.view.*;
import android.app.*;
import android.text.*;
import android.media.*;
import android.widget.*;
import android.location.*;
import android.hardware.*;
import android.content.*;
import android.content.pm.*;

public class ObserverActivity extends Activity implements SensorEventListener, LocationListener
{
	class StampedVector3
	{
		StampedVector3(long time, Vector3 vector)
		{
			this.time = time;
			this.vector = vector;
		}

		long time;
		Vector3 vector;
	}

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;

	private LocationManager locationManager;

	private ToneGenerator toneGen;

	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;

	private LinearLayout layout;

	long bootTime;
	Location lastLocation;

	long lastUpdate;

	private enum State { IDLE, TRAIL_BEG, TRAIL_END };
	State state;

	private float accValues[];
	private float mmValues[];

	Record currRecord;
	private RecordStore recordStore;

	private LinkedList<StampedVector3> oriHistory;
	private LinkedList<StampedVector3> accHistory;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.observer);

		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
											"Meteor Observer");

		toneGen = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		recordStore = ((ObserverApplication) getApplication()).getRecordStore();

		bootTime = -1;
		lastLocation = null;
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.observer_options, menu);
		return true;
	}

	protected void onResume() {
		super.onResume();

		oriHistory = new LinkedList<StampedVector3>();
		accHistory = new LinkedList<StampedVector3>();
		currRecord = new Record();

		changeState(State.IDLE);

		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		wakeLock.acquire();
	}

	protected void onPause() {
		super.onPause();

		wakeLock.release();

		locationManager.removeUpdates(this);
		sensorManager.unregisterListener(this);
	}

	private float historyDeviation(Queue<StampedVector3> history) {
		Vector3 avg = new Vector3();

		for (StampedVector3 sVec : history)
			avg.add(sVec.vector);

		avg.normalize();

		float dev = 0;

		for (StampedVector3 sVec : history)
			dev += (float) Math.acos(Vector3.dot(sVec.vector, avg));

		return dev / history.size();
	}

/*
	private float historyDeviation2(Queue<StampedVector3> history) {
		Vector3 avg = new Vector3();

		for (StampedVector3 sVec : history)
			avg.add(sVec.vector);

		avg.scale(-1.0f / history.size());

		float dev = 0;

		for (StampedVector3 sVec : history) {
			Vector3 diff = new Vector3();
			diff.add(sVec.vector);
			diff.add(avg);
			float len = Vector3.dot(diff, diff);
			if (len > dev)
				dev = len;
		}

		//return dev / history.size();
		return dev;
	}
*/
/*
	private static float historyTrajectory(Queue<StampedVector3> history) {
		float sum = 0;

		Iterator<StampedVector3> it = history.iterator();

		if (!it.hasNext())
			return 0;
		Vector3 prev = it.next().vector;

		while (it.hasNext()) {
			Vector3 v = it.next().vector;

			Vector3 diff = new Vector3();
			diff.add(prev);
			diff.scale(-1);
			diff.add(v);

			sum += diff.length();

			prev = v;
		}

		return sum;
	}
*/

	private static Vector3 recentAverage(LinkedList<StampedVector3> history,
											long since) {
		Vector3 avg = new Vector3();

		/* can be improved for API >= 9 */
		Iterator<StampedVector3> it = history.iterator();
		while (it.hasNext()) {
			StampedVector3 sVec = it.next();

			if (sVec.time < since)
				continue;

			avg.add(sVec.vector);
		}

		avg.normalize();
		return avg;
	}

	/* static */
	private Vector3 getOrientation() {
		/* TODO */
		Vector3 g = new Vector3(accValues[0], accValues[1], accValues[2]);
		Vector3 m = new Vector3(mmValues[0], mmValues[1], mmValues[2]);

		g.normalize();
		m.normalize();

		Vector3 c1 = Vector3.cross(m, g);
		c1.normalize();
		Vector3 c2 = Vector3.cross(g, c1);

		return new Vector3(c2.y, c1.y, g.y);
	}

	private static void clearHistory(Queue<StampedVector3> history, long since)
	{
		while (history.peek().time < since)
			history.poll();
	}

	public void changeState(State state)
	{
		this.state = state;

		TextView instructions = (TextView) findViewById(R.id.instruct);

		switch (state) {
			case IDLE:
				instructions.setText("When you see a meteor, tilt your phone over."
										+ " You will hear a beep.");
				break;

			case TRAIL_BEG:
				instructions.setText("Point toward the beginning of the trail.");
				break;

			case TRAIL_END:
				instructions.setText("Point toward the end of the trail.");
				break;
		}
	}

	public void update() {
		Vector3 g = new Vector3(accValues[0], accValues[1], accValues[2]);
		Vector3 m = new Vector3(mmValues[0], mmValues[1], mmValues[2]);

		Vector3 v = getOrientation();

		String vecString = String.format("[%.2f %.2f %.2f]", v.x, v.y, v.z);
		((TextView) findViewById(R.id.ori)).setText(vecString);

		long rtNow = SystemClock.elapsedRealtime();

		oriHistory.add(new StampedVector3(rtNow, v));
		accHistory.add(new StampedVector3(rtNow, g));

		clearHistory(oriHistory, rtNow - 500);
		clearHistory(accHistory, rtNow - 500);

		long now;

		if (bootTime == -1)
			now = System.currentTimeMillis();
		else
			now = bootTime + rtNow;

		String timeStr = (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss",
														new java.util.Date(now));
		((TextView) findViewById(R.id.time)).setText(timeStr);

		float deviation = historyDeviation(oriHistory);

		switch (state) {
			case IDLE:
				Vector3 a = new Vector3(accHistory.getFirst().vector);
				Vector3 b = new Vector3(accHistory.getLast().vector);
				a.normalize();
				b.normalize();
				float dot = Vector3.dot(a, b);

				if (dot < -0.5 && (now - lastUpdate) > 2000) {
					currRecord.time = now;

					if (lastLocation != null) {
						currRecord.locLat = lastLocation.getLatitude();
						currRecord.locLong = lastLocation.getLongitude();
					}

					changeState(State.TRAIL_BEG);
					toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
					lastUpdate = now;
				}
				break;

			case TRAIL_BEG:
				if (deviation < 0.05 && (now - lastUpdate) > 2000) {
					currRecord.trailBeg = recentAverage(oriHistory, rtNow - 300);
					changeState(State.TRAIL_END);
					toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
					lastUpdate = now;
				}
				break;

			case TRAIL_END:
				if (deviation < 0.05 && (now - lastUpdate) > 2000) {
					currRecord.trailEnd = recentAverage(oriHistory, rtNow - 300);
					changeState(State.IDLE);
					toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 100);
					lastUpdate = now;
					recordStore.addRecord(currRecord);
					currRecord = new Record();
				}
				break;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor == accelerometer)
			accValues = event.values.clone();

		if (event.sensor == magnetometer)
			mmValues = event.values.clone();

		if (accValues != null && mmValues != null)
			update();
	}

	public void onLocationChanged(Location location) {
		bootTime = location.getTime() - SystemClock.elapsedRealtime();
		lastLocation = location;

		((TextView) findViewById(R.id.time_desc)).setText("GPS Time:");
		((TextView) findViewById(R.id.loc)).setText("Lat: " + Location.convert(location.getLatitude(), Location.FORMAT_SECONDS)
													+ "\nLong: " + Location.convert(location.getLongitude(), Location.FORMAT_SECONDS));
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
	public void onProviderEnabled(String provider) {}
	public void onProviderDisabled(String provider) {}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.show_records:
				startActivity(new Intent(this, RecordsListActivity.class));
				return true;

			case R.id.gps_settings:
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				return true;

			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
