package jakub.zajac.com.inzv2.userPanel;



import android.util.Log;

import jakub.zajac.com.inzv2.R;
import jakub.zajac.com.inzv2.UserPanelActivity;
import jakub.zajac.com.inzv2.bleCore.BleService;

/**
 * Created by Jakub on 23.12.2017.
 */

public class UserSensorPresenter {

    private UserPanelActivity userPanelActivity;

    private UserPanelView userUserPanelView;
    private UserPanelModel userUserPanelModel;
    private BleService bleService;

    private int lastAmmoLevel;
    private int lastWaterLevel;
    private int ammoLevel;
    private  int waterLevel;
    private int ammoSensorSource;
    private int waterSensorSource;
    private int positionXSource;
    private int positionYSource;

    public UserSensorPresenter()
    {
        userUserPanelView = new UserPanelView();
        userUserPanelModel = new UserPanelModel();
        lastAmmoLevel=lastWaterLevel=-10;
        ammoSensorSource=0;
        waterSensorSource=1;
        positionXSource=2;
        positionYSource=3;
    }

    public void connected() {
        userPanelActivity.connected();

    }

    public void disenabledBleDevice()
    {
        userUserPanelModel.closeModel();
        userPanelActivity.disenableBleDevice();

    }

    public void bleDeviceNotFounded()
    {
        userPanelActivity.disenableBleDevice();

    }


    public void startWorking(UserPanelActivity userPanelActivity)
    {
        this.userPanelActivity = userPanelActivity;
        userUserPanelModel = new UserPanelModel(userPanelActivity.getApplicationContext());
        userUserPanelView.setUserSensorPresenter(this);
    }

    public void destroy() {
        userUserPanelModel.closeModel();
    }

    public void getBleDeviceServices()
    {
       userUserPanelModel.setServices( bleService.getSupportedGattServices());
    }
    public void getBleDeviceCharacteristic()
    {
        for(int i = 0; i< userUserPanelModel.getServices().size(); i++)
        {
            if((userUserPanelModel.getService(i).getUuid().toString()).equals("19b10000-e8f2-537e-4f6c-d104768a1214"));
            {
                userUserPanelModel.setListCharacteristics(userUserPanelModel.getService(i).getCharacteristics());
            }
        }
        for(int i = 0; i< userUserPanelModel.getListCharacteristics().size(); i++) Log.i("CHARACTERISTIC", userUserPanelModel.getListCharacteristic(i).getUuid().toString());
    }

    public void setAmmoNotification(boolean result)
    {
        if(result)
        {
            getUserUserPanelModel().setAmmoCalibrationed(true);
            if(userUserPanelModel.setAmmoLevelDao(ammoLevel))Log.i("UserSensorPresenter","Insert new data");
            bleService.setCharacteristicNotification(getUserUserPanelModel().getListCharacteristic(0),true);
        }
        else
        {
            getUserUserPanelModel().setAmmoCalibrationed(false);
            bleService.setCharacteristicNotification(getUserUserPanelModel().getListCharacteristic(0),false);
        }

    }

    public void setAmmoSource(boolean choice)
    {
        if(choice)ammoSensorSource=0;
        else ammoSensorSource=4;
        ammoLevel = userUserPanelModel.specifyAmmoLevel(getUserUserPanelModel().convertDataToFloat(bleService.readCharacteristic(getUserUserPanelModel().getListCharacteristic(ammoSensorSource))),ammoSensorSource);
        userUserPanelView.ammoCalibration(ammoLevel);

    }

    public void ammoCalibration()
    {
        userUserPanelView.setAmmoCalibrationSource();
    }


    public void dataChanged() {


        if(getUserUserPanelModel().isAmmoCalibrationed())
        {
            ammoLevel = userUserPanelModel.specifyAmmoLevel(userUserPanelModel.convertDataToFloat(userUserPanelModel.getListCharacteristic(ammoSensorSource).getValue()),ammoSensorSource);
            if(lastAmmoLevel!=ammoLevel)
            {
                if(!userUserPanelModel.isDetailsFragment())userUserPanelView.setAmmoLevelText(ammoLevel,userPanelActivity.isCanRePaint());
                if(userUserPanelModel.setAmmoLevelDao(ammoLevel))Log.i("DB","New data sensor save");
                lastAmmoLevel=ammoLevel;
                if(ammoLevel<3) userUserPanelView.showAlert(ammoLevel,true);
            }


        }
        if(getUserUserPanelModel().isWaterCalibrationed())
        {

            float  positionX = userUserPanelModel.convertDataToFloat(userUserPanelModel.getListCharacteristic(positionXSource).getValue());
            float positionY = userUserPanelModel.convertDataToFloat(userUserPanelModel.getListCharacteristic(positionYSource).getValue());
            if(positionX>60.0 &&positionY>-190.0 &&positionY<-100.0)
            {
                if(!userUserPanelModel.isDetailsFragment()) userUserPanelView.setPositionImage();
                userUserPanelModel.setVertical(true);

                waterLevel = userUserPanelModel.specifyLevel(userUserPanelModel.convertDataToInt(userUserPanelModel.getListCharacteristic(waterSensorSource).getValue()));
                if (waterLevel != lastWaterLevel) {
                    userUserPanelModel.setWaterLevelDao(waterLevel);
                    lastWaterLevel = waterLevel;

                    if(!userUserPanelModel.isDetailsFragment())
                    {
                        userUserPanelView.setWaterLevelText(waterLevel);

                        if (waterLevel <= 10) userUserPanelView.showAlert(waterLevel, false);
                    }


                }
            }
            else
            {
                userUserPanelModel.setVertical(false);
                if(!userUserPanelModel.isDetailsFragment())
                {
                    userUserPanelView.setPositionImage();
                }
            }

        }

    }

    public void showAmmoDetails() {
        userPanelActivity.setCanRePaint(false);
            userPanelActivity.showDetailsFragment(true);
    }

    public void showWaterDetails() {
            userPanelActivity.showDetailsFragment(false);
    }

    public void generateReport()
    {
        if(userUserPanelModel.generateReport()) userUserPanelView.showToast(userPanelActivity.getBaseContext().getString(R.string.createReportCorrect));
        else userUserPanelView.showToast(userPanelActivity.getBaseContext().getString(R.string.createReportError));
    }

    public int setAmmoLevelText()
    {
        int result = userUserPanelModel.getAmmoLevelDao();
        if(result>=0&&userUserPanelModel.isAmmoCalibrationed())
        {
            return result;
        }
        else return -1;

    }

    public int setWaterLevelText()
    {
        int result = userUserPanelModel.getWaterLevelDao();
        if(result>=0)
        {
            return result;
        }
        else return -1;
    }


    public void prepareView()
    {
        userUserPanelView.prepareView();
    }



    public UserPanelView getUserUserPanelView() {
        return userUserPanelView;
    }
    public void setBleService(BleService bleService) {
        this.bleService = bleService;
    }
    public UserPanelModel getUserUserPanelModel() {
        return userUserPanelModel;
    }
    public UserPanelActivity getUserUserPanelActivity() {
        return userPanelActivity;
    }



}
