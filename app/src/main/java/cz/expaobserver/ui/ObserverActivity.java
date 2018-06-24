package cz.expaobserver.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.expaobserver.R;
import cz.expaobserver.background.ObserverLogic;
import cz.expaobserver.model.Vector3;
import cz.expaobserver.util.ActivityUtils;
import cz.expaobserver.util.DateUtils;
import cz.expaobserver.util.Util;
import timber.log.Timber;

public class ObserverActivity extends AppCompatActivity implements ObserverLogic.Callbacks,
    ConfirmIntentDialogFragment.ConfirmIntentClient, LocationListener, SensorEventListener
        {

    private static final long DELAY_DIM = 2000;

    //@Bind(R.id.time) TextView mTimeText;
    //@Bind(R.id.loc) TextView mLocationText;
    //@Bind(R.id.instruct) TextView mInstructionsText;
    //@Bind(R.id.ori) TextView mOrientationText;
    //@Bind(R.id.toolbar) Toolbar mToolbar;
    private TextView mLocationText;
    private TextView mOrientationText;
    private TextView mTimeText;
    private TextView mInstructionsText;
    private Toolbar mToolbar;


    //TextView mLocationText = (TextView) findViewById(R.id.loc);

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private Sensor mMagneticSensor;
    private boolean mIntentNeedsConfirmation = true;

    private final Handler mHandler = new Handler();
    private final Runnable mDelayedDim = new Runnable() {
        @Override
        public void run() {
            dimSystemUi();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_observer);

        mLocationText = (TextView) findViewById(R.id.loc);
        mOrientationText = (TextView) findViewById(R.id.ori);
        mTimeText = (TextView) findViewById(R.id.time);
        mInstructionsText = (TextView) findViewById(R.id.instruct);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        //ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

        LocationManager locationManager = (LocationManager)
                getSystemService(this.getApplicationContext().LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
      //ActionBar ab = getSupportActionBar();
      //ab.setDisplayHomeAsUpEnabled(false);
      //ab.setHomeButtonEnabled(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(ObserverFragment.newInstance(), ObserverFragment.TAG).commit();
        }

        mSensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLightSensor == null) {
            Timber.d("No light sensor available.");
        }
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (mMagneticSensor == null) {
            Timber.d("No magnetometer available.");
        }
    }

    protected void onPause() {
//        if (mLightSensor != null) {
//            mSensorManager.unregisterListener(this);
//        }

        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        dimSystemUi();

        if (mMagneticSensor != null){
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_GAME);
        }

//        if (mLightSensor != null) {
//            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dimSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

            mHandler.removeCallbacks(mDelayedDim);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleSystemUi();
        }

        return super.onTouchEvent(event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.observer_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
//    MenuItemCompat.setShowAsAction(menu.findItem(R.id.dim_screen), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        Util.Material.tintMenu(menu, getSupportActionBar().getThemedContext());
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_records:
                startActivity(new Intent(this, RecordListActivity.class));
                return true;

            case R.id.gps_settings:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return true;

            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.dim_screen:
                ActivityUtils.setBrightness(this, 25);
                dimSystemUi();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void toggleSystemUi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }

        if (getWindow().getDecorView().getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE) {
            dimSystemUi();
        } else {
            showSystemUi();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            mHandler.postDelayed(mDelayedDim, DELAY_DIM);
        }
    }

    @Override
    public void onStateChanged(@ObserverLogic.TrailMeasureState int state) {
        switch (state) {
            case ObserverLogic.TrailMeasureState.IDLE:
                mInstructionsText.setText(getString(R.string.mo_instruction_init));
                break;

            case ObserverLogic.TrailMeasureState.TRAIL_START:
                mInstructionsText.setText(getString(R.string.mo_instruction_point_to_start));
                break;

            case ObserverLogic.TrailMeasureState.TRAIL_END:
                mInstructionsText.setText(getString(R.string.mo_instruction_point_to_end));
                break;
        }
    }

    @Override
    public void onTimeChanged(long time) {
        mTimeText.setText(DateUtils.DATETIME_FORMATTER.print(time));
    }

    @Override
    public void onOrientationChanged(Vector3 v) {
        String vecString = String.format(Locale.getDefault(), "[%.2f %.2f %.2f]", v.x(), v.y(), v.z());
        mOrientationText.setText(vecString);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationText.setText("Lat "
            + Location.convert(location.getLatitude(), Location.FORMAT_SECONDS) + "\nLong "
            + Location.convert(location.getLongitude(), Location.FORMAT_SECONDS));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void startActivity(final Intent intent) {
        if (mIntentNeedsConfirmation) {
            ConfirmIntentDialogFragment.validateIntent(this, intent);
        } else {
            superStartActivity(intent);
        }
    }

    @Override
    public void superStartActivity(final Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        onOrientationChanged(new Vector3(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}