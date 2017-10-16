package com.sirnommington.squid.services.squid;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Device returned by SquidService.
 */
public class DeviceModel {
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

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static DeviceModel from(String s) throws JSONException {
        JSONObject obj = new JSONObject(s);
        return from(obj);
    }

    public static DeviceModel from(JSONObject obj) throws JSONException {
        return new DeviceModel(
                obj.getString("id"),
                obj.getString("name"));
    }
}
