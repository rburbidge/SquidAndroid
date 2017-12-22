package com.sirnommington.squid.activity.device;

/**
 * Listener for remove confirmation dialog.
 */
public interface RemoveConfirmationDialogListener {
    /**
     * Called when the dialog is dismissed.
     * @param remove True if the user chose to remove the device.
     */
    void onRemoveConfirmComplete(boolean remove);
}
