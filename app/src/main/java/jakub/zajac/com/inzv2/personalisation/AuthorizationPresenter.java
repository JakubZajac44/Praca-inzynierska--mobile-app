package jakub.zajac.com.inzv2.personalisation;

import android.content.Context;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.os.Handler;
import android.widget.ProgressBar;

import java.util.regex.Pattern;

import jakub.zajac.com.inzv2.PersonalisationActivity;
import jakub.zajac.com.inzv2.R;
import jakub.zajac.com.inzv2.bleCore.BleDeviceScanner;

/**
 * Created by Jakub on 20.12.2017.
 */

public class AuthorizationPresenter{


    private static final String REGISTRATION = "REGISTRATION";
    private RegistrationView registrationView;
    private LoginView loginView;
    private BleDeviceListView bleDeviceListView;
    private AuthorizationModel authorizationModel;
    private PersonalisationActivity personalisationActivity;
    private BleDeviceScanner bleDeviceScanner;
    private Context context;
    private String encryptedPassword;
    private int progressStatus;
    private boolean isScanning;
    private Handler progressHandler;


    public AuthorizationPresenter()
    {
        registrationView = new RegistrationView();
        loginView = new LoginView();
        bleDeviceListView = new BleDeviceListView();
        progressHandler = new Handler();

    }

    public void startWorking(PersonalisationActivity personalisationActivity)
    {
        registrationView.setAuthorizationPresenter(this);
        loginView.setAuthorizationPresenter(this);
        this.personalisationActivity = personalisationActivity;
        authorizationModel = new AuthorizationModel( personalisationActivity.getApplicationContext());
    }


    public void registerUser(String login, String password)
    {
        context = registrationView.getContext();
        Pattern patternLogin = Pattern.compile("[a-zA-Z0-9]{1,10}");
        Pattern patternPassword = Pattern.compile("[a-zA-Z0-9]{4,10}");
        if(patternLogin.matcher(login).matches()&&patternPassword.matcher(password).matches())
        {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(password.getBytes());
                encryptedPassword = new String(messageDigest.digest());
                registrationView.showToast(context.getString(R.string.correctUserData));
                bleDeviceListView.setAuthorizationPresenter(this);
                authorizationModel.getUserDao().setUserLogin(login);
                authorizationModel.getUserDao().setUserPassword(encryptedPassword);
                personalisationActivity.loadFragment(bleDeviceListView);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        else registrationView.showToast(context.getString(R.string.wrongUserData));
        Log.i(REGISTRATION,"Regiser new user");
    }

    public void startBleScanner()
    {
        authorizationModel.getDeviceList().clear();
        bleDeviceListView.clearList();
        bleDeviceScanner = new BleDeviceScanner(personalisationActivity.getBluetoothAdapter(),personalisationActivity);
        bleDeviceScanner.startScan();
        authorizationModel.getItemList().clear();
        progressStatus=0;
        isScanning=true;
        bleDeviceListView.setProgressBar(0);
        Handler mHandler = new Handler();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isScanning && progressStatus<=100){
                    progressStatus +=1;
                    try{
                        Thread.sleep(40);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    progressHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            bleDeviceListView.setProgressBar(progressStatus);
                        }
                    });
                }
            }
        }).start();


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bleDeviceScanner.getBleCallback().getCount()>0)
                {
                    bleDeviceListView.refreshMenu(authorizationModel.getDeviceList(),bleDeviceScanner.getBleCallback(),
                            authorizationModel.getDeviceName(),authorizationModel.getDeviceAddress(),authorizationModel.getItemList(),bleDeviceScanner.getBleCallback().getmLeDevices());
                    isScanning=false;
                }

            }
        },4000);

    }

    public void registerUserDeviceAddress(int positionDeviceList)
    {
        context =  bleDeviceListView.getContext();
        String deviceAddress = authorizationModel.getDeviceAddress().get(positionDeviceList);
        authorizationModel.getUserDao().setDeviceAddress(deviceAddress);
        if(authorizationModel.saveNewUser())
        {
            bleDeviceListView.showToast(context.getString(R.string.new_user_into_db));
            Log.i(REGISTRATION,"new user save into DB");
            personalisationActivity.newActivity(deviceAddress);
        }
        else
        {
            bleDeviceListView.showToast(context.getString(R.string.new_user_error_db));
            Log.i(REGISTRATION,"error during saving into DB");
        }

    }


    public void loginUser(String login,String password)
    {
        context=loginView.getContext();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            encryptedPassword = new String(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(authorizationModel.getRegisteredUser().getUserLogin().equals(login)&&authorizationModel.getRegisteredUser().getUserPassword().equals(encryptedPassword))
        {
            personalisationActivity.newActivity(authorizationModel.getUserDao().getDeviceAddress());
        }
        else
        {
            loginView.showToast(context.getString(R.string.wrong_user_login_data));
        }
    }

    public PersonalisationActivity getPersonalisationActivity() {
        return personalisationActivity;
    }

    public RegistrationView getRegistrationView() {
        return registrationView;
    }

    public LoginView getLoginView() {
        return loginView;
    }

}
