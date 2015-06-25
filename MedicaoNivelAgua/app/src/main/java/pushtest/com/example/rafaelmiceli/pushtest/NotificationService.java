package pushtest.com.example.rafaelmiceli.pushtest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.util.Set;

/**
 * Created by rafael.miceli on 26/02/2015.
 */
public class NotificationService {

    private String SENDER_ID = "709979546467";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private Context mContext;

    private static NotificationService notificationServiceInstance;

    public static NotificationService getInstance(Context context){
        if (notificationServiceInstance == null)
            notificationServiceInstance = new NotificationService(context);

        return notificationServiceInstance;
    }

    private NotificationService(Context context){
        mContext = context;

        NotificationsManager.handleNotifications(mContext, SENDER_ID, MyHandler.class);

        gcm = GoogleCloudMessaging.getInstance(mContext);

        String connectionString = "Endpoint=sb://arduinoapphub2-ns.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=wGpbXy3zlfIgaJKQybw0xdTavi+TZUlLby6jfqVmfFM=";
        hub = new NotificationHub("arduinoapphub", connectionString, mContext);
    }

    public void subscribeToClient(final Set<String> clients) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    hub.register(regid, clients.toArray(new String[clients.size()]));
                } catch (Exception e) {
                    Log.e("LoginActivity", "Failed to register - " + e.getMessage());
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                if (result != null)
                    Toast.makeText(mContext, result.toString(), Toast.LENGTH_LONG).show();

                String message = "Bem-vindo as informacões dos reservatórios de: " + clients.toString();
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    public void unsubscribeToClient() {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    hub.unregister();
                } catch (Exception e) {
                    Log.e("MainActivity", "Failed to unregister - " + e.getMessage());
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                String message = "Fim da sessão do cliente ";
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

}
