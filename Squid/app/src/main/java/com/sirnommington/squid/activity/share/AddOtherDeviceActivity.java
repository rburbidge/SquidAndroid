package com.sirnommington.squid.activity.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sirnommington.squid.R;

/**
 * An activity that describes how to add other devices. To add another device, the user needs to install and run the app
 * on the new device. This contains links to both this app in the Play Store, as well as the chrome extension in the
 * Chrome Web Store.
 *
 */
public class AddOtherDeviceActivity extends AppCompatActivity {

    private static final String EXTRA_HAS_OTHER_DEVICES = "hasOtherDevices";

    /**
     * Creates an intent for the activity
     * @param hasOtherDevices True if the user has other devices registered. This controls the messaging that the user
     * will see.
     */
    public static Intent createIntent(Context context, boolean hasOtherDevices) {
        final Intent intent = new Intent(context, AddOtherDeviceActivity.class);
        intent.putExtra(EXTRA_HAS_OTHER_DEVICES, hasOtherDevices);
        return intent;
    }

    /**
     * Initializes the activity with its intent parameters.
     */
    private void init() {
        final TextView title = (TextView) this.findViewById(R.id.title);
        final boolean hasOtherDevices = this.getIntent().getBooleanExtra(EXTRA_HAS_OTHER_DEVICES, true);
        title.setText(hasOtherDevices
                ? R.string.add_other_device_title_has_devices
                : R.string.add_other_device_title_no_devices);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_other_device);

        this.init();

        this.findViewById(R.id.install_android).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installAndroidApp();
            }
        });
        this.findViewById(R.id.install_chrome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installChromeExtension();
            }
        });
    }

    /**
     * Opens the Google Play store.
     */
    private void installAndroidApp() {
        final String appPackageName = this.getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // This exception will be thrown if the Play Store is not installed
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /**
     * Opens the Chrome Web Store.
     */
    private void installChromeExtension() {
        final Intent openChromeWebStore = new Intent(
                Intent.ACTION_VIEW, Uri.parse(this.getResources().getString(R.string.chrome_ext_url)));
        this.startActivity(openChromeWebStore);
    }
}
