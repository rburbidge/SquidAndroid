package com.sirnommington.squid.activity.prefs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;

/**
 * About screen for the app. Shows credits, etc.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
