package com.sirnommington.squid.activity;

import android.app.Fragment;

import com.sirnommington.squid.activity.common.AddOtherDeviceActivity;

/**
 * Request codes for startActivityForResult() calls from either {@link android.support.v7.app.AppCompatActivity} or
 * {@link Fragment}.
 * <p>Numerical assignments can be changed, but must be unique.</p>
 */
public class RequestCode {
    /**
     * For use with {@link com.sirnommington.squid.services.google.GoogleSignIn#signIn(Fragment, int)}
     */
    public static final int SIGN_IN = 1;

    /**
     * For use with {@link AddOtherDeviceActivity}
     */
    public static final int ADD_OTHER_DEVICE = 2;
}
