/**
 *
 */
package cz.expaobserver.ui;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import cz.expaobserver.BuildConfig;
import cz.expaobserver.R;

/**
 * Instances of class {@code SettingsFragment} represent...
 *
 * @author pechanecjr
 * @version 0.0.0000 - 5. 9. 2014
 */
public class SettingsFragment extends PreferenceFragment {
  public static final String KEY_PREF_OBSERVER_ID = "pref_observer_id";
  public static final String KEY_PREF_UPLOAD_SERVER = "pref_upload_server";

  public static SettingsFragment newInstance() {
    SettingsFragment f = new SettingsFragment();
    return f;
  }

  public SettingsFragment() {
  }

  @Override
  public void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);

    addPreferencesFromResource(R.xml.preferences);

    if (!BuildConfig.DEBUG) {
      getPreferenceScreen().removePreference(findPreference(KEY_PREF_UPLOAD_SERVER));
    }
  }
}
