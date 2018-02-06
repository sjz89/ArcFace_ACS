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
    private String idNum;
    private String type;
    private String startDate;
    private String endDate;

    public void setFaceData(byte[] faceData) {
        this.faceData = faceData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
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

    public String getStartDate() {
        return startDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
