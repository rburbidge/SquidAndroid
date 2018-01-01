package com.sirnommington.squid.activity.intro.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.Actions;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.activity.common.Delay;
import com.sirnommington.squid.activity.common.GoogleSignInProvider;
import com.sirnommington.squid.activity.fragment.ProgressFragment;
import com.sirnommington.squid.activity.intro.IntroListener;
import com.sirnommington.squid.common.StringUtil;
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
public class AddDeviceFragment extends ProgressFragment {
    private static final String TAG = AddDeviceFragment.class.getSimpleName().toString();

    private final AddDeviceFragment thiz = this;

    private boolean isReceiverRegistered;
    private BroadcastReceiver registrationBroadcastReceiver;
    private SquidService squidService;
    private EditText deviceName;
    private String gcmToken;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_device, container, false);
        this.deviceName = view.findViewById(R.id.device_name);
        this.deviceName.setText(Build.MODEL);
        this.deviceName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Also register device when user hits enter on keyboard
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    registerDevice();
                }
                return false;
            }
        });
        final Button addDeviceButton = view.findViewById(R.id.register_device_button);
        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDevice();
            }
        });

        final GoogleSignIn googleSignIn = ((GoogleSignInProvider) this.getActivity()).getGoogleSignIn();
        final Preferences prefs = new Preferences(this.getActivity());
        this.squidService = new SquidService(prefs.getSquidEndpoint(), googleSignIn);

        this.initGcmRegistration();

        return view;
    }

    @Override
    public int getLoadingTextId() {
        return R.string.add_device_in_progress;
    }

    /**
     * Initializes retrieval of the GCM token.
     */
    private void initGcmRegistration() {
        // When retrieving GCM token completes, register the device with the Squid service
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // GCM token is available from BroadcastReceiver intent, NOT MainActivity intent
                gcmToken = intent.getStringExtra(IntentExtras.GCM_TOKEN);
            }
        };
        registerReceiver();
        final Intent intent = new Intent(this.getActivity(), SquidRegistrationIntentService.class);
        this.getActivity().startService(intent);
    }

    /**
     * Begins device registration. Shows an error to the user if either the device name is invalid, or GCM token is
     * unavailable.
     */
    private void registerDevice() {
        final String deviceName = this.deviceName.getText().toString();
        final String gcmToken = this.gcmToken;

        Integer errorStringId;
        if(StringUtil.isNullOrWhitespace(deviceName)) {
            errorStringId = R.string.add_device_name_invalid;
        } else if(StringUtil.isNullOrWhitespace(gcmToken)) {
            errorStringId = R.string.add_device_error;
            Log.e(TAG, "GCM token was unavailable when user attempted to register device");
        } else {
            errorStringId = null;
        }

        if(errorStringId != null) {
            Toast.makeText(getActivity(), errorStringId.intValue(), Toast.LENGTH_LONG).show();
        } else {
            this.registerDeviceImpl(deviceName, gcmToken);
        }
    }

    /**
     * Adds this device to the Squid service.
     * @param gcmToken The device's GCM token on which it will be messaged.
     */
    private void registerDeviceImpl(final String deviceName, final String gcmToken) {
        new AsyncTask<String, Void, AsyncResponse<InitializeResult>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected AsyncResponse<InitializeResult> doInBackground(String... params) {
                // Delay the add device operation to a minimum of 3s so that the fragment doesn't disappear too quickly
                final int minMillisToAddDevice = 3000;
                return Delay.delay(new Delay.Run<AsyncResponse<InitializeResult>>() {
                    @Override
                    public AsyncResponse<InitializeResult> run() {
                        return thiz.initializeApp(deviceName, gcmToken);
                    }
                }, minMillisToAddDevice);
            }

            @Override
            protected void onPostExecute(AsyncResponse<InitializeResult> result) {
                String message;
                if(result == null || result.error != null) {
                    message = getResources().getString(R.string.add_device_error);
                    showLoading(false);
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
    private AsyncResponse<InitializeResult> initializeApp(final String deviceName, final String gcmToken) {
        final InitializeResult result = new InitializeResult();

        // Register this device
        AsyncResponse<AddDeviceResult> addDeviceResult = thiz.squidService.addDevice(deviceName, gcmToken);
        if(addDeviceResult.error != null) {
            return AsyncResponse.createError(addDeviceResult.error);
        }
        result.deviceAdded = addDeviceResult.payload.deviceCreated;

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
