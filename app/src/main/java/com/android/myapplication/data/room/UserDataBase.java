package com.android.myapplication.data.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.android.myapplication.app.App;

/**
 * on 2020/4/17
 */
//exportSchema https://www.jianshu.com/p/190b6e5e4592
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {

    private static UserDataBase sInstance;

    public abstract UserDao waitDao();

    /**
     * 版本号迁移：
     * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2017/0728/8278.html
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static UserDataBase getDatabase() {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(App.getInstance(), UserDataBase.class, "User.db").build();
            //addMigrations(MIGRATION_1_2,MIGRATION_2_3) fallbackToDestructiveMigration 所有表都会被丢弃
        }
        return sInstance;
    }

    public static void onDestroy() {
        sInstance = null;
    }
}
