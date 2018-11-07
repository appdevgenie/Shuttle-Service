package com.appdevgenie.shuttleservice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MainGridIcon implements Parcelable {

    private int iconImage;
    private String iconString;

    public MainGridIcon(int iconImage, String iconString) {
        this.iconImage = iconImage;
        this.iconString = iconString;
    }

    protected MainGridIcon(Parcel in) {
        iconImage = in.readInt();
        iconString = in.readString();
    }

    public static final Creator<MainGridIcon> CREATOR = new Creator<MainGridIcon>() {
        @Override
        public MainGridIcon createFromParcel(Parcel in) {
            return new MainGridIcon(in);
        }

        @Override
        public MainGridIcon[] newArray(int size) {
            return new MainGridIcon[size];
        }
    };

    public int getIconImage() {
        return iconImage;
    }

    public String getIconString() {
        return iconString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iconImage);
        dest.writeString(iconString);
    }
}
