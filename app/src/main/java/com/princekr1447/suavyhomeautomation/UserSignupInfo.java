package com.princekr1447.suavyhomeautomation;

import java.util.ArrayList;

public class UserSignupInfo {
    String pincode;
    String addressL1;
    String addressL2;
    String city;
    String state;
    String country;
    String phoneNumber;

    public UserSignupInfo(String addressL1,String addressL2, String city, String state,String pincode,String country,String phoneNumber) {
        this.pincode = pincode;
        this.addressL1= addressL1;
        this.addressL2=addressL2;
        this.city = city;
        this.state = state;
        this.country=country;
        this.phoneNumber=phoneNumber;
    }
    public UserSignupInfo() {
    }

    public String getAddressL1() {
        return addressL1;
    }

    public void setAddressL1(String addressL1) {
        this.addressL1 = addressL1;
    }

    public String getAddressL2() {
        return addressL2;
    }

    public void setAddressL2(String addressL2) {
        this.addressL2 = addressL2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
