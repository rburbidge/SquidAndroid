package com.sirnommington.squid.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.contracts.Device;
import com.sirnommington.squid.services.squid.SquidService;

/**
 * Shows device details and allows the user to delete the device or test sending a link.
 */
public class DeviceActivity extends AppCompatActivity implements RemoveConfirmationDialogListener {

    private Device device;
    private SquidService squidService;

    /**
     * Creates an intent to launch this activity for a given device.
     * @param context The app context.
     * @param device The device for which the activity is launched.
     */
    public static Intent createIntent(Context context, Device device) {
        final Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra(IntentExtras.DEVICE, device);
        return intent;
    }

    private void init(Device device) {
        this.device = device;

        final TextView title = this.findViewById(R.id.title);
        title.setText(device.name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        final Device device = (Device) getIntent().getSerializableExtra(IntentExtras.DEVICE);
        this.init(device);

        final Preferences preferences = new Preferences(this);
        final GoogleSignIn googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint(), googleSignIn);

        final Button sendLink = this.findViewById(R.id.send_link);
        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLink();
            }
        });
        final Button removeDevice = this.findViewById(R.id.remove_device);
        removeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDevice();
            }
        });
    }

    /**
     * Called when the user confirms/aborts device removal.
     * @param remove True if the user chose to remove the device.
     */
    @Override
    public void onRemoveConfirmComplete(boolean remove) {
        if(!remove) return;

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return squidService.removeDevice(device.id);
            }

            @Override
            protected void onPostExecute(Boolean removed) {
                super.onPostExecute(removed);

                if(!removed) {
                    showError(getResources().getString(R.string.device_remove_device_error, device.name));
                } else {
                    finish();
                }
            }
        }.execute();
    }

    /**
     * Shows a dialog to confirm device removal.
     */
    private void removeDevice() {
        final RemoveConfirmationDialog removeDialog = new RemoveConfirmationDialog();
        final Bundle bundle = new Bundle();
        bundle.putString(IntentExtras.DEVICE_NAME, this.device.name);
        removeDialog.setArguments(bundle);
        removeDialog.show(this.getSupportFragmentManager(), RemoveConfirmationDialog.TAG);
    }

    /**
     * Sends a link to the device. This allows the user to verify that their app is setup correctly.
     */
    private void sendLink() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // TODO Change the default URL
                return squidService.sendUrl(device.id, "https://google.com");
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if(!success) {
                    showError(getResources().getString(R.string.share_link_error, device.name));
                }
            }
        }.execute();
    }

    /**
     * Shows an error message to the user.
     */
    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
