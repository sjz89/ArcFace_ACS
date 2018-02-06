package com.daylight.arcface_acs.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "feature_table",
        indices = {@Index(value = "faceId")},
        foreignKeys = @ForeignKey(entity = Face.class,parentColumns = "id",childColumns = "faceId"))
public class Feature {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long faceId;
    private byte[] featureData;
    private byte[] imageData;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
    }

    public void setFeatureData(byte[] featureData) {
        this.featureData = featureData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public Long getId() {
        return id;
    }

    public Long getFaceId() {
        return faceId;
    }

    public byte[] getFeatureData() {
        return featureData;
    }
}
