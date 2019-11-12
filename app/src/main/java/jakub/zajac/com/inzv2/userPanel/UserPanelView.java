package jakub.zajac.com.inzv2.userPanel;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jakub.zajac.com.inzv2.R;

/**
 * Created by Jakub on 23.12.2017.
 */

public class UserPanelView extends Fragment implements View.OnClickListener {

    private TextView waterLevelPercent;
    private TextView ammoLevel;
    private TextView waterLevelPercentText;
    private TextView ammoLevelText;
    private UserSensorPresenter userSensorPresenter;
    private ImageView ammoDetailsImage;
    private ImageView positionImage;
    private ImageView waterDetailsImage;
    private View view;
    private AlertDialog.Builder builder;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate( R.layout.user_sensor_fragment, container, false);
        waterLevelPercent = (TextView)view.findViewById(R.id.waterLevelPercent);
        ammoLevel = (TextView)view.findViewById(R.id.ammoLevel);
        waterLevelPercentText = (TextView)view.findViewById(R.id.waterLevelText);
        ammoLevelText = (TextView)view.findViewById(R.id.ammoLevelText);
        ammoDetailsImage=(ImageView)view.findViewById(R.id.ammoLevelImage);
        waterDetailsImage=(ImageView)view.findViewById(R.id.waterLevelImage);
        positionImage=(ImageView)view.findViewById(R.id.positionImage);
        this.setWaterLevelText(userSensorPresenter.setWaterLevelText());
        this.setPositionImage();
        this.setAmmoLevelText(userSensorPresenter.setAmmoLevelText(),false);
        ammoDetailsImage.setOnClickListener(this);
        waterDetailsImage.setOnClickListener(this);
        context = this.getContext();
       if(!ammoLevel.getText().toString().equals(this.getResources().getString(R.string.notCalibrationed)))this.drawAmmoText(Integer.valueOf(ammoLevel.getText().toString()));
        return view;
    }

    private void drawAmmoText(Integer ammoLevel) {

        int id;
        if(ammoLevel==0) id= R.drawable.magazynek_empty;
        else if(ammoLevel<5) id = R.drawable.magazynek_warning;
        else id = R.drawable.magazynek;

        Bitmap bitmap =
                BitmapFactory.decodeResource(context.getResources(), id);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        float scale = this.getContext().getResources().getDisplayMetrics().density;
        paint.setTextSize((int) (100 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        Rect bounds = new Rect();
        String aa = String.valueOf(ammoLevel);
        paint.getTextBounds(aa, 0, aa.length(), bounds);
        int x = 650;
        int y = 1420;
        if(ammoLevel<10)x=730;
        canvas.drawText(aa, x, y, paint);
        ammoDetailsImage.setImageBitmap(bitmap);
        Log.i(String.valueOf(y),String.valueOf(x));

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==ammoDetailsImage.getId())
        {
            userSensorPresenter.showAmmoDetails();
        }
        else if(v.getId()==waterDetailsImage.getId())
        {
            userSensorPresenter.showWaterDetails();
        }
    }



    public void ammoCalibration(int ammoLevel) {
        builder = new AlertDialog.Builder(userSensorPresenter.getUserUserPanelActivity());
        final int ammoLevelValue = ammoLevel;
        builder.setMessage(this.getContext().getString(R.string.ammoCalibrationDialog1) + String.valueOf(ammoLevelValue)+" "+this.getContext().getString(R.string.ammoCalibrationDialog2))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setAmmoLevelText(ammoLevelValue,true);
                        userSensorPresenter.setAmmoNotification(true);
                        dialog.cancel();
                        builder=null;
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UserPanelView.this.ammoLevel.setText(R.string.notCalibrationed);
                        ammoDetailsImage.setImageResource(R.drawable.magazynek_empty);
                        userSensorPresenter.setAmmoNotification(false);
                        dialog.cancel();
                        builder=null;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void prepareView() {
       waterLevelPercent.setText(R.string.notCalibrationed);
        ammoLevel.setText(R.string.notCalibrationed);
           }

    public void showToast(String message)
    {
        Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT).show();
    }

    public void showAlert(int resourceLevel, boolean ammoAlert) {

        if(builder==null)
        {
            builder = new AlertDialog.Builder(userSensorPresenter.getUserUserPanelActivity());
            String message;
            if(ammoAlert)message=this.getContext().getString(R.string.ammoAlert1) + ": "+String.valueOf(resourceLevel)+" "+this.getContext().getString(R.string.ammoAlert2);
            else
            {
                if(resourceLevel==0) message=this.getContext().getString(R.string.emptyWaterBag);
                message=this.getContext().getString(R.string.waterAlert1)+ ": "+String.valueOf(resourceLevel)+" "+this.getContext().getString(R.string.waterAlert2);
            }
            Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(2000);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            builder=null;

                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();

        }


    }

    public void setUserSensorPresenter(UserSensorPresenter userSensorPresenter) {
        this.userSensorPresenter = userSensorPresenter;
    }

    public void setPositionImage()
    {
        if(userSensorPresenter.getUserUserPanelModel().isVertical())
        {
            positionImage.setBackgroundResource(R.drawable.icon_ok);
        }
        else
        {
            positionImage.setBackgroundResource(R.drawable.icon_x);
        }
    }

    public void setWaterLevelText(int waterLevelPercent)
    {
        if(waterLevelPercent>=0)
        {
                this.waterLevelPercentText.setText(getString(R.string.WaterText1));
                if(waterLevelPercent==0)
                {
                    this.waterLevelPercent.setText(getString(R.string.water0percent));
                    this.waterDetailsImage.setImageResource(R.drawable.camelbak_0);
                }
                else if(waterLevelPercent==10)
                {
                    this.waterLevelPercent.setText(getString(R.string.water10percent));
                    this.waterDetailsImage.setImageResource(R.drawable.camelbak_10);
                }

                else if(waterLevelPercent==25)
                {
                    this.waterLevelPercent.setText(getString(R.string.water35percent));
                    this.waterDetailsImage.setImageResource(R.drawable.camelbak_35);
                }

                else if(waterLevelPercent==50)
                {
                    this.waterLevelPercent.setText(getString(R.string.water65percent));
                    this.waterDetailsImage.setImageResource(R.drawable.camelbak_65);
                }
                else if(waterLevelPercent==75)
                {
                    this.waterLevelPercent.setText(getString(R.string.water80percent));
                    this.waterDetailsImage.setImageResource(R.drawable.camelbak_80);
                }
                else if(waterLevelPercent==100)
                {

                    this.waterLevelPercent.setText(getString(R.string.waterfull));
                    this.waterDetailsImage.setImageResource(R.drawable.camelbak_full);
                }
            else this.waterLevelPercent.setText(R.string.waterError);
        }

    }

    public void setAmmoLevelText(int ammoLevel, boolean canRepaint)
    {
        if(ammoLevel>=0)
        {
            this.ammoLevel.setText(String.valueOf(ammoLevel));
            if(canRepaint)this.drawAmmoText(ammoLevel);
        }
        else this.ammoLevel.setText(R.string.notCalibrationed);


    }

    public void setAmmoCalibrationSource() {
        builder = new AlertDialog.Builder(userSensorPresenter.getUserUserPanelActivity());
        builder.setMessage(this.getContext().getString(R.string.ammoSource))
                .setCancelable(false)
                .setPositiveButton(R.string.distance, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        builder=null;
                        userSensorPresenter.setAmmoSource(true);
                    }
                })
                .setNegativeButton(R.string.tenso, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        builder=null;
                        userSensorPresenter.setAmmoSource(false);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();




    }
}
