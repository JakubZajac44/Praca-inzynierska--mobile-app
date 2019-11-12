package jakub.zajac.com.inzv2.repository;

/**
 * Created by Przemysław on 31.12.2017.
 */

public class UserDao {

    public static final String DB_TABLE_USER = "USER";
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";

    public static final String KEY_LOGIN = "LOGIN";
    public static final String LOGIN_OPTIONS = "TEXT NOT NULL";

    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String PASSWORD_OPTIONS = "TEXT NOT NULL";

    public static final String KEY_ADDRESS = "ADDRESS";
    public static final String ADDRESS_OPTIONS = "TEXT NOT NULL";

    public static final String DB_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_USER + "( " +
                    KEY_ID + " " + ID_OPTIONS + " , " +
                    KEY_LOGIN + " " + LOGIN_OPTIONS + " , "+
                    KEY_PASSWORD + " "+ PASSWORD_OPTIONS + " , "+
                    KEY_ADDRESS + " " +ADDRESS_OPTIONS +")";

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + DB_TABLE_USER;





    private String userLogin;
    private String userPassword;
    private String deviceAddress;

    public UserDao()
    {

    }

    public UserDao(String userLogin, String userPassword, String deviceAddress) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.deviceAddress = deviceAddress;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
}
