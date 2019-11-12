package jakub.zajac.com.inzv2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import jakub.zajac.com.inzv2.personalisation.AuthorizationPresenter;

import static android.content.ContentValues.TAG;

public class PersonalisationActivity extends AppCompatActivity {

    private static final String REGISTRATION_NEEDED_TEXT = "NOT_REGISTERED";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;


    private SharedPreferences myPreferences;
    private boolean registrationNeeded = true;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalisation);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        registrationNeeded   = myPreferences.getBoolean(REGISTRATION_NEEDED_TEXT, true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AuthorizationPresenter authorizationPresenter = new AuthorizationPresenter();
        authorizationPresenter.startWorking(this);
        if(registrationNeeded)
        {
            this.loadFragment(authorizationPresenter.getRegistrationView());

        }
        else  this.loadFragment(authorizationPresenter.getLoginView());

        if (ContextCompat.checkSelfPermission(PersonalisationActivity.this.getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        int permissionCheck = ContextCompat.checkSelfPermission(PersonalisationActivity.this,
                Manifest.permission.WRITE_CALENDAR);
        Log.e(TAG, "Permission Status: " + permissionCheck);

        if (ContextCompat.checkSelfPermission(PersonalisationActivity.this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(PersonalisationActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {


            }
            else
            {
                ActivityCompat.requestPermissions(PersonalisationActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    }


    public void newActivity(String extraString)
    {
        SharedPreferences.Editor myEditor =  myPreferences.edit();
        myEditor.putBoolean(REGISTRATION_NEEDED_TEXT,false);
        myEditor.commit();
        Intent intent = new Intent(PersonalisationActivity.this,UserPanelActivity.class);
        intent.putExtra("address",extraString);
        startActivity(intent);
    }




    public void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();

    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
