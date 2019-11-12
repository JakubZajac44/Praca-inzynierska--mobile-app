package jakub.zajac.com.inzv2.personalisation;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakub.zajac.com.inzv2.repository.DbAdapter;

import jakub.zajac.com.inzv2.repository.UserDao;

/**
 * Created by Jakub on 20.12.2017.
 */

public class AuthorizationModel {

    private DbAdapter dateBase;
    private UserDao userDao;

    private HashMap<String, String> deviceList;
    private ArrayList<String> deviceName;
    private ArrayList<String> deviceAddress;
    private List<HashMap<String,String>> itemList;



    public AuthorizationModel(Context context)
    {
        dateBase = new DbAdapter(context);
        deviceList = new HashMap<>();
        deviceName = new ArrayList<>();
        deviceAddress = new ArrayList<>();
        itemList = new ArrayList<>();
        userDao = new UserDao();

    }

    public UserDao getRegisteredUser()
    {
        dateBase.openDb();
        Cursor cr = dateBase.getUser();
        cr.moveToLast();
        userDao.setUserLogin(cr.getString(cr.getColumnIndex(UserDao.KEY_LOGIN)));
        userDao.setUserPassword(cr.getString(cr.getColumnIndex(UserDao.KEY_PASSWORD)));
        userDao.setDeviceAddress(cr.getString(cr.getColumnIndex(UserDao.KEY_ADDRESS)));
        dateBase.closeDb();
        return   userDao;
    }

    public boolean saveNewUser()
    {
        dateBase.openDb();
        if(dateBase.insertDataUser(userDao.getUserLogin(), userDao.getUserPassword(), userDao.getDeviceAddress())!=-1)
        {
            dateBase.closeDb();
            return true;
        }
        else
        {
           dateBase.closeDb();
            return false;
        }
    }

    public HashMap<String, String> getDeviceList() {
        return deviceList;
    }

    public ArrayList<String> getDeviceName() {
        return deviceName;
    }

    public ArrayList<String> getDeviceAddress() {
        return deviceAddress;
    }

    public List<HashMap<String, String>> getItemList() {
        return itemList;
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
