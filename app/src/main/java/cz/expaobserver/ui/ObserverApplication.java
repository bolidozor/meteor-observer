package cz.expaobserver.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import java.util.UUID;

import cz.expaobserver.R;
import cz.expaobserver.model.RecordStore;

public class ObserverApplication extends Application {
  private static ObserverApplication sInstance;

  private RecordStore recordStore;

  private Drawable mTintedIcon;

  public ObserverApplication() {
    super();
  }

  public void onCreate() {
    super.onCreate();

    setInstance(this);

    // bugfix: theme is not accessible programatically usually
    setTheme(R.style.Theme_MeteorObserver);

    loadTintedIcon();

    setupId();
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    recordStore = new RecordStore(this);
  }

  public RecordStore getRecordStore() {
    return recordStore;
  }

  private void setupId() {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    String observerId = sharedPrefs.getString(SettingsFragment.KEY_PREF_OBSERVER_ID, null);

    if (observerId == null) {
      observerId = "unnamed-" + UUID.randomUUID().toString();
      sharedPrefs.edit().putString(SettingsFragment.KEY_PREF_OBSERVER_ID, observerId).commit();
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    loadTintedIcon();
  }

  public Drawable getTintedIcon() {
    return mTintedIcon;
  }

  private void loadTintedIcon() {
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(0);
    matrix.setScale(1f, 0f, 0f, 1f);
    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

    mTintedIcon = getResources().getDrawable(R.drawable.ic_launcher2).mutate();
    mTintedIcon.setColorFilter(filter);
  }

  private synchronized static void setInstance(ObserverApplication app) {
    sInstance = app;
  }

  public synchronized static ObserverApplication getInstance() {
    return sInstance;
  }
}
