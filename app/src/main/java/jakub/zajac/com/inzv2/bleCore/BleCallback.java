package jakub.zajac.com.inzv2.bleCore;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Jakub on 03.10.2017.
 */

public class BleCallback extends BaseAdapter implements Parcelable
{
    private ArrayList<BluetoothDevice> mLeDevices;


    public BleCallback()
    {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();

    }

    protected BleCallback(Parcel in) {
        mLeDevices = in.createTypedArrayList(BluetoothDevice.CREATOR);
    }

    public static final Creator<BleCallback> CREATOR = new Creator<BleCallback>() {
        @Override
        public BleCallback createFromParcel(Parcel in) {
            return new BleCallback(in);
        }

        @Override
        public BleCallback[] newArray(int size) {
            return new BleCallback[size];
        }
    };

    public void addDevice(BluetoothDevice device)
    {
      //  Log.i("EE",device.getAddress().toString());
        if(mLeDevices.isEmpty()&&device.getName()!=null&& device.getAddress()!=null)mLeDevices.add(device);
        for(BluetoothDevice deviceTemp : mLeDevices)
        {
            if(device.getName()!=null&& device.getAddress()!=null&&!deviceTemp.getAddress().equals(device.getAddress()))mLeDevices.add(device);
        }
    }


    public void clear()
    {
        mLeDevices.clear();
    }

    @Override
    public int getCount()
    {
       return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mLeDevices);
    }

    public ArrayList<BluetoothDevice> getmLeDevices() {
        return mLeDevices;
    }
}

