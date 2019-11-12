package jakub.zajac.com.inzv2.dataDetails;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.Collections;

import jakub.zajac.com.inzv2.repository.DataDao;
import jakub.zajac.com.inzv2.repository.DbAdapter;

/**
 * Created by Jakub on 01.01.2018.
 */

public class DataDetailsModel {

    private DbAdapter dataBase;
    private ArrayList<BarEntry> barEntryArrayList;
    private ArrayList<String> timeArrayList;
    private  ArrayList<Integer> timeSecondList;
    private  ArrayList<Integer> ammoLevelList;
    private  ArrayList<Integer> waterLevelList;
    private  MyXAxisValueFormatter myXAxisValueFormatter;
    private final int maxBarChartsize = 10;
    private Cursor cr;
    private DataDao dataDao;
    private int sampleNumber;

    public DataDetailsModel(DbAdapter dataBase, DataDao dataDao)
    {
        this.dataBase=dataBase;
        timeArrayList = new ArrayList<>(16);
        barEntryArrayList = new ArrayList<>(16);
        timeSecondList = new ArrayList<>(16);
        ammoLevelList = new ArrayList<>(16);
        waterLevelList = new ArrayList<>(16);
        this.dataDao = dataDao;
        sampleNumber=0;
    }

    private int getCountRowAmmo()
    {
        return dataBase.rowCounterAmmo();
    }

    private int getCountRowWater() {
        return dataBase.rowCounterWater();
    }

    public BarData createDataBarChartAmmo(XAxis xAxis)
    {
        sampleNumber =0;
        barEntryArrayList.clear();
        timeArrayList.clear();
        ammoLevelList.clear();
        int countRaw = this.getCountRowAmmo();
        int counterTime =1;
        int ammoLevel;
        cr = dataBase.getDataSensor();
        cr.moveToLast();
        if(cr==null)return null;
        do{

            ammoLevel = cr.getInt(cr.getColumnIndex(DataDao.KEY_AMMO));
            if(ammoLevel>=0)
            {
                timeArrayList.add((cr.getString(cr.getColumnIndex(DataDao.KEY_TIME))).substring(11)) ;
                ammoLevelList.add(ammoLevel);
                if(countRaw>maxBarChartsize)barEntryArrayList.add(new BarEntry(maxBarChartsize-counterTime,ammoLevel));
                else barEntryArrayList.add(new BarEntry(countRaw-counterTime,ammoLevel));
                counterTime++;
                sampleNumber++;
            }

        } while(cr.moveToPrevious()&& sampleNumber <maxBarChartsize);
        if(sampleNumber<=0)return null;
        Collections.reverse(timeArrayList);
        String[] tempArray = timeArrayList.toArray(new String[timeArrayList.size()]);
        myXAxisValueFormatter = new MyXAxisValueFormatter(tempArray);
        xAxis.setLabelCount(sampleNumber,false);
        xAxis.setValueFormatter(myXAxisValueFormatter);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"Poziom amunicji");
        barDataSet.setValueTextColor(Color.BLACK);
        return  new BarData(barDataSet);

    }

    public BarData createDataBarChartWater(XAxis xAxis) {

        sampleNumber =0;
        barEntryArrayList.clear();
        timeArrayList.clear();
        int countRaw = this.getCountRowWater();
        int counterTime =1;
        int waterLevel;
        cr = dataBase.getDataSensor();
        cr.moveToLast();
        if(cr==null)return null;
        do{
            waterLevel = cr.getInt(cr.getColumnIndex(DataDao.KEY_WATER));
            if(waterLevel >=0)
            {
                Log.i(String.valueOf(waterLevel),String.valueOf(countRaw));
                timeArrayList.add((cr.getString(cr.getColumnIndex(DataDao.KEY_TIME))).substring(11)) ;
                waterLevelList.add(waterLevel);
                if(countRaw>maxBarChartsize)barEntryArrayList.add(new BarEntry(maxBarChartsize-counterTime, waterLevel));
                else barEntryArrayList.add(new BarEntry(countRaw-counterTime, waterLevel));
                counterTime++;
                sampleNumber++;
            }

        } while(cr.moveToPrevious()&& sampleNumber <maxBarChartsize);

        Collections.reverse(timeArrayList);
        String[] tempArray = timeArrayList.toArray(new String[timeArrayList.size()]);
        myXAxisValueFormatter = new MyXAxisValueFormatter(tempArray);
        xAxis.setLabelCount(sampleNumber,false);
        xAxis.setValueFormatter(myXAxisValueFormatter);
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"Poziom wody");
        barDataSet.setValueTextColor(Color.BLACK);
        return  new BarData(barDataSet);
    }



    public void deleteDataSensor() {
        int ammoLevel;
        dataBase.deleteDataSensor();
        if(dataDao.getAmmoLevel()<=0)ammoLevel=0;
        else ammoLevel = dataDao.getAmmoLevel();
        dataBase.insertDataSensor(dataDao.getWaterLevel(),ammoLevel);
    }

    public String getApproximateTime(boolean choice) {
        if(sampleNumber<2)return "Niewystarczajaca ilość  danych";
        timeSecondList.clear();
        int hour,minute, second, totalTime=0, secondStep, timeSecond, disparity=0;
        double[][] regressionData = new double[sampleNumber][2];
        for(String stringElement:timeArrayList)
        {
            hour = Integer.parseInt(stringElement.substring(0,2));
            minute = Integer.parseInt(stringElement.substring(3,5));
            second = Integer.parseInt(stringElement.substring(6,8));
            timeSecond = ((hour*60)+minute)*60+second;
            timeSecondList.add(timeSecond);
        }
        for(int i=0;i<timeSecondList.size()-1;i++)
        {
            disparity=timeSecondList.get(i+1)-timeSecondList.get(i);
            totalTime+=disparity;
        }
        secondStep = totalTime/sampleNumber;
        if(secondStep==0) secondStep=1;
        int interval=0,step=0,lastX=-1,x,firstPeriod=timeSecondList.get(0);
        if(choice)Collections.reverse(ammoLevelList);
        else Collections.reverse(waterLevelList);
        for(int timeElement : timeSecondList)
        {
            do interval++;
            while(secondStep*interval<timeElement);
            x=interval;
            if(x<=lastX)x++;
            regressionData[step][0]=x;
            if(choice)regressionData[step][1]=ammoLevelList.get(step);
            else regressionData[step][1]=waterLevelList.get(step);
            lastX=x;
            step++;
        }
        SimpleRegression regression = new SimpleRegression();
        regression.addData(regressionData);
        if(regression.getSlope()>=0)return "Brak możliwości wyliczenia";
        double time = -(regression.getIntercept()/regression.getSlope())*secondStep;
        hour = ((int)time/3600)%25;
        minute = (int)(time - hour*3600)/60;
        second = (int)(time -hour*3600-minute*60);
        return String.valueOf(hour)+":"+String.valueOf(minute)+":"+String.valueOf(second);
    }



    public class MyXAxisValueFormatter implements IAxisValueFormatter
    {
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }

}
