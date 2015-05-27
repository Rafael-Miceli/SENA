package pushtest.com.example.rafaelmiceli.pushtest;

import android.content.Context;
import android.content.Intent;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pushtest.com.example.rafaelmiceli.pushtest.Callbacks.AccountInsertCallbackHandler;
import pushtest.com.example.rafaelmiceli.pushtest.Repositories.UserAzureRepository;
import pushtest.com.example.rafaelmiceli.pushtest.Services.UserService;
import pushtest.com.example.rafaelmiceli.pushtest.Wrapper.GsonWrapper;
import pushtest.com.example.rafaelmiceli.pushtest.Wrapper.WrappedMobileServiceJsonTable;

/**
 * Created by Rafael on 05/05/2015.
 */
@Module(library = true, injects = {LoginActivity.class, MyActivity.class})
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public UserAzureRepository providesUserRepository() {
        MobileServiceClient mobileServiceClient = null;

        try {
            mobileServiceClient = new MobileServiceClient("https://arduinoapp.azure-mobile.net/", "QkTMsFHSEaNGuiKVsywYYHpHnIHMUB64", context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        WrappedMobileServiceJsonTable wrappedMobileServiceJsonTable = new WrappedMobileServiceJsonTable(mobileServiceClient.getTable("accounts"));
        AccountInsertCallbackHandler accountInsertCallbackHandler = new AccountInsertCallbackHandler(context, new Intent("Login"), new GsonWrapper());

        return new UserAzureRepository(wrappedMobileServiceJsonTable, accountInsertCallbackHandler);
    }

    @Provides
    @Singleton
    public UserService providesUserService() {
        return new UserService(providesUserRepository());
    }

}
