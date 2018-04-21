package com.daylight.arcface_acs.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.daylight.arcface_acs.bean.User;

import java.util.List;

/**
 *
 * Created by Daylight on 2018/1/26.
 */

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(User... users);
    @Query("Delete from user_table")
    void deleteAll();
    @Delete
    void deleteUsers(User... users);
    @Update
    void updateUsers(User... users);
    @Query("Select * from user_table where communityName=:community AND buildingName=:building And name!=:name order by doorNum desc")
    List<User> getNeighbors(String community,String building,String name);
    @Query("Select * from user_table where phoneNum=:account limit 1")
    LiveData<User> getUser(String account);
    @Query("Select * from user_table where phoneNum=:account limit 1")
    User loadUser(String account);
    @Query("Select phoneNum from user_table")
    LiveData<List<String>> getAccounts();
    @Query("Select * from user_table where communityName=:community And isManager=0")
    LiveData<List<User>> getUsers(String community);
}
