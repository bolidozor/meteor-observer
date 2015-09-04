package cz.expaobserver.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.expaobserver.R;
import cz.expaobserver.model.Vector3;
import cz.expaobserver.util.ActivityUtils;
import cz.expaobserver.util.DateUtils;

public class ObserverActivity extends AppCompatActivity implements ObserverFragment.Callbacks,
    ConfirmIntentDialogFragment.ConfirmIntentClient {

    private static final long DELAY_DIM = 2000;

    @Bind(R.id.time)
    TextView mTimeText;
    @Bind(R.id.loc)
    TextView mLocationText;
    @Bind(R.id.instruct)
    TextView mInstructionsText;
    @Bind(R.id.ori)
    TextView mOrientationText;

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
        ButterKnife.bind(this);

//    ActionBar ab = getSupportActionBar();
//    ab.setDisplayHomeAsUpEnabled(false);
//    ab.setHomeButtonEnabled(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(ObserverFragment.newInstance(), ObserverFragment.TAG).commit();
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        dimSystemUi();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void dimSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

//    MenuItemCompat.setShowAsAction(menu.findItem(R.id.dim_screen), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void toggleSystemUi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }

        if (getWindow().getDecorView().getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE) {
            dimSystemUi();
        } else {
            showSystemUi();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            mHandler.postDelayed(mDelayedDim, DELAY_DIM);
        }
    }

    @Override
    public void onStateChanged(ObserverFragment.State state) {
        switch (state) {
            case IDLE:
                mInstructionsText.setText(getString(R.string.mo_instruction_init));
                break;

            case TRAIL_BEG:
                mInstructionsText.setText(getString(R.string.mo_instruction_point_to_start));
                break;

            case TRAIL_END:
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
    public void startActivity(final Intent intent) {
        ConfirmIntentDialogFragment.validateIntent(this, intent);
    }

    @Override
    public void superStartActivity(final Intent intent) {
        super.startActivity(intent);
    }
}
