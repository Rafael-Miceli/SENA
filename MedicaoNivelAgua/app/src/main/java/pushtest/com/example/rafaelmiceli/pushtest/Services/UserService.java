package pushtest.com.example.rafaelmiceli.pushtest.Services;

import android.content.Context;
import android.content.SharedPreferences;

import pushtest.com.example.rafaelmiceli.pushtest.Models.Client;
import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Repositories.UserCloudRepository;

/**
 * Created by Rafael on 24/04/2015.
 */
public class UserService {


    private UserCloudRepository userRepository;

    public UserService(UserCloudRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {

        User user = userRepository.getByUsernameAndPassword(username, password);

        return user;
    }

    public void saveUserInMemory(String userId, String token, Context context) {
        SharedPreferences settings = context.getSharedPreferences("Client", 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("userId", userId);
        preferencesEditor.putString("token", token);
        preferencesEditor.commit();
    }

}
