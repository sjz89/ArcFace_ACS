package com.daylight.arcface_acs.bean;


import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * 小区实体类
 * Created by Daylight on 2018/2/4.
 */

public class Estate implements IPickerViewData {

    private int cid;
    private String name;
    private List<Building> buildings;

    public Estate(int cid, String name) {
        this.cid = cid;
        this.name = name;
        buildings = new ArrayList<>();
    }

    @Override
    public String getPickerViewText() {
        return name;
    }

    public int getCid() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

}
