package com.sirnommington.squid.activity.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.activity.common.SquidServiceProvider;
import com.sirnommington.squid.services.Preferences;
import com.sirnommington.squid.services.squid.contracts.Device;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shows a set of devices in a grid, as well as an add device item.
 */
public class DeviceGridFragment extends ProgressFragment implements AdapterView.OnItemClickListener {
    private static class Arguments {
        public static final String SHOW_THIS_DEVICE = "showThisDevice";
    }

    private OnDeviceClickedListener deviceClickedListener;
    private DevicesAdapter devicesAdapter;

    private boolean showThisDevice;

    /**
     * Keeps track of whether or not this fragment has loaded devices before.
     */
    private boolean hasLoaded = false;

    public static DeviceGridFragment create(boolean showThisDevice) {
        final DeviceGridFragment fragment = new DeviceGridFragment();
        final Bundle bundle = new Bundle();
        bundle.putBoolean(Arguments.SHOW_THIS_DEVICE, showThisDevice);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState) {
        this.showThisDevice = getArguments().getBoolean(Arguments.SHOW_THIS_DEVICE);
        return super.onCreateView(inflater, parentViewGroup, savedInstanceState);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_device_grid, container, false);

        this.deviceClickedListener = (OnDeviceClickedListener)this.getActivity();

        final GridView devices = view.findViewById(R.id.devices);
        this.devicesAdapter = new DevicesAdapter(this.getActivity());
        devices.setAdapter(this.devicesAdapter);
        devices.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getDevices();
    }

    /**
     * Retrieves the user's devices from the server.
     */
    public void getDevices() {
        final SquidService squidService = ((SquidServiceProvider) this.getActivity()).getSquidService();

        new AsyncTask<Void,  Void, AsyncResponse<Collection<Device>>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(!hasLoaded);
            }

            @Override
            protected AsyncResponse<Collection<Device>> doInBackground(Void... params) {
                return squidService.getDevices();
            }

            @Override
            protected void onPostExecute(AsyncResponse<Collection<Device>> response) {
                super.onPostExecute(response);
                showLoading(false);

                if(response.error != null) {
                    showError(getResources().getString(R.string.get_devices_error));
                } else {
                    final Preferences prefs = new Preferences(getActivity());
                    final Device thisDevice = prefs.getThisDevice();

                    // If the device is not set or the server does not contain this device, then reset the app and close
                    if(thisDevice == null || getDevice(response.payload, thisDevice) == null) {
                        prefs.reset();
                        getActivity().finish();
                        return;
                    }

                    final Collection<Device> devices = filterDevices(response.payload, thisDevice, showThisDevice);
                    devicesAdapter.setDevices(devices, thisDevice);
                    hasLoaded = true;
                }
            }
        }.execute();
    }

    private static Device getDevice(Collection<Device> devices, Device device) {
        for(Device current : devices) {
            if(current.id.equals(device.id)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Filters the devices for display.
     * @param devices The devices.
     * @param device The current device.
     * @param showThisDevice Whether or not to include the current device.
     */
    private static Collection<Device> filterDevices(Collection<Device> devices, Device device, boolean showThisDevice) {
        if(!showThisDevice && device != null) {
            devices.remove(device);
        }

        final List<Device> filteredDevices = new ArrayList<>(devices);
        Collections.sort(filteredDevices, new DeviceComparator(device));
        return filteredDevices;
    }

    private static class DeviceComparator implements Comparator<Device> {

        private final Device thisDevice;

        public DeviceComparator(Device thisDevice) {
            this.thisDevice = thisDevice;
        }

        @Override
        public int compare(Device d1, Device d2) {
            if(this.thisDevice != null) {
                if (d1.id.equals(this.thisDevice.id)) {
                    return -1;
                }
                else if(d2.id.equals(thisDevice.id)) {
                    return 1;
                }
            }
            return d1.name.compareToIgnoreCase(d2.name);
        }
    }

    /**
     * Calls the parent Activity listener method when a device is clicked.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int viewType = this.devicesAdapter.getItemViewType(position);
        final Device device = (Device) this.devicesAdapter.getItem(position);
        this.deviceClickedListener.onDeviceClicked(viewType, device);
    }

    /**
     * Shows an error message to the user.
     */
    private void showError(String error) {
        Toast.makeText(this.getActivity(), error, Toast.LENGTH_LONG).show();
    }
}
