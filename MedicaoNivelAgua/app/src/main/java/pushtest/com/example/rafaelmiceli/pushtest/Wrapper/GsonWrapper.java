package pushtest.com.example.rafaelmiceli.pushtest.Wrapper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Created by Rafael on 05/05/2015.
 */
public class GsonWrapper {
    private Gson gson;

    public GsonWrapper() {
        gson = new Gson();
    }

    public GsonWrapper(Gson gson) {
        this.gson = gson;
    }

    public <T> T fromJson(JsonElement json, Class<T> tClass){
        return gson.fromJson(json, tClass);
    }
}
