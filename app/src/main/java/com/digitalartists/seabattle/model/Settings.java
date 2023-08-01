package com.digitalartists.seabattle.model;

import android.os.Parcel;
import android.os.Parcelable;

// Settings class
public class Settings implements Parcelable {

    private int cols;       // number of columns
    private int rows;       // number of rows
    private int minesNum;   // number of mines
    private int isDarkMode; // is dark mode enabled

    public Settings(int cols, int rows, int minesNum, int isDarkMode) {
        this.cols = cols;
        this.rows = rows;
        this.minesNum = minesNum;
        this.isDarkMode = isDarkMode;
    }

    protected Settings(Parcel in) {
        cols = in.readInt();
        rows = in.readInt();
        minesNum = in.readInt();
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

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getMinesNum() {
        return minesNum;
    }

    public int getIsDarkMode() {
        return isDarkMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(cols);
        parcel.writeInt(rows);
        parcel.writeInt(minesNum);
        parcel.writeInt(isDarkMode);
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setMinesNum(int minesNum) {
        this.minesNum = minesNum;
    }

    public void setIsDarkMode(int isDarkMode) {
        this.isDarkMode = isDarkMode;
    }

}
