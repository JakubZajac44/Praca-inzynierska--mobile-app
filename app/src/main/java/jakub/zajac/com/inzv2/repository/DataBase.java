package jakub.zajac.com.inzv2.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jakub on 20.12.2017.
 */

public class DataBase extends SQLiteOpenHelper {



    public DataBase(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDao.DB_CREATE_TABLE);
        db.execSQL(DataDao.DB_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDao.DROP_TABLE);
        db.execSQL(DataDao.DROP_TABLE);
        onCreate(db);
    }

}
