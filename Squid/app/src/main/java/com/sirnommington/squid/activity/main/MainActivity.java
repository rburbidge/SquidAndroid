package com.sirnommington.squid.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.common.MenuActivity;
import com.sirnommington.squid.activity.common.AddOtherDeviceActivity;
import com.sirnommington.squid.activity.common.SquidServiceProvider;
import com.sirnommington.squid.activity.device.DeviceActivity;
import com.sirnommington.squid.activity.fragment.DevicesAdapter;
import com.sirnommington.squid.activity.fragment.OnDeviceClickedListener;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.contracts.Device;
import com.sirnommington.squid.services.squid.SquidService;

/**
 * The main activity that the user will see upon opening the app via the homescreen (assuming the app has already been
 * initialized).
 */
public class MainActivity extends MenuActivity implements OnDeviceClickedListener, SquidServiceProvider {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SquidService squidService;

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Preferences preferences = new Preferences(this);
        final GoogleSignIn googleSignIn = new GoogleSignIn(this);
        this.squidService = new SquidService(preferences.getSquidEndpoint(), googleSignIn);

        this.setContentView(R.layout.activity_main);
    }

    @Override
    public void onDeviceClicked(int viewType, Device device) {
        switch(viewType) {
            case DevicesAdapter.VIEW_TYPE_DEVICE:
                this.startActivity(DeviceActivity.createIntent(this, device));
                break;
            case DevicesAdapter.VIEW_TYPE_ADD_DEVICE:
                // TODO Pass correct hasOtherDevices value, or change the name of param and make it false
                this.startActivity(AddOtherDeviceActivity.createIntent(this, true));
                break;
            default:
                    Log.e(TAG, "OnItemClickListener cannot handle view type: " + viewType);
        }
    }

    @Override
    public SquidService getSquidService() {
        return this.squidService;
    }
}
