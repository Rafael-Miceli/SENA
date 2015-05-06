package pushtest.com.example.rafaelmiceli.pushtest.Slider;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import pushtest.com.example.rafaelmiceli.pushtest.MyValueFormatter;
import pushtest.com.example.rafaelmiceli.pushtest.R;

/**
 * Created by Rafael on 06/05/2015.
 */
public class TankFragment extends Fragment {

    public final static String TANK_NAME = "Reservatorio";
    public final static String TANK_CRITICAL_LEVEL = "Valor";
    private String mTankName;
    private int mTankValue;

    protected BarChart mChart;
    private TextView mTxtCmDown;
    private TextView txtTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_tank, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTankName = arguments.getString(TANK_NAME);
            mTankValue = arguments.getInt(TANK_CRITICAL_LEVEL);

            initializeControls(theView);

            configureBarChart(theView);

            displayValues(mTankName, mTankValue);
        }

        return theView;
    }

    private void configureBarChart(View theView) {
        mChart.setDescription("");
        mChart.setDrawValueAboveBar(true);
        mChart.setMaxVisibleValueCount(2);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.setValueTextSize(10f);

        Typeface tf = Typeface.createFromAsset(theView.getContext().getAssets(), "OpenSans-Regular.ttf");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setLabelCount(8);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tf);
        rightAxis.setLabelCount(8);

        mChart.setValueFormatter(new MyValueFormatter());

        mChart.setValueTypeface(tf);

        Legend l = mChart.getLegend();
        if (l != null) {
            l.setLegendLabels(new String[] {"Nível d'água"});
            l.setEnabled(true);
        }
        else {
            l = new Legend();
            String[] labels = l.getLegendLabels();
            if (labels == null)
                labels = new String[] {"Nível d'água"};

            l.setEnabled(true);
        }
    }

    private void initializeControls(View theView) {
        mChart = (BarChart)  theView.findViewById(R.id.chart1);
        mTxtCmDown = (TextView) theView.findViewById(R.id.txtCmDown);
        txtTitle = (TextView) theView.findViewById(R.id.txtTitle);
    }

    private void displayValues(final String tankName, int tankValue) {
        txtTitle.setText("Queda de " + tankName + " em cm");
        //mTankValueTextView.setText(String.valueOf(tankValue));
    }
}
