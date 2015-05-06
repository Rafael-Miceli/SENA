package pushtest.com.example.rafaelmiceli.pushtest.Callbacks;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

import pushtest.com.example.rafaelmiceli.pushtest.Models.Client;
import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Wrapper.GsonWrapper;

/**
 * Created by Rafael on 05/05/2015.
 */
public class AccountInsertCallbackHandler implements TableJsonOperationCallback {

    public final User[] user = {null};
    private Context context;
    private Intent intent;
    private GsonWrapper gson;

    public AccountInsertCallbackHandler(Context context, Intent intent, GsonWrapper gson) {
        this.context = context;
        this.intent = intent;
        this.gson = gson;
    }

    @Override
    public void onCompleted(JsonObject jsonObject, Exception e, ServiceFilterResponse serviceFilterResponse) {
        if (e != null)
            return;
        else
        {
            Client client = gson.fromJson(jsonObject.get("Client"), Client.class);
            String userId = jsonObject.getAsJsonPrimitive("userId").getAsString();
            String token = jsonObject.getAsJsonPrimitive("token").getAsString();

            User receivedUser = new User();
            receivedUser.client = client;

            user[0] = receivedUser;

            intent.putExtra("client", client);
            intent.putExtra("userId", userId);
            intent.putExtra("token", token);
            context.sendBroadcast(intent);
        }
    }
}
