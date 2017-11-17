package com.sirnommington.squid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.prefs.PreferencesActivity;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;
import com.sirnommington.squid.services.Preferences;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {
    private final MainActivity thiz = this;

    private Preferences preferences;
    private GoogleSignIn googleSignIn;
    private SquidService squidService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the app as initialized once we get to this activity
        this.preferences = new Preferences(this);
        this.preferences.setInitialized();

        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());

        this.refreshDevices();
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
     * Refreshes the list of devices.
     */
    private void refreshDevices() {
        new AsyncTask<Void, Void, AsyncResponse<Collection<DeviceModel>>>() {
            @Override
            protected AsyncResponse<Collection<DeviceModel>> doInBackground(Void... params) {
                try {
                    final String idToken = googleSignIn.silentSignIn();
                    return new AsyncResponse(thiz.squidService.getDevices(idToken), null);
                } catch(Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(AsyncResponse<Collection<DeviceModel>> response) {
                super.onPostExecute(response);
                if(response.error != null) {
                    Toast.makeText(thiz, getResources().getString(R.string.get_devices_error), Toast.LENGTH_LONG);
                } else {
                    DevicesFragment devicesFragment = (DevicesFragment) getFragmentManager().findFragmentById(R.id.devices);
                    devicesFragment.setDevices(response.payload);
                }
            }
        }.execute();
    }
}
