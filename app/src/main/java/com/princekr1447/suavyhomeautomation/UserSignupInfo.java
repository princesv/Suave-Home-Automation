package com.princekr1447.suavyhomeautomation;
public class UserSignupInfo {
    String pincode;
    String productKey;
    String Address;
    String city;
    String state;
    String keyPos;

    public UserSignupInfo(String pincode, String productKey, String address, String city, String state,String keyPos) {
        this.pincode = pincode;
        this.productKey = productKey;
        Address = address;
        this.city = city;
        this.state = state;
        this.keyPos=keyPos;
    }

    public String getKeyPos() {
        return keyPos;
    }

    public void setKeyPos(String keyPos) {
        this.keyPos = keyPos;
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

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
}
