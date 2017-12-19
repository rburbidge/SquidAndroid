package com.sirnommington.squid.activity.device;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;

/**
 * Confirmation dialog for removing a device.
 */
public class RemoveConfirmationDialog extends DialogFragment {

    public static final String TAG = RemoveConfirmationDialog.class.getSimpleName();

    private String deviceName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceName = this.getArguments().getString(IntentExtras.DEVICE_NAME);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RemoveConfirmationDialogListener listener = (RemoveConfirmationDialogListener) this.getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setMessage(getResources().getString(R.string.device_remove_device_confirm, this.deviceName))
            .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onRemoveConfirmComplete(true);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onRemoveConfirmComplete(false);
                }
            });
        return builder.create();
    }
}
