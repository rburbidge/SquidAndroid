package com.sirnommington.squid.services.squid.contracts;

import com.google.gson.Gson;
import com.sirnommington.squid.R;

import java.io.Serializable;

/**
 * Device returned by SquidService.
 */
public class Device implements Serializable {
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
        if(this.deviceType.equals(DeviceType.ANDROID)) {
            return R.drawable.ic_phone_android_black_100dp;
        } else if(this.deviceType.equals(DeviceType.CHROME)) {
            return R.drawable.ic_laptop_black_100dp;
        } else {
            return R.drawable.ic_laptop_black_100dp;
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
