package com.digitalartists.seabattle.model;

import android.os.Parcel;
import android.os.Parcelable;

// Settings class
public class Settings implements Parcelable {

    private int isDarkMode; // is dark mode enabled

    public Settings(int isDarkMode) {
        this.isDarkMode = isDarkMode;
    }

    protected Settings(Parcel in) {
        isDarkMode = in.readInt();
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    public int getIsDarkMode() {
        return isDarkMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(isDarkMode);
    }

    public void setIsDarkMode(int isDarkMode) {
        this.isDarkMode = isDarkMode;
    }

}
