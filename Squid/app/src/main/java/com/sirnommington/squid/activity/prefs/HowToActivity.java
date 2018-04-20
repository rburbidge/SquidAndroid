package com.sirnommington.squid.activity.prefs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

public class HowToActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        final Preferences preferences = new Preferences(this);
        WebView webView = this.findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadUrl(preferences.getSquidEndpoint() + "/squid/instructions.html");
    }

}
