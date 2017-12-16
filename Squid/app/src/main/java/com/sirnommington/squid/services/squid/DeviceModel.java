package com.sirnommington.squid.services.squid;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Device returned by SquidService.
 */
public class DeviceModel implements Serializable {
    /**
     * The device unique ID, defined by SquidService.
     */
    public final String id;

    /**
     * The device name displayable in the UI. e.g. "Nexus 5".
     */
    public final String name;

    public DeviceModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DeviceModel from(String s) throws JSONException {
        JSONObject obj = new JSONObject(s);
        return from(obj);
    }

    private static DeviceModel from(JSONObject obj) throws JSONException {
        return new DeviceModel(
                obj.getString("id"),
                obj.getString("name"));
    }
}
