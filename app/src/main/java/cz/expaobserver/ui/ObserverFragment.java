package cz.expaobserver.ui;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cz.expaobserver.background.ObserverLogic;
import cz.expaobserver.model.Vector3;

/**
 * Created by pechanecjr on 7. 12. 2014.
 */
public class ObserverFragment extends Fragment implements ObserverLogic.Callbacks {

    public static final String TAG = ObserverFragment.class.getSimpleName();

    private ObserverLogic mObserverLogic;
    private ObserverLogic.Callbacks mCallbacks;

    public static ObserverFragment newInstance() {
        return new ObserverFragment();
    }

    public ObserverFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mObserverLogic = ObserverLogic.getInstance((ObserverApplication) getActivity().getApplication());
        mObserverLogic.setCallbacks(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallbacks = (ObserverLogic.Callbacks) activity;
    }

    @Override
    public void onDetach() {
        mCallbacks = ObserverLogic.Callbacks.DUMMY;

        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onStart();

        mObserverLogic.startListening();
    }

    @Override
    public void onPause() {
        mObserverLogic.stopListening();

        super.onStop();
    }

    @Override
    public void onLocationChanged(final Location location) {
        mCallbacks.onLocationChanged(location);
    }

    @Override
    public void onStateChanged(@ObserverLogic.TrailMeasureState final int state) {
        mCallbacks.onStateChanged(state);
    }

    @Override
    public void onTimeChanged(final long time) {
        mCallbacks.onTimeChanged(time);
    }

    @Override
    public void onOrientationChanged(final Vector3 vector) {
        mCallbacks.onOrientationChanged(vector);
    }
}
