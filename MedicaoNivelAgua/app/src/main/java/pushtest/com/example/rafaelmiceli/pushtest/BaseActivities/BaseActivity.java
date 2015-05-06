package pushtest.com.example.rafaelmiceli.pushtest.BaseActivities;

import android.app.Activity;
import android.os.Bundle;

import pushtest.com.example.rafaelmiceli.pushtest.SenaApp;

/**
 * Created by Rafael on 05/05/2015.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SenaApp senaApp = (SenaApp)getApplication();

        senaApp.getObjectGraph().inject(this);
    }
}
