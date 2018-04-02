package com.sirnommington.squid.activity.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sirnommington.squid.R;
import com.sirnommington.squid.services.squid.contracts.Device;
import com.sirnommington.squid.services.squid.contracts.DeviceType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Adapter that includes the user's devices, as well as an add device item.
 */
public class DevicesAdapter extends BaseAdapter {
    /**
     * The following are view types.
     * **When adding a new view type, change COUNT_VIEW_TYPES to refer to <latest view type> + 1**
     */
    public static final int VIEW_TYPE_DEVICE = 0;
    public static final int VIEW_TYPE_ADD_DEVICE = 1;
    public static final int COUNT_VIEW_TYPES = VIEW_TYPE_ADD_DEVICE + 1;

    private final String addDeviceTitle;
    private final LayoutInflater inflater;
    private final ArrayList<Device> devices;
    private Device thisDevice;

    public DevicesAdapter(Context context) {
        this.addDeviceTitle = context.getResources().getString(R.string.add_a_device);
        this.inflater = LayoutInflater.from(context);
        this.devices = new ArrayList<>();
    }

    @Override
    public int getCount() {
        // The number of devices, plus the add device item
        return this.devices.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position < this.devices.size()) {
            return this.devices.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // Only the last type is an add-device item
        if(position < this.devices.size()) {
            return VIEW_TYPE_DEVICE;
        }
        return VIEW_TYPE_ADD_DEVICE;
    }

    @Override
    public int getViewTypeCount() {
        return COUNT_VIEW_TYPES;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int viewType = getItemViewType(position);

        final View itemView = inflater.inflate(R.layout.grid_item_device, parent, false);
        final TextView titleView = itemView.findViewById(R.id.title);
        final ImageView imageView = itemView.findViewById(R.id.image);
        final View thisDeviceIndicator = itemView.findViewById(R.id.this_device);

        final String title;
        final int imageResourceId;
        final boolean showThisDeviceIndicator;
        switch(viewType) {
            case VIEW_TYPE_DEVICE:
                final Device device = this.devices.get(position);

                title = device.name;
                imageResourceId = device.getIconId();
                showThisDeviceIndicator = device.id.equals(this.thisDevice.id);
                break;
            case VIEW_TYPE_ADD_DEVICE:
                title = this.addDeviceTitle;
                imageResourceId = R.drawable.ic_add_circle_black_100dp;
                showThisDeviceIndicator = false;
                break;
            default:
                throw new IllegalArgumentException("Unsupported view type: " + viewType);
        }

        titleView.setText(title);
        imageView.setImageResource(imageResourceId);
        thisDeviceIndicator.setVisibility(showThisDeviceIndicator ? View.VISIBLE : View.INVISIBLE);

        return itemView;
    }

    /**
     * Updates the adapter's set of devices, and notifies all users of the adapter.
     * @param devices The new set of devices.
     * @param thisDevice The current device. Null if this device does not exist.
     */
    public void setDevices(Collection<Device> devices, Device thisDevice) {
        this.devices.clear();
        this.devices.addAll(devices);

        this.thisDevice = thisDevice;

        this.notifyDataSetChanged();
    }
}