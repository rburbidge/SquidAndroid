package com.sirnommington.squid.activity;

import android.app.Activity;
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
import com.sirnommington.squid.services.squid.SquidService;
import com.sirnommington.squid.services.gcm.SquidRegistrationIntentService;
import com.sirnommington.squid.services.Preferences;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String idToken = this.getIntent().getStringExtra(IntentExtras.GOOGLE_ID_TOKEN);
        final Preferences preferences = new Preferences(this);
        final Activity thiz = this;

        // When retrieving GCM token completes, register the device with the Squid service
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // GCM token is available from BroadcastReceiver intent, NOT MainActivity intent
                final String gcmToken = intent.getStringExtra(IntentExtras.GCM_TOKEN);

                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            final SquidService squid = new SquidService(preferences.getSquidEndpoint());
                            final String deviceName = Build.MODEL;

                            // Determine the result message
                            AddDeviceResult result = squid.addDevice(idToken, deviceName, gcmToken);
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
        };

        // Registering BroadcastReceiver
        registerReceiver();

        Intent intent = new Intent(this, SquidRegistrationIntentService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Actions.GCM_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
}
