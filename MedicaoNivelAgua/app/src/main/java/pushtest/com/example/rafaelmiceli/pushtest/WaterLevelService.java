package pushtest.com.example.rafaelmiceli.pushtest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;

import java.net.MalformedURLException;

/**
 * Created by rafael.miceli on 27/02/2015.
 */
public class WaterLevelService {

    private MobileServiceJsonTable mClientsTable;
    private MobileServiceJsonTable mClientsTanksTable;
    private MobileServiceJsonTable mClientTableData;
    private MobileServiceClient mMobileServiceClient;
    private Context mContext;
    private static WaterLevelService instance;
    private String mClientName;

    public static WaterLevelService getInstance(Context context)
    {
        if (instance == null)
            instance = new WaterLevelService(context);

        return instance;
    }

    private WaterLevelService(Context context) {
        mContext = context;

        try {
            mMobileServiceClient = new MobileServiceClient("https://arduinoapp.azure-mobile.net/", "QkTMsFHSEaNGuiKVsywYYHpHnIHMUB64", mContext);

            mClientsTanksTable = mMobileServiceClient.getTable("ClientsTanks");
            mClientsTable = mMobileServiceClient.getTable("Clients");
            mClientTableData = mMobileServiceClient.getTable("TankLevel");

        } catch (MalformedURLException e) {
            Log.e("WaterLevelService", "There was an error creating the Mobile Service.  Verify the URL");
        }
    }


    public void getLatestLevelFromAzure(final String tankId, final TableJsonQueryCallback callback){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (mClientTableData != null)
                        mClientTableData.where().field("idclienttank").eq(tankId).orderBy("__createdAt", QueryOrder.Descending).top(1).execute(callback);
                } catch (Exception exception) {
                    Log.e("ErrorAuthService", "Error Azure AuthService - " + exception.getMessage());
                }
                return null;
            }
        }.execute();

    }

    public void setCriticalLevel(final TableJsonQueryCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mClientsTanksTable.where().field("id").eq("ACB10227-B6C9-4F49-A2AF-4227D0FBF0B7").execute(callback);
                } catch (Exception exception) {
                    Log.e("ErrorAuthService", "Error Azure AuthService - " + exception.getMessage());
                }
                return null;
            }
        }.execute();
    }

}
