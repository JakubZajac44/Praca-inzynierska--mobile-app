package jakub.zajac.com.inzv2;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import jakub.zajac.com.inzv2.bleCore.BleService;
import jakub.zajac.com.inzv2.dataDetails.DataDetailsPresenter;
import jakub.zajac.com.inzv2.userPanel.UserSensorPresenter;
import jakub.zajac.com.inzv2.waterCalibration.WaterCalibrationPresenter;

public class UserPanelActivity extends AppCompatActivity {

    private static final String TAG = "Warning";
    private BleService bleService;
    private String deviceAddress;
    private UserSensorPresenter userSensorPresenter;
    private WaterCalibrationPresenter waterCalibrationPresenter;
    private DataDetailsPresenter dataDetailsPresenter;
    private int pageNumber;
    private ProgressDialog scanDialog;
    private Toast toast;
    private boolean canRePaint;



    private MyBroadcastReceiver bleDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sensor);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        userSensorPresenter = new UserSensorPresenter();
        loadFragment(userSensorPresenter.getUserUserPanelView());
        userSensorPresenter.startWorking(this);

        waterCalibrationPresenter = new WaterCalibrationPresenter(userSensorPresenter.getUserUserPanelModel());
        dataDetailsPresenter = new DataDetailsPresenter(userSensorPresenter.getUserUserPanelModel().getDateBase(), userSensorPresenter.getUserUserPanelModel().getDataDao());
        dataDetailsPresenter.startWorking();
        Intent intent = getIntent();
        deviceAddress = intent.getExtras().getString("address");
        pageNumber = 0;
        canRePaint=true;

        Intent mServiceIntent = new Intent(this, BleService.class);
        this.startService(mServiceIntent);
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        bleDataReceiver = new MyBroadcastReceiver(userSensorPresenter, waterCalibrationPresenter);
        registerReceiver(bleDataReceiver, MyBroadcastReceiver.makeGattUpdateIntentFilter());

        scanDialog = ProgressDialog.show(this, "", this.getBaseContext().getString(R.string.searchingDevices), true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case  R.id.action_menu:

                this.loadFragment(userSensorPresenter.getUserUserPanelView());
                pageNumber=0;
                canRePaint=true;
                return true;

            case R.id.action_ammo_calibration:
                   if(pageNumber==0)
                   {
                       userSensorPresenter.ammoCalibration();
                       pageNumber=0;
                   }

                return true;

            case R.id.action_water_calibration:
                if(pageNumber==0)
                {
                    if(userSensorPresenter.getUserUserPanelModel().isAmmoCalibrationed())  canRePaint=false;
                    this.loadFragment(waterCalibrationPresenter.getWaterCalibrationView());
                    userSensorPresenter.getUserUserPanelModel().setWaterCalibrationProcess(true);
                    waterCalibrationPresenter.startWorking(this);
                    pageNumber=1;
                }

                return true;

            case R.id.action_report:
                if(pageNumber==0)
                {
                    userSensorPresenter.generateReport();
                }

                return true;


            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed()
    {
        if(pageNumber==4||pageNumber==1||pageNumber==0)
        {
            canRePaint=true;
            this.loadFragment(userSensorPresenter.getUserUserPanelView());
            pageNumber=0;
            userSensorPresenter.getUserUserPanelModel().setDetailsFragment(false);
        }
    }

    @Override
    protected void onDestroy()
    {
        bleService.onDestroy();
        userSensorPresenter.destroy();
        super.onDestroy();

    }

    public void mainFragmentLoad() {
        if(userSensorPresenter.getUserUserPanelModel().isAmmoCalibrationed())this.canRePaint=true;
        this.loadFragment(userSensorPresenter.getUserUserPanelView());
        pageNumber=0;
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutUserSensor, fragment);
        ft.commit();

    }

    public void showDetailsFragment(boolean ammoCalibration)
    {
        userSensorPresenter.getUserUserPanelModel().setDetailsFragment(true);
       if(ammoCalibration)
       {
           if(userSensorPresenter.getUserUserPanelModel().isAmmoCalibrationed())
           {
               this.loadFragment(dataDetailsPresenter.getAmmoDetailsView());
               pageNumber=4;
           }
           else userSensorPresenter.getUserUserPanelView().showToast(this.getApplicationContext().getString(R.string.ammoNotCalibrationed));
       }
        else
       {

           if(userSensorPresenter.getUserUserPanelModel().isWaterCalibrationed())
           {
               this.loadFragment(dataDetailsPresenter.getWaterDetailsView());
               pageNumber=4;
           }
           else userSensorPresenter.getUserUserPanelView().showToast(this.getApplicationContext().getString(R.string.waterNotCalibrationed));
       }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleService = ((BleService.LocalBinder) service).getService();
            if (!bleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            boolean result = bleService.connect(deviceAddress);
            if(!result)
            {
                finish();
            }
            userSensorPresenter.setBleService(bleService);
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            bleService = null;
        }
    };


    public void disenableBleDevice()
    {
        toast = Toast.makeText(getApplicationContext(),R.string.notfoundDevice, Toast.LENGTH_SHORT);
        toast.show();
        bleService.onUnbind(new Intent(this,UserPanelActivity.class));
        bleService.stopService(new Intent(this,UserPanelActivity.class));
        unregisterReceiver(bleDataReceiver);
        Intent intent = new Intent(UserPanelActivity.this,PersonalisationActivity.class);
        startActivity(intent);
    }

    public void connected() {
        scanDialog.dismiss();
        userSensorPresenter.prepareView();
    }

    public BleService getBleService() {
        return bleService;
    }

    public boolean isCanRePaint() {
        return canRePaint;
    }

    public void setCanRePaint(boolean canRePaint) {
        this.canRePaint = canRePaint;
    }
}
