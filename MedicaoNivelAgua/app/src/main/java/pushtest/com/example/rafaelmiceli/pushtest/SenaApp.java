package pushtest.com.example.rafaelmiceli.pushtest;

import dagger.ObjectGraph;

/**
 * Created by Rafael on 05/05/2015.
 */
public class SenaApp extends android.app.Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new ApplicationModule(this));
    }

    public ObjectGraph getObjectGraph(){
        return objectGraph;
    }
}
