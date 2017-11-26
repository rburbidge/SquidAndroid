package com.sirnommington.squid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.SquidService;

/**
 * Handles the share link command from Chrome browser.
 */
public class ShareLink extends AppCompatActivity {
    private GoogleSignIn googleSignIn;
    private SquidService squidService;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.url = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);

        final Preferences preferences = new Preferences(this);
        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());

        setContentView(R.layout.activity_main);
        this.sendLink();
    }

    private void sendLink() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String accessToken = googleSignIn.silentSignIn();
                try {
                    squidService.sendUrl(accessToken, "db6aa74d-c0ee-4ddd-9863-ae575c13b4c9", url);

                } catch (Exception e) {
                    int i = 0;
                    i++;
                    // TODO Show error
                }
                return null;
            }
        }.execute();
    }
}
