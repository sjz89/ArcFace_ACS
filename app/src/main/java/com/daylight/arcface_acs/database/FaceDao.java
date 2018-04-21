package com.daylight.arcface_acs.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.daylight.arcface_acs.bean.Face;

import java.util.List;

@Dao
public interface FaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFaces(Face... faces);
    @Delete
    void deleteFaces(Face... faces);
    @Update
    void updateFaces(Face... faces);
    @Query("Select * From faces_table where account=:account order by id asc")
    LiveData<List<Face>> getAllFacesOrderById(String account);
    @Query("Select * From faces_table where account=:account limit 1")
    Face queryFace(String account);
    @Query("Select * From faces_table ")
    List<Face> getFaces();
}
