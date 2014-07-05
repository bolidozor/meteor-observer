package cz.astrozor.meteorobserver;

import java.util.UUID;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ObserverApplication extends Application
{
	private RecordStore recordStore;

	public ObserverApplication() {
		super();
	}

	public void onCreate() {
		super.onCreate();

		setupId();

		recordStore = new RecordStore(this);
	}

	public RecordStore getRecordStore() {
		return recordStore;
	}

	private void setupId() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String observerId = sharedPrefs.getString(SettingsActivity.KEY_PREF_OBSERVER_ID, null);

		if (observerId == null) {
			observerId = "unnamed-" + UUID.randomUUID().toString();
			sharedPrefs.edit().putString(SettingsActivity.KEY_PREF_OBSERVER_ID, observerId).commit();
		}
	}
}
