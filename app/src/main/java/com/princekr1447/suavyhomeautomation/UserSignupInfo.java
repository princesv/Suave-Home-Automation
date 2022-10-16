package com.princekr1447.suavyhomeautomation;

import java.util.ArrayList;

public class UserSignupInfo {
    String pincode;
    ArrayList<String> productKey;
    String Address;
    String city;
    String state;

    public UserSignupInfo(String pincode, ArrayList<String> productKey, String address, String city, String state) {
        this.pincode = pincode;
        this.productKey = productKey;
        Address = address;
        this.city = city;
        this.state = state;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
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
}
