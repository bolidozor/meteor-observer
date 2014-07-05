package cz.astrozor.meteorobserver;

import android.preference.*;
import android.app.*;
import android.os.*;

public class SettingsActivity extends PreferenceActivity {
	public static final String KEY_PREF_OBSERVER_ID = "pref_observer_id";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
