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

import java.util.Collection;

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

                    Collection<Device> devices = response.payload;
                    if(!showThisDevice) {
                        devices = filterDevices(response.payload, thisDevice);
                    }
                    devicesAdapter.setDevices(devices);
                    hasLoaded = true;
                }
            }
        }.execute();
    }

    /**
     * Filters a device out of the a collection.
     */
    private Collection<Device> filterDevices(Collection<Device> devices, Device device) {
        if(device == null) return devices;

        for(Device currentDevice : devices) {
            if(device.id.equals(currentDevice.id)) {
                devices.remove(currentDevice);
                break;
            }
        }

        return devices;
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
