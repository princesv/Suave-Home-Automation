package com.princekr1447.suavyhomeautomation;

public class HomePhotoPojo {
    private String url;
    private String photoUUID;

    public HomePhotoPojo(String url, String photoUUID) {
        this.url = url;
        this.photoUUID = photoUUID;
    }
    public HomePhotoPojo(){

    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoUUID() {
        return photoUUID;
    }

    public void setPhotoUUID(String photoUUID) {
        this.photoUUID = photoUUID;
    }
}
