package com.sirnommington.squid.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.squid.DeviceModel;

import java.util.ArrayList;
import java.util.Collection;

public class DevicesAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final ArrayList<DeviceModel> devices;

    public DevicesAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.devices = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.devices.size();
    }

    @Override
    public Object getItem(int position) {
        return this.devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View deviceView = inflater.inflate(R.layout.grid_item_device, parent, false);
        final TextView deviceName = deviceView.findViewById(R.id.device_name);
        deviceName.setText(this.devices.get(position).name);
        return deviceView;
    }

    public void setDevices(Collection<DeviceModel> payload) {
        this.devices.clear();
        this.devices.addAll(payload);
        this.notifyDataSetChanged();
    }
}
