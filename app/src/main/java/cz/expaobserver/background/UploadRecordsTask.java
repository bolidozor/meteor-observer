package cz.expaobserver.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import cz.expaobserver.model.Record;
import cz.expaobserver.ui.ObserverApplication;
import cz.expaobserver.ui.SettingsFragment;

public class UploadRecordsTask extends AsyncTask<Void, Integer, Boolean> {
    public interface Listener {
        public void onFinish();
    }

    ;

    private final Activity activity;
    private final ObserverApplication application;
    private final Listener listener;
    private List<Record> records;
    private ProgressDialog dialog;
    private String errorStr;

    public UploadRecordsTask(Activity activity, Listener listener,
                             List<Record> records) {
        super();
        this.activity = activity;
        this.application = (ObserverApplication) activity.getApplication();
        this.listener = listener;
        this.records = records;
    }

    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Uploading records...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(records.size());
        dialog.setProgress(0);
        dialog.show();
    }

    private Boolean uploadRecord(String serverAddr, String observerId, Record record) {
/*
        HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;

		try {
			String uri = "http://192.168.1.121:2000/report"
							+ "?time=" + record.time
							+ "&trail_beg=" + URLEncoder.encode(record.trailBeg.toString(), "utf-8")
							+ "&trail_end=" + URLEncoder.encode(record.trailEnd.toString(), "utf-8")
							+ "&loc_lat=" + record.locLat
							+ "&loc_long=" + record.locLong
							+ "&observer_id=" + observerId;

			Log.d("MeteorObserver", "uri: " + uri);

			response = httpclient.execute(new HttpGet(uri));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else {
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		Log.d("MeterObserver", "recv: " + responseString);
		return responseString == "ACK";
*/
        try {
            URL url = new URL(serverAddr
                + "?time=" + record.time
                + "&trail_beg=" + URLEncoder.encode(record.trailBeg.toString(), "utf-8")
                + "&trail_end=" + URLEncoder.encode(record.trailEnd.toString(), "utf-8")
                + "&loc_lat=" + record.locLat
                + "&loc_long=" + record.locLong
                + "&observer_id=" + observerId
                + (record.note == null ? ""
                : ("&note=" + URLEncoder.encode(record.note, "utf-8"))));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();

            connection.disconnect();

            errorStr = line;
            return line.equals("ACK");
        } catch (IOException e) {
            errorStr = e.toString();
            return false;
        }
    }

    protected Boolean doInBackground(Void... arg) {
        if (records.size() == 0)
            return true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
        String serverAddr = sharedPref.getString(SettingsFragment.KEY_PREF_UPLOAD_SERVER, "");
        String observerId = sharedPref.getString(SettingsFragment.KEY_PREF_OBSERVER_ID, "");
        try {
            observerId = URLEncoder.encode(observerId, "utf-8");
        } catch (Exception e) {}

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);

            if (!uploadRecord(serverAddr, observerId, record))
                return false;

            application.getRecordStore().deleteRecord(record);
            publishProgress(i + 1);
        }

        return true;
    }

    protected void onProgressUpdate(Integer... progress) {
        dialog.setProgress(progress[0]);
    }

    protected void onPostExecute(Boolean result) {
        dialog.dismiss();

        if (listener != null)
            listener.onFinish();

        if (!result) {
            AlertDialog dialog = new AlertDialog.Builder(activity).create();
            dialog.setTitle("Upload failed");
            dialog.setMessage(errorStr);
            dialog.show();
        }
    }
}
