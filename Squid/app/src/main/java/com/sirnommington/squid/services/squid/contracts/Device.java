package com.sirnommington.squid.services.squid.contracts;

import com.google.gson.Gson;

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
     * Creates a device from a JSON string.
     */
    public static Device from(String s) {
        Gson gson = new Gson();
        return gson.fromJson(s, Device.class);
    }
}
