package jakub.zajac.com.inzv2.userPanel;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import jakub.zajac.com.inzv2.R;
import jakub.zajac.com.inzv2.repository.DataDao;
import jakub.zajac.com.inzv2.repository.DbAdapter;


/**
 * Created by Jakub on 23.12.2017.
 */

public class UserPanelModel {

    private Context context;
    private DbAdapter dateBase;
    private DataDao dataDao;
    private  List<BluetoothGattService> services;
    private List<BluetoothGattCharacteristic> listCharacteristics;

    private boolean waterCalibrationProcess;
    private boolean ammoCalibrationed;
    private boolean waterCalibrationed;
    private boolean isDetailsFragment;
    private boolean isVertical;


    public UserPanelModel()
    {

    }

    public UserPanelModel(Context context){
        dataDao = new DataDao(-1,-1);
        this.context=context;
        dateBase = new DbAdapter(context);
        services = new ArrayList<>(8);

        waterCalibrationProcess = false;
        ammoCalibrationed=false;
        waterCalibrationed=false;
        isDetailsFragment=false;
        isVertical = false;
        dateBase.openDb();
    }


    public int convertDataToInt(byte[] data)
    {
        if(data == null)return -1;
        int a = (data[0] & 0xFF)
                |  ((data[1] & 0xFF) << 8)
                |  ((data[2] & 0xFF) << 16)
                |  ((data[3] & 0xFF) << 24);
        return  (int)Float.intBitsToFloat(a);
    }

    public float convertDataToFloat(byte[] data)
    {
        if(data == null)return -1;
        int a = (data[0] & 0xFF)
                |  ((data[1] & 0xFF) << 8)
                |  ((data[2] & 0xFF) << 16)
                |  ((data[3] & 0xFF) << 24);
        return  Float.intBitsToFloat(a);
    }





    private boolean saveDataSensor()
    {

        if(dateBase.insertDataSensor(dataDao.getWaterLevel(),dataDao.getAmmoLevel())!=-1)return true;
        else return false;

    }

    public void closeModel()
    {
        services.clear();
        listCharacteristics.clear();
         dateBase.closeDb();
    }

    public boolean generateReport() {
       Document doc = new Document();
        String outPath = Environment.getExternalStorageDirectory()+"/report.pdf";
        try {
            PdfWriter.getInstance(doc,new FileOutputStream(outPath));
            Time now = new Time();
            now.setToNow();
            doc.open();
            doc.add(new Paragraph(context.getString(R.string.report1)+" "+now.monthDay+"."+now.month+1+"."+now.year+ " "+
                    now.format("%k:%M:%S")+"\n"+context.getString(R.string.report2)+" " +String.valueOf(dataDao.getWaterLevel())+"\n"+
                    context.getString(R.string.report3)+" "+String.valueOf(dataDao.getAmmoLevel())));
            Log.i("REPORT",context.getString(R.string.report1)+" "+now.monthDay+"."+now.month+1+"."+now.year+ " "+
                    now.format("%k:%M:%S")+"\n"+context.getString(R.string.report2)+" " +String.valueOf(dataDao.getWaterLevel())+"\n"+
                    context.getString(R.string.report3)+" "+String.valueOf(dataDao.getAmmoLevel()));
            doc.close();
            return true;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BluetoothGattService> getServices() {
        return services;
    }

    public List<BluetoothGattCharacteristic> getListCharacteristics() {
        return listCharacteristics;
    }

    public BluetoothGattService getService(int position) {
        if(position<services.size())return services.get(position);
        else return null;
    }

    public void setServices(List<BluetoothGattService> services) {
        this.services = services;
    }

    public void setListCharacteristics(List<BluetoothGattCharacteristic> listCharacteristics) {
        this.listCharacteristics = listCharacteristics;
    }

    public BluetoothGattCharacteristic getListCharacteristic(int position) {
        return listCharacteristics.get(position);
    }

    public boolean setWaterLevelDao(int waterLevel)
    {
        dataDao.setWaterLevel(waterLevel);
        return this.saveDataSensor();
    }

    public boolean setAmmoLevelDao(int ammoLevel)
    {
        dataDao.setAmmoLevel(ammoLevel);
        return this.saveDataSensor();
    }



    public int getAmmoLevelDao()
    {
        return dataDao.getAmmoLevel();
    }

    public int getWaterLevelDao()
    {
        return dataDao.getWaterLevel();
    }

    public boolean isWaterCalibrationProcess() {
        return waterCalibrationProcess;
    }

    public void setWaterCalibrationProcess(boolean waterCalibrationProcess) {
        this.waterCalibrationProcess = waterCalibrationProcess;
    }

    public boolean isAmmoCalibrationed() {
        return ammoCalibrationed;
    }

    public void setAmmoCalibrationed(boolean ammoCalibrationed) {
        this.ammoCalibrationed = ammoCalibrationed;
    }

    public boolean isWaterCalibrationed() {
        return waterCalibrationed;
    }

    public void setWaterCalibrationed(boolean waterCalibrationed) {
        this.waterCalibrationed = waterCalibrationed;
    }

    public DbAdapter getDateBase() {
        return dateBase;
    }

    public DataDao getDataDao() {
        return dataDao;
    }

    public boolean isDetailsFragment() {
        return isDetailsFragment;
    }

    public void setDetailsFragment(boolean detailsFragment) {
        isDetailsFragment = detailsFragment;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public int specifyLevel(int waterLevel) {

            if(waterLevel==0)return 0;
            else if(waterLevel==1)return 10;
            else if(waterLevel==11)return 25;
            else if(waterLevel==111)return 50;
            else if(waterLevel==1111)return 75;
            else if(waterLevel==11111)return 100;
           return -1;
    }

    public int specifyAmmoLevel(float ammoLevel, int source) {

       if(source==0)
       {
           if(Math.floor(ammoLevel)>=2.0&&Math.floor(ammoLevel)<=32.0)return (int)Math.floor(ammoLevel)-2;
            else return -1;

       }
       else
       {
           if(Math.floor(ammoLevel)>=11.0&&Math.floor(ammoLevel)<=330.0)return (int)Math.floor(ammoLevel)/11;
           return -1;
       }

    }
}
