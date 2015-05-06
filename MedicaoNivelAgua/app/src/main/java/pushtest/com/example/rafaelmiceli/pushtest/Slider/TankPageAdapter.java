package pushtest.com.example.rafaelmiceli.pushtest.Slider;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import pushtest.com.example.rafaelmiceli.pushtest.Models.Tank;

/**
 * Created by Rafael on 06/05/2015.
 */
public class TankPageAdapter extends FragmentPagerAdapter {

    ArrayList<Tank> mTanks;
    Context mContext;

    public TankPageAdapter(FragmentManager fm, Context context, ArrayList<Tank> tanks) {
        super(fm);

        mContext = context;
        mTanks = tanks;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putString(TankFragment.TANK_NAME, mTanks.get(position).getName());
        arguments.putInt(TankFragment.TANK_CRITICAL_LEVEL, mTanks.get(position).getCriticalLevel());

        TankFragment tankFragment = new TankFragment();
        tankFragment.setArguments(arguments);
        return tankFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTanks.get(position).getName();
    }

    @Override
    public int getCount() {
        return mTanks.size();
    }
}
