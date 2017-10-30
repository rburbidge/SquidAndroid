package com.sirnommington.squid.activity.intro;

import android.content.Intent;

public class ActivityHelper {
    /**
     * Flags used when launching an activity and clearing the existing activity stack.
     */
    public static final int ACTIVITY_START_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
}
