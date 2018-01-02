package com.sirnommington.squid.activity.prefs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;

/**
 * About screen for the app. Shows credits, etc.
 *
 * Shows dev options if the user touches the screen {@link #ENABLE_DEV_OPTIONS_TOUCH_COUNT} times.
 */
public class AboutActivity extends AppCompatActivity {

    private static final int ENABLE_DEV_OPTIONS_TOUCH_COUNT = 5;

    private final AboutActivity thiz = this;
    private int touchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_about);

        final Preferences preferences = new Preferences(this);

        final View layout = this.findViewById(R.id.about);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchCount++;
                if(touchCount == ENABLE_DEV_OPTIONS_TOUCH_COUNT) {
                    preferences.enableDevMode();
                    Toast.makeText(thiz, R.string.preferences_about_dev_mode, Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
    }
}
