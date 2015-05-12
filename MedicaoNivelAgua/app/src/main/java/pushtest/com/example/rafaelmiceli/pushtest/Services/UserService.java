package pushtest.com.example.rafaelmiceli.pushtest.Services;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import pushtest.com.example.rafaelmiceli.pushtest.Models.Client;
import pushtest.com.example.rafaelmiceli.pushtest.Models.User;
import pushtest.com.example.rafaelmiceli.pushtest.Repositories.InternalStorage;
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

    public void saveUserDataInMemory(String userId, String token, Client client, Context context) {
        SharedPreferences settings = context.getSharedPreferences("user", 0);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("userId", userId);
        preferencesEditor.putString("token", token);
        preferencesEditor.commit();

        User user = new User();
        user.Client = client;
        user.Token = token;
        user.UserId = userId;

        try {
            InternalStorage.writeObject(context, "user", user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
