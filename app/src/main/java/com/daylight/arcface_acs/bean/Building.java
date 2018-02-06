package com.daylight.arcface_acs.bean;

/**
 * 楼栋实体类
 * Created by Daylight on 2018/2/6.
 */
public class Building {
    private int cid;
    private int bid;
    private String name;

    public Building(int bid, String name) {
        this.bid = bid;
        this.name = name;
    }

    public int getBid() {
        return bid;
    }

    public String getName() {
        return name;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public void setName(String name) {
        this.name = name;
    }
}
