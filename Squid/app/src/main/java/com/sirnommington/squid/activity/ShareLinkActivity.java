package com.sirnommington.squid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Handles the share link command from Chrome browser.
 */
public class ShareLinkActivity extends AppCompatActivity {
    private static final String TAG = ShareLinkActivity.class.getSimpleName();

    final ShareLinkActivity thiz = this;

    private GoogleSignIn googleSignIn;
    private SquidService squidService;
    private DevicesAdapter devicesAdapter;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.url = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);

        final Preferences preferences = new Preferences(this);
        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());
        this.devicesAdapter = new DevicesAdapter(this);

        setContentView(R.layout.activity_share_link);

        final GridView devices = (GridView) this.findViewById(R.id.devices);
        devices.setAdapter(this.devicesAdapter);
        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int viewType = devicesAdapter.getItemViewType(position);
                switch(viewType) {
                    case DevicesAdapter.VIEW_TYPE_DEVICE:
                        final DeviceModel device = (DeviceModel) devicesAdapter.getItem(position);
                        sendLink(device);
                        break;
                    case DevicesAdapter.VIEW_TYPE_ADD_DEVICE:
                        // Show AddOtherDevice activity
                        break;
                    default:
                        // TODO Log this error in telemetry
                        Log.e(TAG, "OnItemClickListener cannot handle view type: " + viewType);
                }
            }
        });

        this.getDevices();
    }

    /**
     * Retrieves the user's devices from the server.
     * TODO Cache the user's devices on the device so that they show up faster.
     */
    private void getDevices() {
        new GetDevicesTask(this.googleSignIn, this.squidService) {
            @Override
            protected void onPostExecute(AsyncResponse<Collection<DeviceModel>> response) {
                super.onPostExecute(response);
                if(response.error != null) {
                    showError(getResources().getString(R.string.get_devices_error));
                } else {
                    thiz.devicesAdapter.setDevices(response.payload);
                }
            }
        }.execute();
    }

    /**
     * Sends the URL to the device specified, and then closes the activity.
     */
    private void sendLink(final DeviceModel device) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String accessToken = googleSignIn.silentSignIn();
                try {
                    squidService.sendUrl(accessToken, device.id, url);
                    finish();
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
        Toast.makeText(thiz, error, Toast.LENGTH_LONG).show();
    }
}
