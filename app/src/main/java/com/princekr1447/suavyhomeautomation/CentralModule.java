package com.princekr1447.suavyhomeautomation;

public class CentralModule {
    String name;
    String productKey;

    public CentralModule(String name, String productKey) {
        this.name = name;
        this.productKey = productKey;
    }

    public CentralModule() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
}
