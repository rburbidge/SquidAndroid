package com.sirnommington.squid.services.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Listens for when the GCM token is refreshed and calls the registration service, which will  notify the
 * UI of the change.
 */
public class SquidInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, SquidRegistrationIntentService.class);
        startService(intent);
    }
}
