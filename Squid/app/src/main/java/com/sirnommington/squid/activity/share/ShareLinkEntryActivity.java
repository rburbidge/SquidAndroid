package com.sirnommington.squid.activity.share;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.ActivityHelper;
import com.sirnommington.squid.activity.intro.IntroActivity;
import com.sirnommington.squid.activity.intro.IntroStep;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Launched when the user shares a URL from the browser. This activity is just an entry point for the share link flow.
 * It is never shown to the user, and merely launches the correct activity based on application status.
 */
public class ShareLinkEntryActivity extends AppCompatActivity {
    private static final String TAG = ShareLinkEntryActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                launchCorrectActivity();
                return null;
            }
        }.execute();
    }

    /**
     * Launches the correct activity based on the state of the app.
     *
     * This is launched on a background thread due to use of silentSignIn().
     */
    private void launchCorrectActivity() {
        final Preferences preferences = new Preferences(this);

        // Determine if we need to launch the IntroActivity for any reason
        Integer introStep = null;
        if (!preferences.isInitialized()) {
            introStep = new Integer(IntroStep.DESCRIPTION);
        } else {
            final boolean isSignedIn = new GoogleSignIn(this).silentSignIn() != null;
            if (!isSignedIn) {
                introStep = new Integer(IntroStep.SIGN_IN);
            }
        }

        if (introStep != null) {
            // Launch the intro activity if required
            // TODO: At the moment this does not send the URL once the intro flow is complete, however we want to enable
            // that int the future.
            final Intent squidIntro = new Intent(this, IntroActivity.class);
            squidIntro.putExtra(IntentExtras.INTRO_STEP, introStep.intValue());
            this.startActivity(squidIntro);
        } else {
            // Otherwise, open ShareLinkActivity
            final String url = ActivityHelper.getStringExtra(this, TAG, Intent.EXTRA_TEXT);
            startActivity(ShareLinkActivity.createShareLinkIntent(this, url));
        }

        this.finish();
    }
}
