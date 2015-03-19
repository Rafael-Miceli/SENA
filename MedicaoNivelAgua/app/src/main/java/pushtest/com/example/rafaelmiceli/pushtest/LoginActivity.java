package pushtest.com.example.rafaelmiceli.pushtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

public class LoginActivity extends Activity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    private Button login_button;
    protected AuthService mAuthService;

    private EditText mTxtUsername;
    private EditText mTxtPassword;
    private ProgressBar progressBar;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthService = AuthService.getInstance(this);

        mTxtUsername = (EditText) findViewById(R.id.editTextLogin);
        mTxtPassword = (EditText) findViewById(R.id.editTextPassword);

        login_button = (Button)findViewById(R.id.button);
        login_button.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(final View v) {

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected void onPreExecute(){
                progressBar.setVisibility(View.VISIBLE);
                login_button.setEnabled(false);
            }

            @Override
            protected Object doInBackground(Object... params) {

                return loginClick(v);

            }

            @Override
            protected void onPostExecute(Object o) {
                if (!(Boolean)o) {
                    progressBar.setVisibility(View.GONE);
                    login_button.setEnabled(true);
                }
            }
        }.execute(null, null, null);
    }

    public boolean loginClick(View v){

        if (mTxtPassword.getText().toString().equals("") ||
                mTxtUsername.getText().toString().equals("")) {
            //We're just logging this here, we should show something to the user
            Log.w(TAG, "Username or password not entered");
            return false;
        }

        try {
            mAuthService.login(mTxtUsername.getText().toString(), mTxtPassword.getText().toString(), new TableJsonOperationCallback() {
                @Override
                public void onCompleted(JsonObject jsonObject, Exception exception,
                                        ServiceFilterResponse response) {
                    try {

                        if (exception == null) {
                            //If they've registered successfully, we'll save and set the userdata and then
                            //show the logged in activity
                            mAuthService.setUserAndSaveData(jsonObject);
                            Intent loggedInIntent = new Intent(getApplicationContext(), MyActivity.class);
                            startActivity(loggedInIntent);
                        } else {
                            //Erro ocorrendo neste momento

                            Log.e(TAG, "Error loggin in: " + exception.getMessage());
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Error loggin in em callback: " + ex.getMessage());
                        Toast.makeText(mContext, "Falha com realização de login: Verifique sua conexão com a Internet", Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                login_button.setEnabled(true);
                            }
                        });
                    }
                }
            });
        }
        catch (Exception ex) {
            Log.e(TAG, "Error loggin in: " + ex.getMessage());
            Toast.makeText(mContext, "Falha com realização de login: Verifique sua conexão com a Internet", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
