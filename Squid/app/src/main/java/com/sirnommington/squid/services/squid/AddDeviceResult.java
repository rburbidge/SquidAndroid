package com.sirnommington.squid.services.squid;

/**
 * Result codes for SquidService.AddDevice()
 */
public class AddDeviceResult {
    /**
     * The device.
     */
    public final DeviceModel device;

    /**
     * True if the device was created, false if it already existed.
     */
    public final boolean deviceCreated;

    public AddDeviceResult(DeviceModel device, boolean deviceAdded) {
        this.device = device;
        this.deviceCreated = deviceAdded;
    }
}
