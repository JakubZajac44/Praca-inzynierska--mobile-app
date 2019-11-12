package jakub.zajac.com.inzv2.repository;

/**
 * Created by Przemysław on 28.12.2017.
 */

public class DataDao {

    public static final String DB_TABLE_SENSOR = "SENSOR_DATA";
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";

    public static final String KEY_WATER = "WATER_LEVEL";
    public static final String WATER_OPTIONS = "INTEGER DEFAULT -1";

    public static final String KEY_AMMO = "AMMO_LEVEL";
    public static final String AMMO_OPTIONS = "INTEGER DEFAULT -1";

    public static final String KEY_TIME = "TIME_SAMPLE";
    public static final String TIME_OPTIONS = "DATETIME DEFAULT CURRENT_TIMESTAMP";

    public static final String DB_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +DB_TABLE_SENSOR + "( " +
                    KEY_ID + " " + ID_OPTIONS + " , " +
                    KEY_WATER + " " + WATER_OPTIONS + " , "+
                    KEY_AMMO + " "+ AMMO_OPTIONS + " , "+
                    KEY_TIME + " " +TIME_OPTIONS +")";

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + DB_TABLE_SENSOR;




    private int ammoLevel;
    private int waterLevel;

    public DataDao(int ammoLevel, int waterLevel) {
        this.ammoLevel = ammoLevel;
        this.waterLevel = waterLevel;
    }

    public int getAmmoLevel() {
        return ammoLevel;
    }

    public void setAmmoLevel(int ammoLevel) {
        this.ammoLevel = ammoLevel;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }
}
