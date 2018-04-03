package com.daylight.arcface_acs.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.daylight.arcface_acs.bean.Recent;

import java.util.List;

/**
 *
 * Created by Daylight on 2018/3/21.
 */

@Dao
public interface RecentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recent... recents);
    @Query("Select * from recent_table where account=:account order by time desc")
    LiveData<List<Recent>> getRecentList(String account);
}
