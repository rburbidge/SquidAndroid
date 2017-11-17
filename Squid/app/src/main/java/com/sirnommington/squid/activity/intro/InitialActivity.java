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
 * MainActivity. Otherwise, sends the user to the IntroActivity.
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

                // If the app has never been opened or the user is not signed in, then start the intro activity
                if(!preferences.isInitialized() || GoogleSignIn.silentSignIn(thiz) == null) {
                    Intent squidDescription = new Intent(thiz, IntroActivity.class);
                    squidDescription.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                    thiz.startActivity(squidDescription);
                    return null;
                }

                // The user has signed-in and initialized the app before. Start the main activity
                final Intent main = new Intent(thiz, MainActivity.class);
                main.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
                thiz.startActivity(main);
                return null;
            }
        }.execute();
    }
}
