package com.daylight.arcface_acs.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * 通行记录
 * Created by Daylight on 2018/3/18.
 */
@Entity(tableName = "record_table",
        indices = {@Index(value = "rid",unique = true)})
public class Record{
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String account;
    private int rid;
    private String text;
    private String subText;
    private Long time;

    public void setText(String text) {
        this.text = text;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getText() {
        return text;
    }

    public String getSubText() {
        return subText;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public Long getId() {
        return id;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }
}
