package com.sirnommington.squid.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Application preferences. A simple wrapper around Android SharedPreferences.
 */
public class Preferences {
    private static final String IS_INITIALIZED = "isInitialized";
    private static final String SENT_GCM_TOKEN_TO_SERVER = "sentGcmTokenToServer";
    private static final String SQUID_ENDPOINT = "squidEndpoint";

    private static final String DEFAULT_SQUID_ENDPOINT = "http://www.sirnommington.com";

    private final SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        return this.sharedPreferences.getString(Preferences.SQUID_ENDPOINT, DEFAULT_SQUID_ENDPOINT);
    }

    /**
     * Sets the squid service endpoint.
     * TODO Create a dev activity with ability to set dev service endpoint
     * @param endpoint e.g. https://www.foo.com
     */
    private void setSquidEndpoint(String endpoint) {
        this.sharedPreferences.edit().putString(Preferences.SQUID_ENDPOINT, endpoint);
    }
}
