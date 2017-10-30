package com.sirnommington.squid.services.google;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sirnommington.squid.R;

/**
 * Helpers for signing into Google.
 */
public class GoogleSignIn {

    private static final String TAG = "GoogleSignIn";

    /**
     * Creates a GoogleApiClient for signing in.
     * @param activity The activity in which to sign-in.
     */
    public static GoogleApiClient Create(AppCompatActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.outh_client_id))
                .build();
        return new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Google Sign-in connection failure: " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
}
