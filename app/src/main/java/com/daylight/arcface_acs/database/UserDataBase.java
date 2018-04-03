package com.daylight.arcface_acs.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.daylight.arcface_acs.bean.ChatMessage;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.bean.Recent;
import com.daylight.arcface_acs.bean.Record;
import com.daylight.arcface_acs.bean.User;

/**
 *
 * Created by Daylight on 2018/1/26.
 */
@Database(entities = {User.class, Face.class, Feature.class, Record.class, ChatMessage.class, Recent.class}, version = 5,exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract FaceDao faceDao();
    public abstract FeatureDao featureDao();
    public abstract RecordDao recordDao();
    public abstract ChatDao chatDao();
    public abstract RecentDao recentDao();
    private static UserDataBase INSTANCE;
    public static UserDataBase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDataBase.class, "user_database").addMigrations(MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE `recent_table` (\n" +
//                    "    `id`          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                    "    `account`      TEXT    REFERENCES `user_table` (`phoneNum`) ON DELETE NO ACTION\n" +
//                    "                                                         ON UPDATE NO ACTION,\n" +
//                    "    `neighbor`     TEXT    REFERENCES `user_table` (`phoneNum`) ON DELETE NO ACTION\n" +
//                    "                                                         ON UPDATE NO ACTION,\n" +
//                    "    `lastMsg`        TEXT,\n" +
//                    "    `time`        INTEGER,\n" +
//                    "    `uniqueId` TEXT\n" +
//                    ")");
//            database.execSQL("CREATE INDEX `index_recent_table_account` on `recent_table`(`account`)");
//            database.execSQL("CREATE INDEX `index_recent_table_neighbor` on `recent_table`(`neighbor`)");
//            database.execSQL("CREATE UNIQUE INDEX `index_recent_table_uniqueId` on `recent_table`(`uniqueId`)");
            database.execSQL("ALTER TABLE `record_table` ADD COLUMN `time` INTEGER");
        }
    };
}
