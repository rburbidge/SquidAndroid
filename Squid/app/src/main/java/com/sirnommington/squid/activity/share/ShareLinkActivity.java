package com.sirnommington.squid.activity.share;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.activity.common.GetDevicesTask;
import com.sirnommington.squid.activity.common.ActivityHelper;
import com.sirnommington.squid.activity.prefs.PreferencesActivity;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Allows the user to send a URL to another device.
 */
public class ShareLinkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = ShareLinkActivity.class.getSimpleName();

    private GoogleSignIn googleSignIn;
    private SquidService squidService;
    private DevicesAdapter devicesAdapter;
    private String url;

    /**
     * Creates an intent to launch this activity as the app's main activity.
     * TODO This is currently a stop-gap until we determine what to show on the main activity.
     */
    public static Intent createMainIntent(Context context) {
        return createIntent(context, "https://www.google.com", R.string.app_name);
    }

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
     * Initializes the activity with its parameters.
     */
    private void init(String url, int titleResourceId) {
        this.url = url;
        this.getSupportActionBar().setTitle(titleResourceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url = ActivityHelper.getStringExtra(this, TAG, IntentExtras.URL);
        final int titleResourceId = getIntent().getIntExtra(IntentExtras.TITLE_RESOURCE_ID, R.string.select_a_device);
        this.init(url, titleResourceId);

        final Preferences preferences = new Preferences(this);
        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());
        this.devicesAdapter = new DevicesAdapter(this);

        this.setContentView(R.layout.activity_share_link);

        final GridView devices = (GridView) this.findViewById(R.id.devices);
        devices.setAdapter(this.devicesAdapter);
        devices.setOnItemClickListener(this);

        this.getDevices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dev_options:
                this.startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles clicks on the devices grid items.
     * * If device was clicked, sends the URL to the device.
     * * If add device was clicked, opens AddOtherDeviceActivity.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int viewType = this.devicesAdapter.getItemViewType(position);
        switch(viewType) {
            case DevicesAdapter.VIEW_TYPE_DEVICE:
                final DeviceModel device = (DeviceModel) this.devicesAdapter.getItem(position);
                this.sendLink(device);
                break;
            case DevicesAdapter.VIEW_TYPE_ADD_DEVICE:
                final Intent addOtherDevice = new Intent(getApplicationContext(), AddOtherDeviceActivity.class);
                this.startActivity(addOtherDevice);
                break;
            default:
                // TODO Log this error in telemetry
                Log.e(TAG, "OnItemClickListener cannot handle view type: " + viewType);
        }
    }

    /**
     * Retrieves the user's devices from the server.
     * TODO Cache the user's devices on the device so that they show up faster.
     */
    private void getDevices() {
        final View progress = this.findViewById(R.id.progress);
        new GetDevicesTask(this.googleSignIn, this.squidService) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(AsyncResponse<Collection<DeviceModel>> response) {
                progress.setVisibility(View.GONE);

                super.onPostExecute(response);
                if(response.error != null) {
                    showError(getResources().getString(R.string.get_devices_error));
                } else {
                    devicesAdapter.setDevices(response.payload);
                }
            }
        }.execute();
    }

    /**
     * Sends the URL to the device specified, and then closes the activity.
     */
    private void sendLink(final DeviceModel device) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String idToken = googleSignIn.silentSignIn();
                try {
                    squidService.sendUrl(idToken, device.id, url);
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
}
