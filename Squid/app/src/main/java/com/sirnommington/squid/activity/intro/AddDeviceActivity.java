package com.sirnommington.squid.activity.intro;

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
import com.sirnommington.squid.activity.Actions;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.MainActivity;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.gcm.SquidRegistrationIntentService;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.AddDeviceResult;
import com.sirnommington.squid.services.squid.SquidService;

public class AddDeviceActivity extends AppCompatActivity {

    private static final String TAG = AddDeviceActivity.class.getSimpleName();

    private final AddDeviceActivity thiz = this;
    private SquidService squidService;
    private boolean isReceiverRegistered;
    private BroadcastReceiver registrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        final Preferences prefs = new Preferences(this);
        this.squidService = new SquidService(prefs.getSquidEndpoint());

        // When retrieving GCM token completes, register the device with the Squid service
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // GCM token is available from BroadcastReceiver intent, NOT MainActivity intent
                final String gcmToken = intent.getStringExtra(IntentExtras.GCM_TOKEN);
                thiz.addThisDevice(gcmToken);
            }
        };
        registerReceiver();

        final Intent intent = new Intent(this, SquidRegistrationIntentService.class);
        startService(intent);
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
                    final String idToken = GoogleSignIn.silentSignIn(thiz);
                    final AddDeviceResult result = thiz.squidService.addDevice(idToken, Build.MODEL, gcmToken);
                    if(result.deviceCreated) {
                        return getResources().getString(R.string.add_device_added, Build.MODEL);
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

                final Intent main = new Intent(thiz, MainActivity.class);
                main.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                thiz.startActivity(main);
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
