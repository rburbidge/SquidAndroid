package com.sirnommington.squid.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.activity.common.GetDevicesTask;
import com.sirnommington.squid.activity.share.OnDeviceClickedListener;
import com.sirnommington.squid.activity.share.SquidServiceProvider;
import com.sirnommington.squid.services.squid.DeviceModel;
import com.sirnommington.squid.services.squid.SquidService;

import java.util.Collection;

/**
 * Shows a set of devices in a grid, as well as an add device item.
 */
public class DeviceGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private OnDeviceClickedListener deviceClickedListener;
    private DevicesAdapter devicesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
     * TODO Cache the user's devices on the device so that they show up faster.
     */
    private void getDevices() {
        final View progress = getView().findViewById(R.id.progress);
        final View devices = getView().findViewById(R.id.devices);

        final SquidService squidService = ((SquidServiceProvider) this.getActivity()).getSquidService();
        new GetDevicesTask(squidService) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                devices.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(AsyncResponse<Collection<DeviceModel>> response) {
                devices.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

                super.onPostExecute(response);
                if(response.error != null) {
                    showError(getResources().getString(R.string.get_devices_error));
                } else {
                    devicesAdapter.setDevices(response.payload);
                }
            }
        }.execute();
    }

    /**
     * Calls the parent Activity listener method when a device is clicked.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int viewType = this.devicesAdapter.getItemViewType(position);
        final DeviceModel device = (DeviceModel) this.devicesAdapter.getItem(position);
        this.deviceClickedListener.onDeviceClicked(viewType, device);
    }

    /**
     * Shows an error message to the user.
     */
    private void showError(String error) {
        Toast.makeText(this.getActivity(), error, Toast.LENGTH_LONG).show();
    }
}
