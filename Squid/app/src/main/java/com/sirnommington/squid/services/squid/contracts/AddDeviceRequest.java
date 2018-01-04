package com.sirnommington.squid.services.squid.contracts;

/**
 * Request to add a new device.
 */
public class AddDeviceRequest {
    /**
     * The device name.
     */
    public final String name;

    /**
     * The device GCM token.
     */
    public final String gcmToken;

    public final String type;

    public AddDeviceRequest(String name, String gcmToken, String type) {
        this.name = name;
        this.gcmToken = gcmToken;
        this.type = type;
    }
}
