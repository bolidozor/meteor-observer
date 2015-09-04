/**
 *
 */
package cz.expaobserver.ui;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import cz.expaobserver.BuildConfig;
import cz.expaobserver.R;

/**
 * Instances of class {@code SettingsFragment} represent...
 *
 * @author pechanecjr
 * @version 0.0.0000 - 5. 9. 2014
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String KEY_PREF_OBSERVER_ID = "pref_observer_id";
    public static final String KEY_PREF_UPLOAD_SERVER = "pref_upload_server";

    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        addPreferencesFromResource(R.xml.preferences);

        findPreference(KEY_PREF_UPLOAD_SERVER).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object o) {
                String s = (String) o;
                if ("".equals(s)) s = null;
                getPreferenceManager().getSharedPreferences().edit().putString(KEY_PREF_UPLOAD_SERVER, s).apply();
                return true;
            }
        });

        if (!BuildConfig.DEBUG) {
            getPreferenceScreen().removePreference(findPreference(KEY_PREF_UPLOAD_SERVER));
        }
    }
}
