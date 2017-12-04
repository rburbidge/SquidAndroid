package com.sirnommington.squid.activity.common;

import android.os.AsyncTask;

import com.sirnommington.squid.services.google.GoogleSignIn;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Retrieves the user's devices.
 */
public class GetDevicesTask extends AsyncTask<Void, Void, AsyncResponse<Collection<DeviceModel>>> {
    private final GoogleSignIn googleSignIn;
    private final SquidService squidService;

    public GetDevicesTask(GoogleSignIn googleSignIn, SquidService squidService) {
        this.googleSignIn = googleSignIn;
        this.squidService = squidService;
    }

    @Override
    protected AsyncResponse<Collection<DeviceModel>> doInBackground(Void... params) {
        try {
            final String idToken = googleSignIn.silentSignIn();
            return new AsyncResponse(squidService.getDevices(idToken), null);
        } catch(Exception e) {
            return null;
        }
    }
}
