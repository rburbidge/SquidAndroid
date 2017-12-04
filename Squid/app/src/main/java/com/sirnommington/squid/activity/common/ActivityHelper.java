package com.sirnommington.squid.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class ActivityHelper {
    /**
     * Flags used when launching an activity and clearing the existing activity stack.
     */
    public static final int ACTIVITY_START_CLEAR_HISTORY = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

    /**
     * Gets a string extra for an Activity, ensuring that it is present and non-null.
     * @param activity The activity.
     * @param tag Tag included in log messages. e.g. The Activity class name.
     * @param name The string extra name.
     * @return The string value.
     * @throws NullPointerException if the extra was not found.
     */
    public static String getStringExtra(Activity activity, String tag, String name) {
        final String value = activity.getIntent().getStringExtra(name);
        if(value == null) {
            final String errorMessage = name + " cannot be null";
            Log.e(tag, errorMessage);
            throw new NullPointerException(errorMessage);
        }

        return value;
    }
}
