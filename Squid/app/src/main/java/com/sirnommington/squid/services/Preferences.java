package com.sirnommington.squid.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sirnommington.squid.R;

/**
 * Application preferences. A simple wrapper around Android SharedPreferences.
 */
public class Preferences {
    private static final String IS_INITIALIZED = "isInitialized";
    private static final String SENT_GCM_TOKEN_TO_SERVER = "sentGcmTokenToServer";

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Resets the application to its default settings. Note that this will not reset the user's Google sign-in auth.
     */
    public void reset() {
        this.sharedPreferences.edit().clear().apply();
    }

    public boolean getIsInitialized() {
        return this.sharedPreferences.getBoolean(IS_INITIALIZED, false);
    }

    public void setInitialized() {
        this.sharedPreferences.edit().putBoolean(IS_INITIALIZED, true).apply();
    }

    public void setGcmTokenSentToServer(boolean wasSentToServer) {
        this.sharedPreferences.edit().putBoolean(Preferences.SENT_GCM_TOKEN_TO_SERVER, wasSentToServer).apply();
    }

    public String getSquidEndpoint() {
        return getPreference(R.string.pref_squid_endpoint, R.string.default_squid_endpoint);
    }

    /**
     * Helper for getting preferences using string resource IDs.
     */
    private String getPreference(int key, int defaultValue) {
        return this.sharedPreferences.getString(
            this.context.getResources().getString(key),
            this.context.getResources().getString(defaultValue));
    }
}
