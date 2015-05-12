package pushtest.com.example.rafaelmiceli.pushtest.Repositories;

import pushtest.com.example.rafaelmiceli.pushtest.Models.User;

/**
 * Created by Rafael on 24/04/2015.
 */
public interface UserCloudRepository {
    User getByUsernameAndPassword(String username, String password);
}
