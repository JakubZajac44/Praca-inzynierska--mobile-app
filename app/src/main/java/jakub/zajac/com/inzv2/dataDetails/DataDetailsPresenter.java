package jakub.zajac.com.inzv2.dataDetails;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;

import jakub.zajac.com.inzv2.repository.DataDao;
import jakub.zajac.com.inzv2.repository.DbAdapter;

/**
 * Created by Przemysław on 01.01.2018.
 */

public class DataDetailsPresenter {
    private DataDetailsModel dataDetailsModel;
    private AmmoDetailsView ammoDetailsView;
    private WaterDetailsView waterDetailsView;


    public DataDetailsPresenter(DbAdapter dataBase, DataDao dataDao)
    {
        dataDetailsModel = new DataDetailsModel(dataBase, dataDao);
        ammoDetailsView = new AmmoDetailsView();
        waterDetailsView = new WaterDetailsView();
    }


    public BarData prepareDataWaterChart(XAxis xAxis) {
        return dataDetailsModel.createDataBarChartWater(xAxis);
    }


    public BarData prepareDataAmmoChart(XAxis xAxis) {

       return dataDetailsModel.createDataBarChartAmmo(xAxis);
    }

    public void startWorking() {
        ammoDetailsView.setDataDetailsPresenter(this);
        waterDetailsView.setDataDetailsPresenter(this);
    }

    public void clearDb() {
        dataDetailsModel.deleteDataSensor();
    }

    public String countAmmoTime() {

        return dataDetailsModel.getApproximateTime(true);
    }

    public String countWaterTime() {
        return dataDetailsModel.getApproximateTime(false);
    }

    public AmmoDetailsView getAmmoDetailsView() {
        return ammoDetailsView;
    }

    public WaterDetailsView getWaterDetailsView() {
        return waterDetailsView;
    }
}


