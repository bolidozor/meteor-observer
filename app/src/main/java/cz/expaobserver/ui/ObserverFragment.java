package cz.expaobserver.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.Queue;

import cz.expaobserver.model.Record;
import cz.expaobserver.model.RecordStore;
import cz.expaobserver.model.StampedVector3;
import cz.expaobserver.model.Vector3;

/**
 * Created by pechanecjr on 7. 12. 2014.
 */
public class ObserverFragment extends Fragment implements SensorEventListener,
    LocationListener {

    public static final String TAG = ObserverFragment.class.getSimpleName();

    private static final Callbacks DUMMY_CALLBACKS = new Callbacks() {
        @Override
        public void onLocationChanged(Location location) {
            //
        }

        @Override
        public void onStateChanged(@TrailMeasureState int state) {
//
        }

        @Override
        public void onTimeChanged(long time) {
//
        }

        @Override
        public void onOrientationChanged(Vector3 vector) {
//
        }
    };

    private static final int TONE_DURATION = 100;
    private static final long DELTA1 = 500;
    private static final long DELTA2 = 300;

    private Location mLastLocation = null;
    private long mLastUpdate = -1;
    private @TrailMeasureState int mState;
    private Record mCurrentRecord;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private LocationManager mLocationManager;
    private ToneGenerator mToneGenerator;
    private float accValues[];
    private float mmValues[];
    private RecordStore mRecordStore;
    private LinkedList<StampedVector3> oriHistory;
    private LinkedList<StampedVector3> accHistory;

    private ObserverApplication mApp;

    private Callbacks mCallbacks = DUMMY_CALLBACKS;

    public static ObserverFragment newInstance() {
        return new ObserverFragment();
    }

    public ObserverFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mApp = (ObserverApplication) getActivity().getApplication();

        mToneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75);

        mSensorManager = (SensorManager) mApp.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mLocationManager = (LocationManager) mApp.getSystemService(Context.LOCATION_SERVICE);

        mRecordStore = mApp.getRecordStore();

        oriHistory = new LinkedList<>();
        accHistory = new LinkedList<>();
        mCurrentRecord = new Record();

        changeState(TrailMeasureState.IDLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        mCallbacks = DUMMY_CALLBACKS;

        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onStart();

        oriHistory = new LinkedList<>();
        accHistory = new LinkedList<>();
        mCurrentRecord = new Record();

        changeState(TrailMeasureState.IDLE);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onPause() {
        mLocationManager.removeUpdates(this);
        mSensorManager.unregisterListener(this);

        super.onStop();
    }

    public void changeState(@TrailMeasureState int state) {
        this.mState = state;

        mCallbacks.onStateChanged(state);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        mCallbacks.onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer)
            accValues = event.values.clone();

        if (event.sensor == mMagnetometer)
            mmValues = event.values.clone();

        if (accValues != null && mmValues != null)
            update();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

  /*
   * private float historyDeviation2(Queue<StampedVector3> history) { Vector3
   * avg = new Vector3();
   *
   * for (StampedVector3 sVec : history) avg.add(sVec.vector);
   *
   * avg.scale(-1.0f / history.size());
   *
   * float dev = 0;
   *
   * for (StampedVector3 sVec : history) { Vector3 diff = new Vector3();
   * diff.add(sVec.vector); diff.add(avg); float len = Vector3.dot(diff, diff);
   * if (len > dev) dev = len; }
   *
   * //return dev / history.size(); return dev; }
   */
  /*
   * private static float historyTrajectory(Queue<StampedVector3> history) {
   * float sum = 0;
   *
   * Iterator<StampedVector3> it = history.iterator();
   *
   * if (!it.hasNext()) return 0; Vector3 prev = it.next().vector;
   *
   * while (it.hasNext()) { Vector3 v = it.next().vector;
   *
   * Vector3 diff = new Vector3(); diff.add(prev); diff.scale(-1); diff.add(v);
   *
   * sum += diff.length();
   *
   * prev = v; }
   *
   * return sum; }
   */

    public void update() {
        Vector3 g = new Vector3(accValues[0], accValues[1], accValues[2]);
        // Vector3 m = new Vector3(mmValues[0], mmValues[1], mmValues[2]);

        Vector3 v = getOrientation();

        mCallbacks.onOrientationChanged(v);

//    long rtNow = SystemClock.elapsedRealtime();
        long now = System.currentTimeMillis();

        oriHistory.add(new StampedVector3(v, now));
        accHistory.add(new StampedVector3(g, now));

        clearHistory(oriHistory, now - DELTA1);
        clearHistory(accHistory, now - DELTA1);

        mCallbacks.onTimeChanged(now);

        float deviation = historyDeviation(oriHistory);

        switch (mState) {
            case TrailMeasureState.IDLE: {
                Vector3 a = new Vector3(accHistory.getFirst());
                Vector3 b = new Vector3(accHistory.getLast());
                a.normalize();
                b.normalize();
                float dot = Vector3.dot(a, b);

                if (dot < -0.5 && (now - mLastUpdate) > 2000) {
                    mCurrentRecord.time = now;

                    if (mLastLocation != null) {
                        mCurrentRecord.locLat = mLastLocation.getLatitude();
                        mCurrentRecord.locLong = mLastLocation.getLongitude();
                    }

                    changeState(TrailMeasureState.TRAIL_START);
                    mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, TONE_DURATION);
                    mLastUpdate = now;
                }
                break;
            }
            case TrailMeasureState.TRAIL_START: {
                if (deviation < 0.05 && (now - mLastUpdate) > 2000) {
                    mCurrentRecord.trailBeg = recentAverage(oriHistory, now - DELTA2);
                    changeState(TrailMeasureState.TRAIL_END);
                    mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, TONE_DURATION);
                    mLastUpdate = now;
                }
                break;
            }
            case TrailMeasureState.TRAIL_END: {
                if (deviation < 0.05 && (now - mLastUpdate) > 2000) {
                    mCurrentRecord.trailEnd = recentAverage(oriHistory, now - DELTA2);
                    changeState(TrailMeasureState.IDLE);
                    mToneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, TONE_DURATION);
                    mLastUpdate = now;
                    mRecordStore.addRecord(mCurrentRecord);
                    mCurrentRecord = new Record();
                }
                break;
            }
        }
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

        return new Vector3(c2.y(), c1.y(), g.y());
    }

    private static void clearHistory(Queue<StampedVector3> history, long since) {
        while (history.peek().time() < since)
            history.poll();
    }

    private float historyDeviation(Queue<StampedVector3> history) {
        Vector3 avg = new Vector3();

        for (StampedVector3 sVec : history)
            avg.add(sVec);

        avg.normalize();

        float dev = 0;

        for (StampedVector3 sVec : history)
            dev += (float) Math.acos(Vector3.dot(sVec, avg));

        return dev / history.size();
    }

    private static Vector3 recentAverage(LinkedList<StampedVector3> history, long since) {
        Vector3 avg = new Vector3();

    /* can be improved for API >= 9 */
        for (StampedVector3 sVec : history) {
            if (sVec.time() < since)
                continue;

            avg.add(sVec);
        }

        avg.normalize();
        return avg;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TrailMeasureState {
        int IDLE = 0;
        int TRAIL_START = 1;
        int TRAIL_END = 2;
    }

    public interface Callbacks {
        public void onLocationChanged(Location location);

        public void onStateChanged(@TrailMeasureState int state);

        public void onTimeChanged(long time);

        public void onOrientationChanged(Vector3 vector);
    }
}
