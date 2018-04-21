package com.daylight.arcface_acs.bean;


public class TempData{
    private byte[] face;
    private boolean isChecked;

    public TempData(byte[] image,boolean isChecked){
        this.face =image;
        this.isChecked=isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setFace(byte[] face) {
        this.face = face;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public byte[] getFace() {
        return face;
    }

}