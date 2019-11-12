package jakub.zajac.com.inzv2.personalisation;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakub.zajac.com.inzv2.R;
import jakub.zajac.com.inzv2.bleCore.BleCallback;

/**
 * Created by Jakub on 20.12.2017.
 */

public class BleDeviceListView extends Fragment implements View.OnClickListener, android.widget.AdapterView.OnItemClickListener{

    private Button scannerButton;
    private AuthorizationPresenter authorizationPresenter;
    private ListView deviceListView;
    private SimpleAdapter adapter;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate( R.layout.ble_device_list_fragment, container, false);
        scannerButton = (Button)view.findViewById(R.id.scannerButton);
        scannerButton.setOnClickListener(this);
        deviceListView = (ListView) view.findViewById(R.id.DevicesList);
        deviceListView.setOnItemClickListener(this);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==scannerButton.getId())
        {
            authorizationPresenter.startBleScanner();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        authorizationPresenter.registerUserDeviceAddress(position);
    }

    public void showToast(String message)
    {
        Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT).show();
    }

    public void refreshMenu(HashMap<String,String> deviceList, BleCallback listAdapter, ArrayList<String> deviceName,
                            ArrayList<String> deviceAddress, List<HashMap<String,String>> listItem, List<BluetoothDevice>listDevice)
    {

        for (int i = 0; i < listAdapter.getCount(); i++) {
            BluetoothDevice device = (BluetoothDevice) listAdapter.getItem(i);
            if (device.getName() != null) {
                deviceName.add(device.getName());
                deviceAddress.add(device.getAddress());
                deviceList.put(device.getName(), device.getAddress());
            }
            adapter = new SimpleAdapter(authorizationPresenter.getPersonalisationActivity(), listItem, R.layout.list_item, new String[]{"Device name", "Device address"},
                    new int[]{R.id.RowName, R.id.RowAddress});

            for(BluetoothDevice deviceTemp : listDevice)
            {
                HashMap<String, String> resultsMap = new HashMap<>();
                resultsMap.put("Device name", deviceTemp.getName());
                resultsMap.put("Device address",deviceTemp.getAddress());
                listItem.add(resultsMap);
            }

            deviceListView.setAdapter(adapter);
        }
    }

    public void setProgressBar(int progress) {
        this.progressBar.setProgress(progress);
    }

    public void clearList() {
        this.deviceListView.setAdapter(null);
    }

    public void setAuthorizationPresenter(AuthorizationPresenter authorizationPresenter) {
        this.authorizationPresenter = authorizationPresenter;
    }
}
