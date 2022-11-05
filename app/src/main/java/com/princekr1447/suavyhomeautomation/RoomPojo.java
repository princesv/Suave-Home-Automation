package com.princekr1447.suavyhomeautomation;

import java.util.ArrayList;

public class RoomPojo {
    String title;
    ArrayList<Integer> indices;
    Boolean expandable=false;

    public RoomPojo(String title, ArrayList<Integer> indices) {
        this.title = title;
        this.indices = indices;
        expandable=false;
    }
    public RoomPojo() {
    }

    public Boolean getExpandable() {
        return expandable;
    }

    public void setExpandable(Boolean expandable) {
        this.expandable = expandable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.indices = indices;
    }
}
