package pushtest.com.example.rafaelmiceli.pushtest;

import android.app.Activity;
import android.app.Application;

/**
 * Created by Rafael on 12/02/2015.
 */
public class AuthenticationApplication extends Application {
    private AuthService mAuthService;
    private Activity mCurrentActivity;

    public AuthenticationApplication() {}


    public void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }
}
