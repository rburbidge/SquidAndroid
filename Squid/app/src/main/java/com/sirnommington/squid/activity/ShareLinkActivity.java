package com.sirnommington.squid.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Handles the share link command from Chrome browser.
 */
public class ShareLinkActivity extends AppCompatActivity {
    private GoogleSignIn googleSignIn;
    private SquidService squidService;
    private DevicesAdapter devicesAdapter;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.url = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);

        final Preferences preferences = new Preferences(this);
        this.googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint());
        this.devicesAdapter = new DevicesAdapter(this);

        setContentView(R.layout.activity_share_link);

        final GridView devices = (GridView) this.findViewById(R.id.devices);
        devices.setAdapter(this.devicesAdapter);
        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int viewType = devicesAdapter.getItemViewType(position);
                switch(viewType) {
                    case DevicesAdapter.VIEW_TYPE_DEVICE:
                        final DeviceModel device = (DeviceModel) devicesAdapter.getItem(position);
                        sendLink(device);
                        break;
                    case DevicesAdapter.VIEW_TYPE_ADD_DEVICE:
                        // Show AddOtherDevice activity
                        break;
                    default:

                }

            }
        });

        this.getDevices();
    }

    private void getDevices() {
        final ShareLinkActivity thiz = this;

        new GetDevicesTask(this.googleSignIn, this.squidService) {
            @Override
            protected void onPostExecute(AsyncResponse<Collection<DeviceModel>> response) {
                super.onPostExecute(response);
                if(response.error != null) {
                    Toast.makeText(thiz, getResources().getString(R.string.get_devices_error), Toast.LENGTH_LONG);
                } else {
                    thiz.devicesAdapter.setDevices(response.payload);
                }
            }
        }.execute();
    }

    private void sendLink(final DeviceModel device) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String accessToken = googleSignIn.silentSignIn();
                try {
                    squidService.sendUrl(accessToken, device.id, url);

                } catch (Exception e) {
                    // TODO Show error
                }
                return null;
            }
        }.execute();
    }
}
