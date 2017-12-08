package com.sirnommington.squid.activity.share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sirnommington.squid.R;

/**
 * An activity that describes how to add other devices. To add another device, the user needs to install and run the app
 * on the new device. This contains links to both this app in the Play Store, as well as the chrome extension in the
 * Chrome Web Store.
 */
public class AddOtherDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_other_device);
    }
}
