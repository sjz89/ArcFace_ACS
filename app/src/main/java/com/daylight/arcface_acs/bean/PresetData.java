package com.daylight.arcface_acs.bean;

public class PresetData {
    private String type;
    private boolean isChecked;

    public PresetData(String type,boolean isChecked){
        this.type=type;
        this.isChecked=isChecked;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getType() {
        return type;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
