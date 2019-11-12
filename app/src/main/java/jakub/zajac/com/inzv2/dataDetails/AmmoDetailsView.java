package jakub.zajac.com.inzv2.dataDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import jakub.zajac.com.inzv2.R;

/**
 * Created by Przemysław on 01.01.2018.
 */

public class AmmoDetailsView extends Fragment implements View.OnClickListener{

    private DataDetailsPresenter dataDetailsPresenter;
    private View view;
    private BarChart barChart;
    private Button clearDbButton;
    private TextView ammoDetailsTextTime;
    private XAxis xAxis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate( R.layout.ammo_details_fragment, container, false);
        clearDbButton = (Button) view.findViewById(R.id.clearDbButton);
        clearDbButton.setOnClickListener(this);
        barChart = (BarChart)view.findViewById(R.id.barChartAmmo);
        ammoDetailsTextTime = (TextView) view.findViewById(R.id.ammoDetailsText);
        barChart.setTouchEnabled(false);
        barChart.setFitBars(true);
        barChart.setNoDataTextColor(Color.BLACK);
        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.BLACK);

        YAxis yAxisL = barChart.getAxisLeft();
        yAxisL.setTextColor(Color.BLACK);
        yAxisL.setGridColor(Color.BLACK);

        YAxis yAxisR = barChart.getAxisRight();
        yAxisR.setTextColor(Color.BLACK);
        yAxisR.setGridColor(Color.BLACK);

        xAxis = barChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGridColor(Color.BLACK);
        xAxis.setTextSize(3.0f);
        barChart.setData(dataDetailsPresenter.prepareDataAmmoChart(xAxis));
        ammoDetailsTextTime.setText( ammoDetailsTextTime.getText()+" "+dataDetailsPresenter.countAmmoTime());
        return view;
    }



    public void setDataDetailsPresenter(DataDetailsPresenter dataDetailsPresenter) {
        this.dataDetailsPresenter = dataDetailsPresenter;
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==clearDbButton.getId())
        {
            dataDetailsPresenter.clearDb();
        }

    }
}
