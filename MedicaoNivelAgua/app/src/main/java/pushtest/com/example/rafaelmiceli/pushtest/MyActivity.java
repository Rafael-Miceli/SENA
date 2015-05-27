package pushtest.com.example.rafaelmiceli.pushtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import javax.inject.Inject;

import pushtest.com.example.rafaelmiceli.pushtest.BaseActivities.BaseFragmentActivity;
import pushtest.com.example.rafaelmiceli.pushtest.Models.Tank;
import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Services.UserService;
import pushtest.com.example.rafaelmiceli.pushtest.Slider.TankPageAdapter;


public class MyActivity extends BaseFragmentActivity {

    private Context context = this;

    TankPageAdapter mTankPageAdapter;
    ViewPager mViewPager;

    @Inject
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        ArrayList<Tank> tanks = null;

        if (getIntent().getExtras() != null)
            tanks = getIntent().getExtras().getParcelableArrayList("tanks");
        else {
            User user = userService.getUserInMemory(context);
            tanks = user.Client.getTanks();
        }

        mTankPageAdapter = new TankPageAdapter(getSupportFragmentManager(), context, tanks);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTankPageAdapter);
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
}
