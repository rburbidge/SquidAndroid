package com.sirnommington.squid.activity.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.ActivityHelper;
import com.sirnommington.squid.activity.intro.IntroActivity;
import com.sirnommington.squid.services.Preferences;

/**
 * Launched when the user shares a URL from the browser. This activity is just an entry point for the share link flow.
 * It is never shown to the user, and merely launches the correct activity based on application status.
 */
public class ShareLinkEntryActivity extends AppCompatActivity {
    private static final String TAG = ShareLinkEntryActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Preferences preferences = new Preferences(this);

        // User not initialized, open IntroActivity. This will walk them through the intro flow
        // TODO: At the moment this does not send the URL once the intro flow is complete, however we want to enable that
        // int the future.
        if(!preferences.isInitialized()) {
            final Intent squidIntro = new Intent(this, IntroActivity.class);
            this.startActivity(squidIntro);
            return;
        }

        // Otherwise, open ShareLinkActivity
        final String url = ActivityHelper.getStringExtra(this, TAG, Intent.EXTRA_TEXT);
        Intent launchIntent = new Intent(this, ShareLinkActivity.class);
        launchIntent.putExtra(IntentExtras.URL, url);
        startActivity(launchIntent);

        finish();
    }
}
