package com.daylight.arcface_acs.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 *
 * Created by Daylight on 2018/1/26.
 */
@Entity(tableName = "user_table",indices = {@Index(value = {"phoneNum"},unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String phoneNum;
    private String password;
    private String name;
    private String idNum;
    private String communityName;
    private String buildingName;
    private String doorNum;
    private String pattern;
    private String pin;
    private boolean isManager;
    private boolean authorize;
    private boolean hasPatternLock;
    private boolean isSecurity;
    private int status;

    public Long getId() {
        return id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getCommunityName() {
        return communityName;
    }

    public String getPattern() {
        return pattern;
    }

    public String getPin() {
        return pin;
    }

    public boolean isHasPatternLock() {
        return hasPatternLock;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setHasPatternLock(boolean hasPatternLock) {
        this.hasPatternLock = hasPatternLock;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setDoorNum(String doorNum) {
        this.doorNum = doorNum;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getDoorNum() {
        return doorNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setAuthorize(boolean authorize) {
        this.authorize = authorize;
    }

    public boolean isAuthorize() {
        return authorize;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isSecurity() {
        return isSecurity;
    }

    public void setSecurity(boolean security) {
        isSecurity = security;
    }
}
