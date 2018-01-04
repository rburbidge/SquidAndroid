package com.sirnommington.squid.services.squid.contracts;

import android.util.Log;

import com.google.gson.Gson;
import com.sirnommington.squid.R;

import java.io.Serializable;

/**
 * Device returned by SquidService.
 */
public class Device implements Serializable {
    private static final String TAG = Device.class.getSimpleName();

    private static final int DEFAULT_ICON = R.drawable.ic_laptop_black_100dp;

    /**
     * The device unique ID, defined by SquidService.
     */
    public String id;

    /**
     * The device name displayable in the UI. e.g. "Nexus 5".
     */
    public String name;

    /**
     * The device type.
     * @see DeviceType
     */
    public String deviceType;

    /**
     * Returns the icon resource ID for the device.
     */
    public int getIconId() {
        if(this.deviceType == null) {
            Log.e(TAG, "Null deviceType received: " + deviceType);
            return DEFAULT_ICON;
        } else if(this.deviceType.equals(DeviceType.ANDROID)) {
            return R.drawable.ic_phone_android_black_100dp;
        } else if(this.deviceType.equals(DeviceType.CHROME)) {
            return R.drawable.ic_laptop_black_100dp;
        } else {
            Log.e(TAG, "Unknown deviceType received: " + deviceType);
            return DEFAULT_ICON;
        }
    }

    /**
     * Creates a device from a JSON string.
     */
    public static Device from(String s) {
        Gson gson = new Gson();
        return gson.fromJson(s, Device.class);
    }
}
