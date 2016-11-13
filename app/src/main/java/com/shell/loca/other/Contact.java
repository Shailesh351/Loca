package com.shell.loca.other;

/**
 * Created by shell on 11/11/16.
 */

public class Contact {
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
}
