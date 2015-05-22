package pushtest.com.example.rafaelmiceli.pushtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import pushtest.com.example.rafaelmiceli.pushtest.BaseActivities.BaseActivity;
import pushtest.com.example.rafaelmiceli.pushtest.Models.Client;
import pushtest.com.example.rafaelmiceli.pushtest.Models.Tank;
import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Repositories.InternalStorage;
import pushtest.com.example.rafaelmiceli.pushtest.Services.UserService;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "LoginActivity";

    @Inject
    UserService userService;

    private ButtonRectangle login_button;
    protected NotificationService notificationService;

    private EditText mTxtUsername;
    private EditText mTxtPassword;
    private ProgressBar progressBar;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        notificationService = NotificationService.getInstance(this);

        mTxtUsername = (EditText) findViewById(R.id.editTextLogin);
        mTxtPassword = (EditText) findViewById(R.id.editTextPassword);

        login_button = (ButtonRectangle)findViewById(R.id.button);
        login_button.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        verifyUserAlreadyLoggedIn();
    }

    private void verifyUserAlreadyLoggedIn() {
        SharedPreferences settings = context.getSharedPreferences("user", 0);


        String token = settings.getString("token", "");
        if (!token.isEmpty()){

            User user = userService.getUserInMemory(context);

            if (user == null)
                return;

            ArrayList<Tank> tanksWithLevelsUpdated = updateMyTanksLevel(user.Client.getTanks());

            Intent loggedInIntent = new Intent(getApplicationContext(), MyActivity.class);
            loggedInIntent.putExtra("tanks", tanksWithLevelsUpdated);
            startActivity(loggedInIntent);
        }
    }

    private ArrayList<Tank> updateMyTanksLevel(ArrayList<Tank> tanks) {
        return tanks;
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

        try{

            userService.login(mTxtUsername.getText().toString(), mTxtPassword.getText().toString());

        }
        catch (Exception ex) {
            Log.e(TAG, "Error loggin in em callback: " + ex.getMessage());
                        Toast.makeText(context, "Falha com realização de login: Verifique sua conexão com a Internet", Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                login_button.setEnabled(true);
                            }
                        });

            return false;
        }

        return true;
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Client client = intent.getParcelableExtra("client");

                if (client == null) {
                    Toast.makeText(context, "Falha com realização de login: e-mail ou senha incorretos", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    login_button.setEnabled(true);
                    return;
                }

                String userId = intent.getStringExtra("userId");
                String token = intent.getStringExtra("token");

                userService.saveUserDataInMemory(userId, token, client, context);

                Set<String> clients = new HashSet<>();
                clients.add(client.getName());

                notificationService.subscribeToClient(clients);

                Intent loggedInIntent = new Intent(getApplicationContext(), MyActivity.class);
                loggedInIntent.putExtra("tanks", client.getTanks());
                startActivity(loggedInIntent);
            }
            catch (Exception ex) {
                Toast.makeText(context, "Falha com realização de login: erro interno", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                login_button.setEnabled(true);
            }

        }
    };

    @Override
    public void onResume(){
        super.onResume();
        context.registerReceiver(messageReceiver, new IntentFilter("Login"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(messageReceiver);
    }
}
