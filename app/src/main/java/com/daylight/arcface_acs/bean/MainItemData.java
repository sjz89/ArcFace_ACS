package com.daylight.arcface_acs.bean;

/**
 *
 * Created by Daylight on 2018/3/17.
 */

public class MainItemData {
    private String text;
    private int pic;
    public MainItemData(String text,int pic){
        this.text=text;
        this.pic=pic;
    }

    public String getText() {

        return text;
    }

    public int getPic() {
        return pic;
    }
}
