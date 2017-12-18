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
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

/**
 * Shows device details and allows the user to delete the device or test sending a link.
 */
public class DeviceActivity extends AppCompatActivity {

    private DeviceModel device;
    private SquidService squidService;

    public static Intent createIntent(Context context, DeviceModel device) {
        final Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra(IntentExtras.DEVICE, device);
        return intent;
    }

    private void init(DeviceModel device) {
        this.device = device;

        final TextView title = this.findViewById(R.id.title);
        title.setText(device.name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        final DeviceModel device = (DeviceModel) getIntent().getSerializableExtra(IntentExtras.DEVICE);
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
     * Removes this device from the service.
     */
    private void removeDevice() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    squidService.removeDevice(device.id);
                    finish();
                } catch (Exception e) {
                    showError(getResources().getString(R.string.device_remove_device_error, device.name));
                }
                return null;
            }
        }.execute();
    }

    /**
     * Sends a link to the device. This allows the user to verify that their app is setup correctly.
     */
    private void sendLink() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // TODO Change the default URL
                    squidService.sendUrl(device.id, "https://google.com");
                } catch (Exception e) {
                    showError(getResources().getString(R.string.share_link_error, device.name));
                }
                return null;
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
