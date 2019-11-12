package jakub.zajac.com.inzv2.waterCalibration;

import jakub.zajac.com.inzv2.R;
import jakub.zajac.com.inzv2.UserPanelActivity;
import jakub.zajac.com.inzv2.bleCore.BleService;
import jakub.zajac.com.inzv2.userPanel.UserPanelModel;

/**
 * Created by Jakub on 29.12.2017.
 */

public class WaterCalibrationPresenter {

    private UserPanelActivity userPanelActivity;
    private WaterCalibrationView waterCalibrationView;
    private UserPanelModel userPanelModel;
    private BleService bleService;
    private int waterLevel;
    private boolean isVertical;

    public WaterCalibrationPresenter(UserPanelModel userPanelModel)
    {
        waterCalibrationView = new WaterCalibrationView();
        this.userPanelModel = userPanelModel;
        isVertical=false;
    }

    public void startCalibration()
    {
        waterLevel = userPanelModel.specifyLevel(userPanelModel.convertDataToInt(this.bleService.readCharacteristic(userPanelModel.getListCharacteristic(1))));
        float  positionX = userPanelModel.convertDataToFloat(bleService.readCharacteristic(userPanelModel.getListCharacteristic(2)));
        float positionY =  userPanelModel.convertDataToFloat(bleService.readCharacteristic(userPanelModel.getListCharacteristic(3)));
        if(positionX>60.0 &&positionY>-190.0 &&positionY<-100.0)
        {
            waterCalibrationView.setCalibrationData(true,waterLevel);
            isVertical=true;
        }
        else
        {
            waterCalibrationView.setCalibrationData(false,waterLevel);
            isVertical=false;
        }
        this.bleService.setCharacteristicNotification(userPanelModel.getListCharacteristic(2),true);
        try {
            Thread.sleep(220);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.bleService.setCharacteristicNotification(userPanelModel.getListCharacteristic(3),true);
        try {
            Thread.sleep(220);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.bleService.setCharacteristicNotification(userPanelModel.getListCharacteristic(1),true);
    }

    public void dataChangedCalibration()
    {
        waterLevel = userPanelModel.specifyLevel(userPanelModel.convertDataToInt(userPanelModel.getListCharacteristic(1).getValue()));
        float  positionX = userPanelModel.convertDataToFloat(userPanelModel.getListCharacteristic(2).getValue());
        float positionY = userPanelModel.convertDataToFloat(userPanelModel.getListCharacteristic(3).getValue());
        if(positionX>60.0 &&positionY>-190.0 &&positionY<-100.0)
        {
            isVertical=true;
            waterCalibrationView.setCalibrationData(true,waterLevel);
        }
        else
        {
            isVertical=false;
            waterCalibrationView.setCalibrationData(false,waterLevel);
        }

    }


    public void finishCalibration()
    {
            if(isVertical&&waterLevel>=0)
            {
                waterCalibrationView.showToast(userPanelActivity.getBaseContext().getResources().getString(R.string.calibrationFinish));
                userPanelModel.setWaterLevelDao(waterLevel);
                userPanelModel.setWaterCalibrationProcess(false);
                userPanelActivity.mainFragmentLoad();
                userPanelModel.setWaterCalibrationed(isVertical);
                userPanelModel.setVertical(isVertical);

            }
            else
            {
                waterCalibrationView.showToast(waterCalibrationView.getString(R.string.waterError));
            }

    }


    public void startWorking( UserPanelActivity userPanelActivity)
    {
        this.bleService = userPanelActivity.getBleService();
        this.userPanelActivity = userPanelActivity;
        waterCalibrationView.setWaterCalibrationPresenter(this);
}

    public WaterCalibrationView getWaterCalibrationView() {
        return waterCalibrationView;
    }



}
