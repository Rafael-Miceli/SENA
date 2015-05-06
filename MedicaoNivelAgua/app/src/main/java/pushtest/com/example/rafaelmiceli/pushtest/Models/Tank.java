package pushtest.com.example.rafaelmiceli.pushtest.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rafael on 24/04/2015.
 */
public class Tank implements Parcelable {

    private String Name;
    private Integer CriticalLevel = 0;
    private Integer Level = 0;

    public Tank(Parcel parcel) {
        Name = parcel.readString();
        CriticalLevel = parcel.readInt();
        Level = parcel.readInt();
    }

    public String getName() {
        return Name;
    }

    public Integer getCriticalLevel() {
        return CriticalLevel;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setCriticalLevel(Integer CriticalLevel) {
        this.CriticalLevel = CriticalLevel;
    }

    public Integer getLevel() {
        return Level;
    }

    public void setLevel(Integer level) {
        Level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (CriticalLevel == null)
            CriticalLevel = 0;
        if (Level == null)
            Level = 0;

        dest.writeString(Name);
        dest.writeInt(CriticalLevel);
        dest.writeInt(Level);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Tank createFromParcel(Parcel in) {
            return new Tank(in);
        }

        public Tank[] newArray(int size) {
            return new Tank[size];
        }
    };

}
