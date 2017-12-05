package com.sirnommington.squid.activity.share;

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
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.activity.common.GetDevicesTask;
import com.sirnommington.squid.activity.common.ActivityHelper;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Allows the user to send a URL to another device.
 */
public class ShareLinkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = ShareLinkActivity.class.getSimpleName();

    private GoogleSignIn googleSignIn;
    private SquidService squidService;
    private DevicesAdapter devicesAdapter;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.url = ActivityHelper.getStringExtra(this, TAG, IntentExtras.URL);

        final Preferences preferences = new Preferences(this);
        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());
        this.devicesAdapter = new DevicesAdapter(this);

        setContentView(R.layout.activity_share_link);

        final GridView devices = (GridView) this.findViewById(R.id.devices);
        devices.setAdapter(this.devicesAdapter);
        devices.setOnItemClickListener(this);

        this.getDevices();
    }

    /**
     * Handles clicks on the devices grid items.
     * * If device was clicked, sends the URL to the device.
     * * If add device was clicked, opens AddOtherDeviceActivity.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int viewType = this.devicesAdapter.getItemViewType(position);
        switch(viewType) {
            case DevicesAdapter.VIEW_TYPE_DEVICE:
                final DeviceModel device = (DeviceModel) this.devicesAdapter.getItem(position);
                this.sendLink(device);
                break;
            case DevicesAdapter.VIEW_TYPE_ADD_DEVICE:
                final Intent addOtherDevice = new Intent(getApplicationContext(), AddOtherDeviceActivity.class);
                this.startActivity(addOtherDevice);
                break;
            default:
                // TODO Log this error in telemetry
                Log.e(TAG, "OnItemClickListener cannot handle view type: " + viewType);
        }
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
                    devicesAdapter.setDevices(response.payload);
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
                final String idToken = googleSignIn.silentSignIn();
                try {
                    squidService.sendUrl(idToken, device.id, url);
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
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
