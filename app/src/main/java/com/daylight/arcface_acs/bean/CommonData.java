package com.daylight.arcface_acs.bean;

/**
 *
 * Created by Daylight on 2018/3/18.
 */



public class CommonData{
    private byte[] image;
    private String pic;
    private int icon;
    private String text;
    private String subText;
    private String time;
    public CommonData(String... data){
        this.text=data[0];
        this.subText=data[1];
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public byte[] getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public String getSubText() {
        return subText;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}