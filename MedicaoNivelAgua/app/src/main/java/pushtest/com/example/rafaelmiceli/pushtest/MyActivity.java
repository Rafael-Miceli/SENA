package pushtest.com.example.rafaelmiceli.pushtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;

import java.util.ArrayList;


public class MyActivity extends Activity {

    protected BarChart mChart;
    private Context mContext = this;
    private TextView mTxtCmDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mChart = (BarChart) findViewById(R.id.chart1);

        mTxtCmDown = (TextView) findViewById(R.id.txtCmDown);

        mChart.setDescription("");
        mChart.setDrawValueAboveBar(true);
        mChart.setMaxVisibleValueCount(2);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.setValueTextSize(10f);

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

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

        Integer value = getLatestWaterDistance();

        setData(value);

        Legend l = mChart.getLegend();
        l.setLegendLabels(new String[] {"Nível d'água"});
        l.setEnabled(true);

    }

    private void setData(float range) {

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Reservatório");

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        yVals1.add(new BarEntry(range, 0));

        BarDataSet set1 = new BarDataSet(yVals1, "Nível d'água");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        mChart.setData(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthService.getInstance(this).logout(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Integer getLatestWaterDistance() {

        final Integer[] latestWaterDistance = {0};

        WaterLevelService.getInstance(this).getLatestLevelFromAzure(new TableJsonQueryCallback() {
            @Override
            public void onCompleted(JsonElement jsonElement, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
                try {
                    if (e != null) {
                        Log.e("ErrorActivity", "Error Azure Activity from WaterLevelService - " + e.getMessage());
                        return;
                    }

                    JsonArray results = jsonElement.getAsJsonArray();


                    for (JsonElement item : results){

                       latestWaterDistance[0] = item.getAsJsonObject().getAsJsonPrimitive("Nivel").getAsInt();
                    }

                    updateViews(latestWaterDistance[0]);
                }
                catch (Exception exception) {
                    Log.e("ErrorActivity", "Error Azure Activity in Activity - " + exception.getMessage());
                }
            }
        });

        return 200;
    }

    public void updateViews(Integer latestWaterDistance) {
        setData((200 - latestWaterDistance));
        mTxtCmDown.setText(latestWaterDistance.toString());
        mChart.invalidate();
        mTxtCmDown.invalidate();
    }

    @Override
    public void onResume(){
        super.onResume();
        mContext.registerReceiver(mMessageReceiver, new IntentFilter("water_level"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContext.unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("azureMessage");

            updateViews(Integer.parseInt(message));
        }
    };
}
