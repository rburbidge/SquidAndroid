package com.sirnommington.squid.services.google;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.sirnommington.squid.R;

/**
 * Helpers for signing into Google.
 */
public class GoogleSignIn {

    private static final String TAG = GoogleSignIn.class.getSimpleName();

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

    /**
     * Silently signs the user in, returning the ID token; null if the user has not already signed in.
     * @param activity The activity from which the user is being silently signed-in.
     * @return The ID token, or null.
     */
    public static String silentSignIn(AppCompatActivity activity) {
        final GoogleApiClient googleApiClient = Create(activity);
        final OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        final GoogleSignInResult result = pendingResult.await();

        // Silent sign-in was not successful
        if (!result.isSuccess()) return null;

        // Success
        final GoogleSignInAccount account = result.getSignInAccount();
        return account.getIdToken();
    }
}
