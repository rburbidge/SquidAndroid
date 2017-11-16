package com.sirnommington.squid.services.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.Actions;
import com.sirnommington.squid.services.Preferences;

/**
 * Handles the new GCM token, and notifies the UI on BroadcastListener that it has changed.
 *
 * Called either from the UI when the user is registering the device, or from the background when the device is notified
 * that the GCM token has expired.
 */
public class SquidRegistrationIntentService extends IntentService {

    private static final String TAG = "SquidRegIntentService";

    public SquidRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Preferences preferences = new Preferences(this);

        String token;
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM token retrieved");

            // TODO Implement this
            preferences.setGcmTokenSentToServer(true);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            preferences.setGcmTokenSentToServer(false);
            return;
        }

        if(token != null) {
            // Notify UI that registration has completed, so the progress indicator can be hidden
            Intent registrationComplete = new Intent(Actions.GCM_REGISTRATION_COMPLETE);
            registrationComplete.putExtra(IntentExtras.GCM_TOKEN, token);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }
}
