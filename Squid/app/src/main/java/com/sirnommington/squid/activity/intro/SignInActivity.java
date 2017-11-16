package com.sirnommington.squid.activity.intro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sirnommington.squid.R;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Lets the user sign into the application using their Google account.
 */
public class SignInActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.googleApiClient = GoogleSignIn.Create(this);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.sign_in_button) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            this.handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (!result.isSuccess()) {
            return;
        }

        // Start the main activity, and close the current one
        // Pass the user's ID token to the main activity
        final Intent signedIn = new Intent(this, AddDeviceActivity.class);
        signedIn.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
        this.startActivity(signedIn);
        this.finish();
    }
}