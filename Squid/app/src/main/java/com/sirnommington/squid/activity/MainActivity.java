package com.sirnommington.squid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.squid.AddDeviceResult;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;
import com.sirnommington.squid.services.gcm.SquidRegistrationIntentService;
import com.sirnommington.squid.services.Preferences;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final MainActivity thiz = this;

    private String idToken;
    private Preferences preferences;
    private SquidService squidService;
    private boolean isReceiverRegistered;
    private BroadcastReceiver registrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.preferences = new Preferences(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());
        this.idToken = this.getIntent().getStringExtra(IntentExtras.GOOGLE_ID_TOKEN);

        // When retrieving GCM token completes, register the device with the Squid service
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // GCM token is available from BroadcastReceiver intent, NOT MainActivity intent
                final String gcmToken = intent.getStringExtra(IntentExtras.GCM_TOKEN);
                thiz.addThisDevice(gcmToken);
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        Intent intent = new Intent(this, SquidRegistrationIntentService.class);
        startService(intent);

        this.refreshDevices();
    }

    /**
     * Adds this device to the Squid service.
     * @param gcmToken The device's GCM token on which it will be messaged.
     */
    private void addThisDevice(final String gcmToken) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    final String deviceName = Build.MODEL;
                    final AddDeviceResult result = thiz.squidService.addDevice(thiz.idToken, deviceName, gcmToken);

                    if(result.deviceCreated) {
                        return getResources().getString(R.string.add_device_added, deviceName);
                    } else {
                        return getResources().getString(R.string.add_device_already_added);
                    }
                } catch(Exception e) {
                    Log.e(TAG, "Exception thrown while adding device: " + e.toString());
                    return getResources().getString(R.string.add_device_error);
                }
            }

            @Override
            protected void onPostExecute(String message) {
                super.onPostExecute(message);
                Toast.makeText(thiz, message, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    /**
     * Refreshes the list of devices.
     */
    private void refreshDevices() {
        new AsyncTask<Void, Void, AsyncResponse<Collection<DeviceModel>>>() {
            @Override
            protected AsyncResponse<Collection<DeviceModel>> doInBackground(Void... params) {
                try {
                    return new AsyncResponse(thiz.squidService.getDevices(thiz.idToken), null);
                } catch(Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(AsyncResponse<Collection<DeviceModel>> response) {
                super.onPostExecute(response);
                if(response.error != null) {
                    Toast.makeText(thiz, getResources().getString(R.string.get_devices_error), Toast.LENGTH_LONG);
                } else {
                    showDevices(response.payload);
                }
            }
        }.execute();
    }

    /**
     * Displays the list of devices.
     * @param devices The devices.
     */
    private void showDevices(Collection<DeviceModel> devices) {
        DevicesFragment devicesFragment = (DevicesFragment) this.getFragmentManager().findFragmentById(R.id.devices);
        devicesFragment.setDevices(devices);
    }

    /**
     * Adds this device to the Squid service.
     * @param gcmToken The device's GCM token on which it will be messaged.
     */
    private void addThisDevice(final String gcmToken) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    final String deviceName = Build.MODEL;
                    final AddDeviceResult result = squidService.addDevice(thiz.idToken, deviceName, gcmToken);

                    // Determine the result message
                    switch(result) {
                        case AlreadyExists:
                            return getResources().getString(R.string.add_device_already_added);
                        case Added:
                            return getResources().getString(R.string.add_device_added, deviceName);
                        default:
                            return getResources().getString(R.string.add_device_error);
                    }
                } catch(Exception e) {
                    Log.e(TAG, "Exception thrown while adding device: " + e.toString());
                    return getResources().getString(R.string.add_device_error);
                }
            }

            @Override
            protected void onPostExecute(String message) {
                super.onPostExecute(message);
                Toast.makeText(thiz, message, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(registrationBroadcastReceiver,
                    new IntentFilter(Actions.GCM_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
}
