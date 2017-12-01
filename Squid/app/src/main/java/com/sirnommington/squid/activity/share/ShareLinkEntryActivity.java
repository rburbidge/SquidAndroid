package com.sirnommington.squid.activity.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.ActivityHelper;

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

        final String url = ActivityHelper.getStringExtra(this, TAG, Intent.EXTRA_TEXT);
        Intent launchIntent = new Intent();
        launchIntent.putExtra(IntentExtras.URL, url);
        launchIntent.setClass(getApplicationContext(), ShareLinkActivity.class);
        startActivity(launchIntent);

        finish();
    }
}
