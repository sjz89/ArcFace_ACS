package com.daylight.arcface_acs.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * 最近聊天
 * Created by Daylight on 2018/3/21.
 */
@Entity(tableName = "recent_table",
        indices = {@Index(value = "account"),@Index(value = "neighbor"),@Index(value = "uniqueId",unique = true)},
        foreignKeys = {@ForeignKey(entity = User.class,parentColumns = "phoneNum",childColumns = "account"),
                    @ForeignKey(entity = User.class,parentColumns = "phoneNum",childColumns = "neighbor")})
public class Recent {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String account;
    private String neighbor;
    private Long time;
    private String lastMsg;
    private String uniqueId;

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setNeighbor(String neighbor) {
        this.neighbor = neighbor;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Long getId() {

        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getNeighbor() {
        return neighbor;
    }

    public Long getTime() {
        return time;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {

        return uniqueId;
    }
}
