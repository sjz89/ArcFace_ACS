package com.daylight.arcface_acs.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * 聊天信息
 * Created by Daylight on 2018/3/21.
 */

@Entity(tableName = "chat_table",
        indices = {@Index(value = "leftId"),@Index(value = "rightId")},
        foreignKeys = {@ForeignKey(entity = User.class,parentColumns = "phoneNum",childColumns = "rightId"),
                        @ForeignKey(entity = User.class,parentColumns = "phoneNum",childColumns = "leftId")})
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String leftId;
    private String rightId;
    private String text;
    private Long time;
    private int MessageType;

    public void setId(Long id) {
        this.id = id;
    }

    public void setLeftId(String leftId) {
        this.leftId = leftId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public String getLeftId() {
        return leftId;
    }

    public String getRightId() {
        return rightId;
    }

    public String getText() {
        return text;
    }

    public Long getTime() {
        return time;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public int getMessageType() {
        return MessageType;
    }
}
