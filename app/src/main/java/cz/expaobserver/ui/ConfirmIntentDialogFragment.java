package cz.expaobserver.ui;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import cz.expaobserver.BuildConfig;
import cz.expaobserver.R;

/**
 * @author Eugen on 4. 9. 2015.
 */
public class ConfirmIntentDialogFragment extends DialogFragment {

    private static final String TAG = ConfirmIntentDialogFragment.class.getSimpleName();

    public static final String EXTRA_INTENT_CONFIRMED = "intentConfirmed";

    Intent mIntent;

    public static <T extends AppCompatActivity & ConfirmIntentClient> void validateIntent(T activity, Intent intent) {
        boolean myPackageName = false;
        ComponentName component = intent.getComponent();
        if (component != null) {
            String packageName = component.getPackageName();
            if (packageName != null) {
                myPackageName = packageName.startsWith(BuildConfig.APPLICATION_ID);
            }
        }

        if (intent.hasExtra(EXTRA_INTENT_CONFIRMED) || myPackageName) {
            activity.superStartActivity(intent);
        } else {
            activity.getSupportFragmentManager().beginTransaction().add(newInstance(intent), TAG).commit();
        }
    }

    public static ConfirmIntentDialogFragment newInstance(Intent intent) {
        Bundle args = new Bundle();
        args.putParcelable("mIntent", intent);

        ConfirmIntentDialogFragment fragment = new ConfirmIntentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mIntent = args.getParcelable("mIntent");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        Intent i = new Intent(mIntent);
                        i.putExtra(EXTRA_INTENT_CONFIRMED, EXTRA_INTENT_CONFIRMED);
                        startActivity(i);
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
            .setPositiveButton(android.R.string.yes, listener)
            .setNegativeButton(android.R.string.no, listener)
            .setMessage(R.string.mo_confirm_intent_message)
            .create();
    }

    public interface ConfirmIntentClient {
        void superStartActivity(Intent intent);
    }
}
