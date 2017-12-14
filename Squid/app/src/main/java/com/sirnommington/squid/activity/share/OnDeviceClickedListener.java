package com.sirnommington.squid.activity.share;

import com.sirnommington.squid.services.squid.DeviceModel;

/**
 * Listener for when a device is clicked.
 */
public interface OnDeviceClickedListener {
    /**
     * Called when a device is clicked.
     * @param viewType One of the DevicesAdapter.VIEW_TYPE* constants.
     * @param device The device. This may be null if the view type does not have a device. E.g. VIEW_TYPE_ADD_DEVICE
     */
    void onDeviceClicked(int viewType, DeviceModel device);
}