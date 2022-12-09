package com.princekr1447.suavyhomeautomation;

public class CentralModule {
    String name;
    String productKey;
    String id;

    public CentralModule(String name, String productKey,String id) {
        this.name = name;
        this.productKey = productKey;
        this.id=id;
    }

    public CentralModule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
