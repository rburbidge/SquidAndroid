package com.sirnommington.squid.activity;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.squid.DeviceModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Displays a list of devices.
 */
public class DevicesFragment extends ListFragment {

    private DevicesAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.adapter = new DevicesAdapter(getActivity());
        this.setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ListView listView = this.getListView();
        // Must use inflater from activity, not from this.getLayoutInflater()
        final LayoutInflater inflater = this.getActivity().getLayoutInflater();
        listView.addHeaderView(inflater.inflate(R.layout.list_header_select_device, listView, false));
    }

    /**
     * Sets the devices displayed. Clears any existing devices.
     * @param devices The devices.
     */
    public void setDevices(Collection<DeviceModel> devices) {
        this.adapter.clear();
        this.adapter.addAll(devices);
    }

    /**
     * Backs the list fragment.
     *
     * @see ArrayAdapter#clear() to clear devices.
     * @see ArrayAdapter#addAll(Collection) to set devices.
     */
    private static class DevicesAdapter extends ArrayAdapter<DeviceModel> {
        private static final int layoutName = R.layout.list_item_device;

        public DevicesAdapter(Context context) {
            super(context, DevicesAdapter.layoutName, 0, new ArrayList<DeviceModel>());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View deviceView = inflater.inflate(DevicesAdapter.layoutName, parent, false);
            final TextView deviceName = deviceView.findViewById(R.id.device_name);
            deviceName.setText(this.getItem(position).name);
            return deviceView;
        }
    }
}
