package com.sirnommington.squid.activity.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

/**
 * Used to display Squid Service web content.
 *
 * Applies consistent settings to web-based experiences.
 */
public class WebViewActivity extends AppCompatActivity {

    private final String squidPath;

    /**
     * Constructor.
     * @param squidPath Forms the path portion of the URL. e.g. "/squid/instructions" in "https://endpoint.com/squid/instructions".
     */
    public WebViewActivity(String squidPath) {
        this.squidPath = squidPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        final Preferences preferences = new Preferences(this);

        final WebView webView = this.findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(preferences.getSquidEndpoint() + this.squidPath);
    }

    /**
     * Returns the top-level container for this activity.
     */
    protected View getLayout() {
        return this.findViewById(R.id.layout);
    }
}
