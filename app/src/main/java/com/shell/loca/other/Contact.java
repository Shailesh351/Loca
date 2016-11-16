package com.shell.loca.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shell on 11/11/16.
 */

public class Contact implements Parcelable{
    private String name;
    private String mobileNo;

    public Contact(){}

    public Contact(String name, String mobileNo){
        this.name = name;

        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    protected Contact(Parcel in) {
        name = in.readString();
        mobileNo = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mobileNo);
    }
}
