package com.daylight.arcface_acs.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.daylight.arcface_acs.bean.Record;

import java.util.List;

/**
 *
 * Created by Daylight on 2018/3/18.
 */

@Dao
public interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Record... records);
    @Query("Select * from record_table where account=:account order by time desc")
    List<Record> getRecords(String account);
    @Query("Select * from record_table where account in " +
            "(select account from user_table where communityName=:communityName) order by time desc")
    List<Record> getCommunityRecords(String communityName);
}
