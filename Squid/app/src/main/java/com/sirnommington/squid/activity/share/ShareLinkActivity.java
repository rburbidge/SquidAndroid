package com.sirnommington.squid.activity.share;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.ActivityHelper;
import com.sirnommington.squid.activity.common.MenuActivity;
import com.sirnommington.squid.activity.fragment.DevicesAdapter;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

/**
 * Allows the user to send a URL to another device.
 */
public class ShareLinkActivity extends MenuActivity implements OnDeviceClickedListener, SquidServiceProvider {
    private static final String TAG = ShareLinkActivity.class.getSimpleName();

    private GoogleSignIn googleSignIn;
    private SquidService squidService;
    private String url;

    /**
     * Creates an intent to launch this activity when a link is being shared from the browser.
     */
    public static Intent createShareLinkIntent(Context context, String url) {
        return createIntent(context, url, R.string.select_a_device);
    }

    /**
     * Creates a share link activity intent.
     * @param context The app context.
     * @param url The URL to share when the user selects a device.
     * @param titleResourceId The activity title.
     * @return The intent.
     */
    private static Intent createIntent(Context context, String url, int titleResourceId) {
        final Intent intent = new Intent(context, ShareLinkActivity.class);
        intent.putExtra(IntentExtras.URL, url);
        intent.putExtra(IntentExtras.TITLE_RESOURCE_ID, titleResourceId);
        intent.addFlags(ActivityHelper.ACTIVITY_START_CLEAR_HISTORY);
        return intent;
    }

    /**
     * Initializes the activity with its intent parameters.
     */
    private void init() {
        final String url = ActivityHelper.getStringExtra(this, TAG, IntentExtras.URL);
        final int titleResourceId = getIntent().getIntExtra(IntentExtras.TITLE_RESOURCE_ID, R.string.select_a_device);

        this.url = url;
        this.getSupportActionBar().setTitle(titleResourceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.init();

        final Preferences preferences = new Preferences(this);
        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint(), this.googleSignIn);

        this.setContentView(R.layout.activity_share_link);
    }

    /**
     * Handles clicks on the devices grid items.
     * * If device was clicked, sends the URL to the device.
     * * If add device was clicked, opens AddOtherDeviceActivity.
     */
    public void onDeviceClicked(int viewType, DeviceModel device) {
        switch(viewType) {
            case DevicesAdapter.VIEW_TYPE_DEVICE:
                this.sendLink(device);
                break;
            case DevicesAdapter.VIEW_TYPE_ADD_DEVICE:
                this.startActivity(AddOtherDeviceActivity.createIntent(this, true));
                break;
            default:
                // TODO Log this error in telemetry
                Log.e(TAG, "OnItemClickListener cannot handle view type: " + viewType);
        }
    }

    /**
     * Sends the URL to the device specified, and then closes the activity.
     */
    private void sendLink(final DeviceModel device) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    squidService.sendUrl(device.id, url);
                    finish();
                } catch (Exception e) {
                    showError(getResources().getString(R.string.share_link_error, device.name));
                }
                return null;
            }
        }.execute();
    }

    /**
     * Shows an error message to the user.
     */
    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public SquidService getSquidService() {
        return this.squidService;
    }
}
