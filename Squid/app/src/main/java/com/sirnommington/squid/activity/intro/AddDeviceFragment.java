package com.sirnommington.squid.activity.intro;

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
import com.sirnommington.squid.activity.MainActivity;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.gcm.SquidRegistrationIntentService;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.AddDeviceResult;
import com.sirnommington.squid.services.squid.SquidService;

import org.json.JSONException;

import java.io.IOException;

public class AddDeviceFragment extends Fragment {
    private final AddDeviceFragment thiz = this;

    private GoogleSignIn googleSignIn;
    private boolean isReceiverRegistered;
    private BroadcastReceiver registrationBroadcastReceiver;
    private SquidService squidService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.googleSignIn = ((GoogleSignInProvider) this.getActivity()).getGoogleSignIn();
        final Preferences prefs = new Preferences(this.getActivity());
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

        final Intent intent = new Intent(this.getActivity(), SquidRegistrationIntentService.class);
        this.getActivity().startService(intent);

        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    /**
     * Adds this device to the Squid service.
     * @param gcmToken The device's GCM token on which it will be messaged.
     */
    private void addThisDevice(final String gcmToken) {
        new AsyncTask<String, Void, AddDeviceResult>() {
            @Override
            protected AddDeviceResult doInBackground(String... params) {
                final String idToken = googleSignIn.silentSignIn();
                try {
                    return thiz.squidService.addDevice(idToken, Build.MODEL, gcmToken);
                } catch(IOException e) {
                    return null;
                } catch(JSONException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(AddDeviceResult result) {
                String message;
                if(result == null) {
                    message = getResources().getString(R.string.add_device_error);
                } else if(result.deviceCreated) {
                    message = getResources().getString(R.string.add_device_added, Build.MODEL);
                } else {
                    message = getResources().getString(R.string.add_device_already_added);
                }
                Toast.makeText(thiz.getActivity(), message, Toast.LENGTH_LONG).show();

                if(result != null) {
                    final Intent main = new Intent(thiz.getActivity(), MainActivity.class);
                    main.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                    thiz.startActivity(main);
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

}
