package jakub.zajac.com.inzv2.bleCore;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/**
 * Created by Jakub on 23.12.2017.
 */

public class BleDeviceScanner {

    private static final long SCAN_PERIOD = 3000;

    private BleCallback bleCallback;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private Activity mainActivity;

    public BleDeviceScanner(BluetoothAdapter bluetoothAdapter, Activity mainActivity) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.mainActivity=mainActivity;
        handler = new Handler();
        bleCallback = new BleCallback();
        bleCallback.clear();

    }

    public void startScan() {
        this.scanLeDevice(true);
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothAdapter.stopLeScan(scanCallback);

                }
            }, SCAN_PERIOD);
            bluetoothAdapter.startLeScan(scanCallback);
        } else {
            bluetoothAdapter.stopLeScan(scanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback scanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            bleCallback.addDevice(device);
                            bleCallback.notifyDataSetChanged();
                        }
                    });
                }
            };

    public BleCallback getBleCallback() {
        return bleCallback;
    }


}