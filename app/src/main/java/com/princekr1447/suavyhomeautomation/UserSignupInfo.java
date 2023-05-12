package com.princekr1447.suavyhomeautomation;

import java.util.ArrayList;

public class UserSignupInfo {
    String pincode;
    ArrayList<String> productKey;
    String addressL1;
    String addressL2;
    String city;
    String state;
    String country;
    String phoneNumber;

    public UserSignupInfo(String addressL1,String addressL2, String city, String state,String pincode,String country,String phoneNumber, ArrayList<String> productKey) {
        this.pincode = pincode;
        this.productKey = productKey;
        this.addressL1= addressL1;
        this.addressL2=addressL2;
        this.city = city;
        this.state = state;
        this.country=country;
        this.phoneNumber=phoneNumber;
    }

    public String getAddressL1() {
        return addressL1;
    }

    public void setAddressL1(String addressL1) {
        addressL1 = addressL1;
    }

    public String getAddressL2() {
        return addressL2;
    }

    public void setAddressL2(String addressL2) {
        addressL2 = addressL2;
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

    public ArrayList<String> getProductKey() {
        return productKey;
    }

    public void setProductKey(ArrayList<String> productKey) {
        this.productKey = productKey;
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
