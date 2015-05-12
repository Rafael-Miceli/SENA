package pushtest.com.example.rafaelmiceli.pushtest.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rafael on 24/04/2015.
 */
public class Client implements Parcelable, Serializable {

    public String name;
    private ArrayList<Tank> tanks;

    public Client(Parcel parcel) {
        name = parcel.readString();
        tanks = parcel.readArrayList(getClass().getClassLoader());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Tank> getTanks() {
        return tanks;
    }

    public void setTanks(ArrayList<Tank> tanks) {
        this.tanks = tanks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(tanks);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
}
