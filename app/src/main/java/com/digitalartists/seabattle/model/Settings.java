package com.digitalartists.seabattle.model;

import android.os.Parcel;
import android.os.Parcelable;

// Settings class
public class Settings implements Parcelable {

    private int isDarkMode; // is dark mode enabled
    private String hostIPAddress;
    private String guestIPAddress;
    private String role;

    public Settings(int isDarkMode, String hostIPAddress, String guestIPAddress, String role) {
        this.isDarkMode = isDarkMode;
        this.hostIPAddress = hostIPAddress;
        this.guestIPAddress = guestIPAddress;
        this.role = role;
    }

    protected Settings(Parcel in) {
        isDarkMode = in.readInt();
        hostIPAddress = in.readString();
        guestIPAddress = in.readString();
        role = in.readString();
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

    public String getHostIPAddress() {
        return hostIPAddress;
    }

    public String getGuestIPAddress() {
        return guestIPAddress;
    }

    public String getRole() {
        return role;
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

    public void setHostIPAddress(String hostIPAddress) {
        this.hostIPAddress = hostIPAddress;
    }

    public void setGuestIPAddress(String guestIPAddress) {
        this.guestIPAddress = guestIPAddress;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
