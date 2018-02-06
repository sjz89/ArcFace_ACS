package com.daylight.arcface_acs.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


/**
 *
 * Created by Daylight on 2018/1/27.
 */

@Entity(tableName = "faces_table",
        indices = {@Index(value = "account"),@Index(value = "name",unique = true)},
        foreignKeys = @ForeignKey(entity = User.class,parentColumns = "phoneNum",childColumns = "account"))
public class Face {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String account;
    private byte[] faceData;
    private String name;
    private String type;
    private String validDate;

    public void setFaceData(byte[] faceData) {
        this.faceData = faceData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public byte[] getFaceData() {
        return faceData;
    }

    public String getName() {
        return name;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public String getType() {
        return type;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }
}
