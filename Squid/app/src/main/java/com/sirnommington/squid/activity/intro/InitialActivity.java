package com.sirnommington.squid.activity.intro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.MainActivity;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Shows the Squid logo. If the user is signed in and the application has been initialized, sends the user to the
 * MainActivity. Otherwise, sends the user to the SquidDescriptionActivity.
 */
public class InitialActivity extends AppCompatActivity {

    private final InitialActivity thiz = this;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        mGoogleApiClient = GoogleSignIn.Create(this);

        // Execute background task to determine if the app is initialized and the user is already signed in
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                Preferences preferences = new Preferences(thiz);
                if(!preferences.getIsInitialized()) {
                    Intent squidDescription = new Intent(thiz, SquidDescriptionActivity.class);
                    squidDescription.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                    thiz.startActivity(squidDescription);
                    return null;
                }

                OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                handleSignInResult(pendingResult.await());
                return null;
            }
        }.execute();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (!result.isSuccess()) {
            Intent signIn = new Intent(this, SignInActivity.class);
            signIn.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
            this.startActivity(signIn);
            return;
        }

        // Start the main activity, and close the current one
        // Pass the user's server token to the main activity
        GoogleSignInAccount account = result.getSignInAccount();
        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
        main.putExtra(IntentExtras.GOOGLE_ID_TOKEN, account.getIdToken());
        this.startActivity(main);
    }
}
