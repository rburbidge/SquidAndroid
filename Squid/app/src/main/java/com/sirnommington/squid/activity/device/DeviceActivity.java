package com.sirnommington.squid.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.IntentExtras;
import com.sirnommington.squid.services.squid.DeviceModel;

public class DeviceActivity extends AppCompatActivity {

    public static Intent createIntent(Context context, DeviceModel device) {
        final Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra(IntentExtras.DEVICE, device);
        return intent;
    }

    private void init(DeviceModel device) {
        final TextView title = this.findViewById(R.id.title);
        title.setText(device.name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        final DeviceModel device = (DeviceModel) getIntent().getSerializableExtra(IntentExtras.DEVICE);
        this.init(device);
    }
}
