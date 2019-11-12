package jakub.zajac.com.inzv2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jakub on 20.12.2017.
 */

public class DbAdapter {

    public static final String DB_NAME = "App.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;
    private Context context;
    private DataBase myDataBase;

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter openDb(){
        myDataBase = new DataBase(context, DB_NAME, null, DB_VERSION);
        try {
            db = myDataBase.getWritableDatabase();
        } catch (SQLException e) {
            db = myDataBase.getReadableDatabase();
        }
        return this;
    }

    public void closeDb() {
        myDataBase.close();
    }


    public long insertDataSensor(int waterLevel,int ammoLevel)
    {
        ContentValues newTodoValues = new ContentValues();
        newTodoValues.put(DataDao.KEY_WATER,waterLevel);
        newTodoValues.put(DataDao.KEY_AMMO,ammoLevel);
        return db.insert(DataDao.DB_TABLE_SENSOR, null, newTodoValues);
    }


    public long insertDataUser(String login, String password, String addressDevice)
    {
        ContentValues newTodoValues = new ContentValues();
        newTodoValues.put(UserDao.KEY_LOGIN,login);
        newTodoValues.put(UserDao.KEY_PASSWORD ,password);
        newTodoValues.put(UserDao.KEY_ADDRESS,addressDevice);
        return db.insert(UserDao.DB_TABLE_USER, null, newTodoValues);
    }

    public Cursor getUser()
    {
        String selectQuery = "SELECT * FROM "+ UserDao.DB_TABLE_USER;
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor getDataSensor()
    {
        String selectQuery = "SELECT * FROM "+ DataDao.DB_TABLE_SENSOR;
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public int rowCounterAmmo()
    {
        String selectQuery = "SELECT COUNT(*) FROM "+ DataDao.DB_TABLE_SENSOR+" WHERE "+DataDao.KEY_AMMO+" >=0";
        Cursor cursor = db.rawQuery(selectQuery,null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void deleteDataSensor()
    {

        String deleteQuery = "DELETE FROM " + DataDao.DB_TABLE_SENSOR;
        db.execSQL(deleteQuery);

    }


    public int rowCounterWater() {

        String selectQuery = "SELECT COUNT(*) FROM "+ DataDao.DB_TABLE_SENSOR+" WHERE "+DataDao.KEY_WATER+" >=0";
        Cursor cursor = db.rawQuery(selectQuery,null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
