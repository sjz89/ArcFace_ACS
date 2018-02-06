package com.daylight.arcface_acs.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.daylight.arcface_acs.bean.Feature;

import java.util.List;

@Dao
public interface FeatureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Feature... features);
    @Delete
    void delete(Feature... features);
    @Query("Delete from feature_table where faceId=:faceId")
    void deleteFeatures(Long faceId);
    @Query("Select * from feature_table where faceId=:faceId")
    List<Feature> getFeatures(Long faceId);
}
