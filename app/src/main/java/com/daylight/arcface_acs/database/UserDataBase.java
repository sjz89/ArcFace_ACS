package com.daylight.arcface_acs.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.daylight.arcface_acs.bean.Building;
import com.daylight.arcface_acs.bean.Estate;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.bean.User;

/**
 *
 * Created by Daylight on 2018/1/26.
 */
@Database(entities = {User.class, Face.class, Feature.class}, version = 1,exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract FaceDao faceDao();
    public abstract FeatureDao featureDao();
    private static UserDataBase INSTANCE;
    static UserDataBase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDataBase.class, "user_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
