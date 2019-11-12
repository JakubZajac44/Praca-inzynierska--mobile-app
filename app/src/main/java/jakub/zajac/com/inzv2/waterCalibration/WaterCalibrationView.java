package jakub.zajac.com.inzv2.waterCalibration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jakub.zajac.com.inzv2.R;

/**
 * Created by Jakub on 29.12.2017.
 */

public class WaterCalibrationView extends Fragment implements View.OnClickListener{

    private View view;
    private ImageView positionImage;
    private ImageView waterCalibratonImage;
    private TextView waterLevelText;
    private ImageButton finishCalibrationButton;
    private WaterCalibrationPresenter waterCalibrationPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate( R.layout.water_calibration_fragment, container, false);

        positionImage = (ImageView)view.findViewById(R.id.positionVerticalImage);
        positionImage.setBackgroundResource(R.drawable.icon_x);
        waterCalibratonImage= (ImageView)view.findViewById(R.id.waterCalibrationImage);
        waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_0);
        waterLevelText = (TextView)view.findViewById(R.id.waterCalibrationLevel);
        finishCalibrationButton = (ImageButton)view.findViewById(R.id.CalibrationFinishButton);
        finishCalibrationButton.setBackgroundResource(R.drawable.icon_right_off);
        finishCalibrationButton.setOnClickListener(this);
        waterCalibrationPresenter.startCalibration();


        return view;
    }



    public void showToast(String message)
    {
        Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onClick(View view) {
        if(view.getId()== finishCalibrationButton.getId())
        {
            waterCalibrationPresenter.finishCalibration();
        }
    }

    public void setWaterCalibrationPresenter(WaterCalibrationPresenter waterCalibrationPresenter) {
        this.waterCalibrationPresenter = waterCalibrationPresenter;
    }


    public void setCalibrationData(boolean isHorizontal, int waterLevel) {

        if(isHorizontal)positionImage.setBackgroundResource(R.drawable.icon_ok);
        else   positionImage.setBackgroundResource(R.drawable.icon_x);
        if(isHorizontal&&waterLevel>=0)
        {

            finishCalibrationButton.setBackgroundResource(R.drawable.icon_right);
            if(waterLevel==0)
            {
                waterLevelText.setText(R.string.water0percent);
                waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_0);
            }
            else if(waterLevel==10)
            {
                waterLevelText.setText(R.string.water10percent);
                waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_10);
            }
            else if(waterLevel==25)
            {
                waterLevelText.setText(R.string.water35percent);
                waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_35);
            }
            else if(waterLevel==50)
            {
                waterLevelText.setText(R.string.water65percent);
                waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_65);
            }
            else if(waterLevel==75)
            {
                waterLevelText.setText(R.string.water80percent);
                waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_80);
            }
            else if(waterLevel==100)
            {
                waterLevelText.setText(R.string.water100percent);
                waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_full);
            }


        }
        else
        {

            waterCalibratonImage.setBackgroundResource(R.drawable.camelbak_0);
            finishCalibrationButton.setBackgroundResource(R.drawable.icon_right_off);
            waterLevelText.setText(R.string.waterError);

        }


    }
}
