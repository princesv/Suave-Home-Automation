package com.princekr1447.suavyhomeautomation;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomPojo {
    String title;
    String id;
    HashMap<String, IndexPojo> indices;

    public RoomPojo(String title, HashMap<String, IndexPojo> indices,String id) {
        this.title = title;
        this.indices = indices;
        this.id=id;
    }
    public RoomPojo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, IndexPojo> getIndices() {
        return indices;
    }

    public void setIndices(HashMap<String, IndexPojo> indices) {
        this.indices = indices;
    }
}
