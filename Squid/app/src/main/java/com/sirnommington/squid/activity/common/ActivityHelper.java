package com.sirnommington.squid.activity.common;

import android.app.Activity;
import android.util.Log;

public class ActivityHelper {
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
