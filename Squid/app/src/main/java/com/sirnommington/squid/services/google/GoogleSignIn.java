package com.sirnommington.squid.services.google;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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

    private final GoogleApiClient googleApiClient;

    /**
     * Constructor.
     * @param activity The activity from which the user is being silently signed-in.
     */
    public GoogleSignIn(FragmentActivity activity) {
        this.googleApiClient = create(activity);
    }

    /**
     * Creates a GoogleApiClient for signing in.
     * @param activity The activity in which to sign-in.
     */
    private static GoogleApiClient create(FragmentActivity activity) {
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
     * Signs the user in. The calling fragment must implement onActivityResult()
     * @param fragment The fragment that sign-in is being called from.
     * @param requestCode The request code to invoke onActivityResult() with.
     */
    public void signIn(Fragment fragment, int requestCode) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        fragment.startActivityForResult(signInIntent, requestCode);
    }

    /**
     * Silently signs the user in, returning the ID token; null if the user has not already signed in.
     * @return The ID token, or null.
     * @throws IllegalStateException if called on the UI thread (due to use of await())
     */
    public String silentSignIn() {
        final OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(this.googleApiClient);
        final GoogleSignInResult result = pendingResult.await();

        // Silent sign-in was not successful
        if (!result.isSuccess()) return null;

        // Success
        final GoogleSignInAccount account = result.getSignInAccount();
        googleApiClient.disconnect();
        return account.getIdToken();
    }
}
