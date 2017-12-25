package com.sirnommington.squid.activity.intro.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.Actions;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.activity.common.Delay;
import com.sirnommington.squid.activity.common.GoogleSignInProvider;
import com.sirnommington.squid.activity.intro.IntroListener;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.gcm.SquidRegistrationIntentService;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.AddDeviceResult;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Retrieves the GCM token for the user and registers their device with the service.
 */
public class AddDeviceFragment extends Fragment {
    private final AddDeviceFragment thiz = this;

    private boolean isReceiverRegistered;
    private BroadcastReceiver registrationBroadcastReceiver;
    private SquidService squidService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final GoogleSignIn googleSignIn = ((GoogleSignInProvider) this.getActivity()).getGoogleSignIn();
        final Preferences prefs = new Preferences(this.getActivity());
        this.squidService = new SquidService(prefs.getSquidEndpoint(), googleSignIn);

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

        final Intent intent = new Intent(this.getActivity(), SquidRegistrationIntentService.class);
        this.getActivity().startService(intent);

        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    /**
     * Adds this device to the Squid service.
     * @param gcmToken The device's GCM token on which it will be messaged.
     */
    private void addThisDevice(final String gcmToken) {
        new AsyncTask<String, Void, AsyncResponse<InitializeResult>>() {
            @Override
            protected AsyncResponse<InitializeResult> doInBackground(String... params) {
                // Delay the add device operation to a minimum of 3s so that the fragment doesn't disappear too quickly
                final int minMillisToAddDevice = 3000;
                return Delay.delay(new Delay.Run<AsyncResponse<InitializeResult>>() {
                    @Override
                    public AsyncResponse<InitializeResult> run() {
                        return thiz.initializeApp(gcmToken);
                    }
                }, minMillisToAddDevice);
            }

            @Override
            protected void onPostExecute(AsyncResponse<InitializeResult> result) {
                String message;
                if(result == null || result.error != null) {
                    message = getResources().getString(R.string.add_device_error);
                } else if(result.payload.deviceAdded) {
                    message = getResources().getString(R.string.add_device_added, Build.MODEL);
                } else {
                    message = getResources().getString(R.string.add_device_already_added);
                }
                Toast.makeText(thiz.getActivity(), message, Toast.LENGTH_LONG).show();

                if(result != null) {
                    ((IntroListener) getActivity()).addDeviceComplete(result.payload.hasOtherDevices);
                }
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(registrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(registrationBroadcastReceiver,
                    new IntentFilter(Actions.GCM_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }


    /**
     * Registers the device with the service, and checks if the user has an existing devices besides this one.
     * @param gcmToken This device's GCM token.
     */
    private AsyncResponse<InitializeResult> initializeApp(final String gcmToken) {
        final InitializeResult result = new InitializeResult();

        // Register this device
        // TODO Have addDevice return AsyncResponse
        try {
            AddDeviceResult addDeviceResult = thiz.squidService.addDevice(Build.MODEL, gcmToken);
            result.deviceAdded = addDeviceResult.deviceCreated;
        } catch(Exception e) {
            return AsyncResponse.createError(e);
        }

        // Check if the user has other registered devices
        AsyncResponse<Collection<DeviceModel>> devices = thiz.squidService.getDevices();
        if(devices.error != null) {
            return AsyncResponse.createError(devices.error);
        }

        // Size > 1 because this device is also included in the getDevices() response
        result.hasOtherDevices = devices != null && devices.payload.size() > 1;

        return AsyncResponse.create(result);
    }

    private class InitializeResult {
        /**
         * True if this device was newly registered; false if it was already registered.
         */
        public boolean deviceAdded;

        /**
         * True if the user has other devices registered.
         */
        public boolean hasOtherDevices;
    }
}
