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
}
