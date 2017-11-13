package com.sirnommington.squid.services.gcm;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.sirnommington.squid.services.Preferences;

public class SquidGcmListenerService extends GcmListenerService {

    private static final String TAG = "SquidGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // If the user has blocked notifications, then do nothing
        final Preferences preferences = new Preferences(this);
        if(preferences.blockNotifications()) return;

        // Handle the message
        try {
            final String type = data.getString("type");
            if (GcmMessageType.URL.equals(type)) {
                final Intent openUrl = new Intent(Intent.ACTION_VIEW);
                openUrl.setData(Uri.parse(data.getString("data")));
                openUrl.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openUrl);

                playSound();
            } else {
                error("Unable to handle data=" + data);
            }
        } catch(Exception e) {
            error("onMessageReceived() failure. data=" + data, e);
        }
    }

    private void error(String message) {
        error(message, null);
    }

    private void error(final String message, Exception e) {
        Log.e(TAG, message, e);

        // Show toast on UI thread
        final Context thisContext = this;
        new Handler(Looper.getMainLooper()).post(
            new Runnable() {
                public void run() {
                    Toast.makeText(thisContext, message, Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void playSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
        r.play();
    }
}
