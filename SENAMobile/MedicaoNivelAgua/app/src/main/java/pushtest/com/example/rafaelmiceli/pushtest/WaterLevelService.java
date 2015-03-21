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

    private MobileServiceJsonTable mClientTableData;
    private MobileServiceClient mClient;
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
            mClient = new MobileServiceClient("https://arduinoapp.azure-mobile.net/", "QkTMsFHSEaNGuiKVsywYYHpHnIHMUB64", mContext);

        } catch (MalformedURLException e) {
        }
    }

    public void setClientTableData(String clientTableDataName) {
        mClientTableData = mClient.getTable(clientTableDataName);
    }

    public void getLatestLevelFromAzure(final TableJsonQueryCallback callback){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (mClientTableData != null)
                        mClientTableData.orderBy("__createdAt", QueryOrder.Descending).top(1).execute(callback);
                } catch (Exception exception) {
                }
                return null;
            }
        }.execute();

    }
}
