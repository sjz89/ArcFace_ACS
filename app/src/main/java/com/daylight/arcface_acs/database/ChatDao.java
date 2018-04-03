package com.daylight.arcface_acs.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.daylight.arcface_acs.bean.ChatMessage;

import java.util.List;

/**
 *
 * Created by Daylight on 2018/3/21.
 */

@Dao
public interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatMessage... messages);
    @Query("Select * from chat_table where rightId=:rightId AND leftId=:leftId")
    LiveData<List<ChatMessage>> queryMessages(String rightId, String leftId);
}
