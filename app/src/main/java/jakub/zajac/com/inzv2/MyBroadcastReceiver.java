package jakub.zajac.com.inzv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import jakub.zajac.com.inzv2.bleCore.BleService;
import jakub.zajac.com.inzv2.userPanel.UserSensorPresenter;
import jakub.zajac.com.inzv2.waterCalibration.WaterCalibrationPresenter;

/**
 * Created by Jakub on 26.12.2017.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private UserSensorPresenter userSensorPresenter;
    private WaterCalibrationPresenter waterCalibrationPresenter;
    public MyBroadcastReceiver(UserSensorPresenter userSensorPresenter, WaterCalibrationPresenter waterCalibrationPresenter)
    {
        this.userSensorPresenter=userSensorPresenter;
        this.waterCalibrationPresenter=waterCalibrationPresenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (BleService.ACTION_GATT_CONNECTED.equals(action))
            {
                userSensorPresenter.connected();
            }
            else if (BleService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                userSensorPresenter.disenabledBleDevice();

            }
            else if (BleService.ACTION_GATT_NOT_CONNECTED.equals(action))
            {
                userSensorPresenter.bleDeviceNotFounded();

            }
            else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                userSensorPresenter.getBleDeviceServices();
                userSensorPresenter.getBleDeviceCharacteristic();

            }
            else if (BleService.ACTION_DATA_AVAILABLE.equals(action))
            {
                if(userSensorPresenter.getUserUserPanelModel().isWaterCalibrationProcess())
                {
                   waterCalibrationPresenter.dataChangedCalibration();
                }
                if(userSensorPresenter.getUserUserPanelModel().isWaterCalibrationed()||userSensorPresenter.getUserUserPanelModel().isAmmoCalibrationed())
                {
                    userSensorPresenter.dataChanged();
                }


            }

    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.READ_DATA_AMMO_SENSOR);
        intentFilter.addAction(BleService.READ_DATA_WATER_SENSOR);
        intentFilter.addAction(BleService.ACTION_GATT_NOT_CONNECTED);
        return intentFilter;
    }

}
