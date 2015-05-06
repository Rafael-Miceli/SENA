package pushtest.com.example.rafaelmiceli.pushtest.Repositories;

import android.util.Pair;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;

import java.util.ArrayList;
import java.util.List;

import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Wrapper.WrappedMobileServiceJsonTable;

/**
 * Created by Rafael on 05/05/2015.
 */
public class UserAzureRepository implements UserCloudRepository{

    private WrappedMobileServiceJsonTable tableAccounts;
    private TableJsonOperationCallback accountInsertCallbackHandler;

    public UserAzureRepository(WrappedMobileServiceJsonTable wrappedMobileServiceJsonTable, TableJsonOperationCallback accountInsertCallbackHandler) {

        tableAccounts = wrappedMobileServiceJsonTable;
        this.accountInsertCallbackHandler = accountInsertCallbackHandler;
    }

    public User getByUsernameAndPassword(String username, String password) {
        JsonObject customUser = new JsonObject();
        customUser.addProperty("username", username);
        customUser.addProperty("password", password);

        List<Pair<String,String>> parameters = new ArrayList<Pair<String, String>>();
        parameters.add(new Pair<>("login", "true"));

        tableAccounts.insert(customUser, parameters, accountInsertCallbackHandler);

        return null;
    }
}
