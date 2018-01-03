package com.sirnommington.squid.services.squid.contracts;

import com.sirnommington.squid.services.squid.SquidService;

/**
 * Response from adding a device.
 * @see SquidService#addDevice(String, String)
 */
public class AddDeviceResult {
    /**
     * The device.
     */
    public final Device device;

    /**
     * True if the device was created, false if it already existed.
     */
    public final boolean deviceCreated;

    public AddDeviceResult(Device device, boolean deviceAdded) {
        this.device = device;
        this.deviceCreated = deviceAdded;
    }
}
