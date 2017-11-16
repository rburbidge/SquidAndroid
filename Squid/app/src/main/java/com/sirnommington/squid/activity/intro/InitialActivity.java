package com.sirnommington.squid.activity.intro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.MainActivity;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Shows the Squid logo. If the user is signed in and the application has been initialized, sends the user to the
 * MainActivity. Otherwise, sends the user to the SquidDescriptionActivity.
 */
public class InitialActivity extends AppCompatActivity {

    private final InitialActivity thiz = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        // Execute background task to determine if the app is initialized and the user is already signed in
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                final Preferences preferences = new Preferences(thiz);

                // 1. If the app has never been opened, then start the description activity
                if(!preferences.isInitialized()) {
                    Intent squidDescription = new Intent(thiz, SquidDescriptionActivity.class);
                    squidDescription.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                    thiz.startActivity(squidDescription);
                    return null;
                }

                // 2. The user has started the app, but hasn't completed sign-in before. Open the sign-in activity
                final String idToken = GoogleSignIn.silentSignIn(thiz);
                if(idToken == null) {
                    final Intent signIn = new Intent(thiz, SignInActivity.class);
                    signIn.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                    thiz.startActivity(signIn);
                    return null;
                }

                // 3. The user has signed-in and initialized the app before. Send them to the main activity
                final Intent main = new Intent(thiz, MainActivity.class);
                main.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                thiz.startActivity(main);
                return null;
            }
        }.execute();
    }
}
