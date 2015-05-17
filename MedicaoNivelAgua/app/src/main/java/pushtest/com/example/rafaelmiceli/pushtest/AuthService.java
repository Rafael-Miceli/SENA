package pushtest.com.example.rafaelmiceli.pushtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import org.apache.http.StatusLine;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class AuthService {

    private NotificationService mNotificationService;
    private MobileServiceClient mClient;
    private MobileServiceJsonTable mTableAccounts;
    private boolean mShouldRetryAuth;
    private Context mContext;
    private final String TAG = "AuthService";
    private boolean mIsCustomAuthProvider = false;
    private MobileServiceAuthenticationProvider mProvider;

    private static AuthService instance;

    public static AuthService getInstance(Context context)
    {
        if (instance == null)
            instance = new AuthService(context);

        return instance;
    }

    private AuthService(Context context) {
        mContext = context;

        try {
            mClient = new MobileServiceClient("https://arduinoapp.azure-mobile.net/", "QkTMsFHSEaNGuiKVsywYYHpHnIHMUB64", mContext)
                    .withFilter(new MyServiceFilter());

            mTableAccounts = mClient.getTable("accounts");

            mNotificationService = NotificationService.getInstance(mContext);

        } catch (MalformedURLException e) {
            Log.e(TAG, "There was an error creating the Mobile Service.  Verify the URL");
        }
    }

    public String getUserId() {
        return mClient.getCurrentUser().getUserId();
    }

    public void login(String username, String password, TableJsonOperationCallback callback) {
        JsonObject customUser = new JsonObject();
        customUser.addProperty("username", username);
        customUser.addProperty("password", password);

        List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
        parameters.add(new Pair<>("login", "true"));

        mTableAccounts.insert(customUser, parameters, callback);
    }

    public boolean isUserAuthenticated() {
        SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
        if (settings != null) {
            String userId = settings.getString("userid", null);
            String token = settings.getString("token", null);
            if (userId != null && !userId.equals("")) {
                setUserData(userId, token);
                return true;
            }
        }
        return false;
    }

    public void setUserData(String userId, String token) {
        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        mClient.setCurrentUser(user);

        //Check for custom provider
        String provider = userId.substring(0, userId.indexOf(":"));
        if (provider.equals("Custom")) {
            mProvider = null;
            mIsCustomAuthProvider = true;
        }
    }

    public void saveUserData() {
        SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("userid", mClient.getCurrentUser().getUserId());
        preferencesEditor.putString("token", mClient.getCurrentUser().getAuthenticationToken());
        preferencesEditor.commit();
    }

    public void logout(boolean shouldRedirectToLogin) {
        //Clear the cookies so they won't auto login to a provider again
        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        //Clear the user id and token from the shared preferences
        SharedPreferences settings = mContext.getSharedPreferences("UserData", 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
        //Clear the user and return to the auth activity
        mClient.logout();
        //Take the user back to the auth activity to relogin if requested
        if (shouldRedirectToLogin) {
            //unsubscribe to push
            mNotificationService.unsubscribeToClient();
            Intent logoutIntent = new Intent(mContext, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(logoutIntent);
        }
    }

    /**
     * Custom ServiceFilter which facilitates retry on 401s (if requested)
     */
    private class MyServiceFilter implements ServiceFilter {

        //A versão do AzureMobileService usada neste App é a 1.1.0
        //Ao atualizar para nova versão, lembrar que a assinatura da Interface ServiceFilter mudou


        @Override
        public void handleRequest(final ServiceFilterRequest request, final NextServiceFilterCallback nextServiceFilterCallback,
                                  final ServiceFilterResponseCallback responseCallback) {


            nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {

                    try {

                        if (exception != null) {
                            //Error begining here : Error while processing request

                            Log.e(TAG, "MyServiceFilter onResponse Exception: " + exception.getMessage());
                        }


                        StatusLine status = response.getStatus();
                        int statusCode = status.getStatusCode();
                        if (statusCode == 401) {
                            final CountDownLatch latch = new CountDownLatch(1);
                            //Log the user out but don't send them to the login page
                            logout(false);
                            //If we shouldn't retry (or they've used custom auth),
                            //we're going to kick them out for now
                            //If you're doing custom auth, you'd need to show your own
                            //custom auth popup to login with
                            if (mShouldRetryAuth && !mIsCustomAuthProvider) {
                                //Get the current activity for the context so we can show the login dialog
                                AuthenticationApplication myApp = (AuthenticationApplication) mContext;
                                Activity currentActivity = myApp.getCurrentActivity();
                                mClient.setContext(currentActivity);

                                currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mClient.login(mProvider, new UserAuthenticationCallback() {
                                            @Override
                                            public void onCompleted(MobileServiceUser user, Exception exception,
                                                                    ServiceFilterResponse response) {
                                                if (exception == null) {
                                                    //Save their updated user data locally
                                                    saveUserData();
                                                    //Update the requests X-ZUMO-AUTH header
                                                    request.removeHeader("X-ZUMO-AUTH");
                                                    request.addHeader("X-ZUMO-AUTH", mClient.getCurrentUser().getAuthenticationToken());

                                                    //Add our BYPASS querystring parameter to the URL
                                                    Uri.Builder uriBuilder = Uri.parse(request.getUrl()).buildUpon();
                                                    uriBuilder.appendQueryParameter("bypass", "true");
                                                    try {
                                                        request.setUrl(uriBuilder.build().toString());
                                                    } catch (URISyntaxException e) {
                                                        Log.e(TAG, "Couldn't set request's new url: " + e.getMessage());
                                                        e.printStackTrace();
                                                    }
                                                    latch.countDown();

                                                } else {
                                                    Log.e(TAG, "User did not login successfully after 401");
                                                    //Kick user back to login screen
                                                    logout(true);
                                                }

                                            }
                                        });
                                    }
                                });
                                try {
                                    latch.await();
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Interrupted exception: " + e.getMessage());
                                    return;
                                }

                                nextServiceFilterCallback.onNext(request, responseCallback);
                            } else {
                                //Log them out and proceed with the response
                                logout(true);
                                responseCallback.onResponse(response, exception);
                            }
                        } else {//
                            responseCallback.onResponse(response, exception);
                        }
                    }
                    catch(Exception ex) {
                        Log.e(TAG, "MyServiceFilter onResponse Exception: " + exception.getMessage());
                    }
                }
            });
        }
    }

}
