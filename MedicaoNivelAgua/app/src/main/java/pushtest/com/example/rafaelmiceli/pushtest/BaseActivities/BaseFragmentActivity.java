package pushtest.com.example.rafaelmiceli.pushtest.BaseActivities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import pushtest.com.example.rafaelmiceli.pushtest.SenaApp;

/**
 * Created by Rafael on 26/05/2015.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SenaApp senaApp = (SenaApp)getApplication();

        senaApp.getObjectGraph().inject(this);
    }

}
