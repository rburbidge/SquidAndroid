package com.sirnommington.squid.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sirnommington.squid.R;
import com.sirnommington.squid.services.squid.contracts.Device;

/**
 * Application preferences. A simple wrapper around Android SharedPreferences.
 */
public class Preferences {
    private static final String IS_DEV_MODE = "isDevMode";
    private static final String IS_INITIALIZED = "isInitialized";
    private static final String SENT_GCM_TOKEN_TO_SERVER = "sentGcmTokenToServer";
    private static final String THIS_DEVICE= "thisDevice";

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

    /**
     * Whether or not the user has blocked received commands/data from being acted upon.
     * Used in the case where the user is being spammed be someone on another device that is logged into their account.
     */
    public boolean blockNotifications() {
        return this.sharedPreferences.getBoolean(this.context.getString(R.string.pref_block_notifications), false);
    }

    public boolean isInitialized() {
        return this.sharedPreferences.getBoolean(IS_INITIALIZED, false);
    }

    public void setInitialized() {
        this.sharedPreferences.edit().putBoolean(IS_INITIALIZED, true).apply();
    }

    public void setGcmTokenSentToServer(boolean wasSentToServer) {
        this.sharedPreferences.edit().putBoolean(Preferences.SENT_GCM_TOKEN_TO_SERVER, wasSentToServer).apply();
    }

    public String getSquidEndpoint() {
        return getPreference(R.string.pref_squid_endpoint, R.string.squid_endpoint_default);
    }

    public Device getThisDevice() {
        final String jsonString = this.sharedPreferences.getString(THIS_DEVICE, null);
        if(jsonString == null) return null;

        final Gson gson = new Gson();
        return gson.fromJson(jsonString, Device.class);
    }

    public void setThisDevice(Device device) {
        final Gson gson = new Gson();
        this.sharedPreferences.edit().putString(THIS_DEVICE, gson.toJson(device, Device.class)).apply();
    }

    /**
     * Helper for getting string preferences using string resource IDs.
     */
    private String getPreference(int key, int defaultValue) {
        return this.sharedPreferences.getString(
            this.context.getResources().getString(key),
            this.context.getResources().getString(defaultValue));
    }

    /**
     * Sets developer mode as enabled.
     */
    public void enableDevMode() {
        this.sharedPreferences.edit().putBoolean(IS_DEV_MODE, true).apply();
    }

    /**
     * Returns true iff app is in dev mode.
     */
    public boolean isDevMode() {
        return this.sharedPreferences.getBoolean(IS_DEV_MODE, false);
    }
}
