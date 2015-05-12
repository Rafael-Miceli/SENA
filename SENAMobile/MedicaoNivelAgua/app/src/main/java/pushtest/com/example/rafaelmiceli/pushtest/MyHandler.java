package pushtest.com.example.rafaelmiceli.pushtest;

import com.microsoft.windowsazure.notifications.NotificationsHandler;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.ArrayMap;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pushtest.com.example.rafaelmiceli.pushtest.Models.Tank;
import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Repositories.InternalStorage;

/**
 * Created by rafael.miceli on 09/12/2014.
 */
public class MyHandler extends NotificationsHandler {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    Context context;
    public static Integer _criticalWaterLevel = 20;

    private HashMap<String, Integer> _criticalsLevels;
    private ArrayList<Tank> _tanks;

    public void initializeHandlerData() {
        try {
            User user = (User)InternalStorage.readObject(context, "user");

            _criticalsLevels = new HashMap<>();
            _tanks = new ArrayList<>();
            for (Tank tank: user.Client.getTanks() ) {
                _criticalsLevels.put(tank.getId(), tank.getCriticalLevel());
                _tanks.add(tank);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        this.context = context;

        initializeHandlerData();

        String level = bundle.getString("level");
        String idTank = bundle.getString("idTank");
        String tankName = "";

        for (Tank tank: _tanks) {
            if (tank.getId().equals(idTank)){
                tankName = tank.getName();
                break;
            }
        }

        if (isCriticalWaterLevel(idTank, level))
            sendNotification(tankName, level);

        updateCharts(level, tankName);
    }

    private boolean isCriticalWaterLevel(String idTank, String level) {
        //Nós medimos o nível de criticidade de nível de água de acordo com quantos centimetros cairam
        //do nível total do reservatório de água.

        return Integer.parseInt(level) >= _criticalsLevels.get(idTank);

    }

    private void updateCharts(String level, String tankName) {

        Intent intent = new Intent("water_level");

        intent.putExtra("level", level);
        intent.putExtra("tankName", tankName);

        context.sendBroadcast(intent);
    }


    private void sendNotification(String nameTank, String level) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MyActivity.class), 0);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Nível de água em " + nameTank + " muito baixo!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(level))
                        .setContentText(level)
                        .setSound(notificationSound);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}

