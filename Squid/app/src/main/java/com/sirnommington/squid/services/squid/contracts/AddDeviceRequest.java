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

    public AddDeviceRequest(String name, String gcmToken) {
        this.name = name;
        this.gcmToken = gcmToken;
    }
}
