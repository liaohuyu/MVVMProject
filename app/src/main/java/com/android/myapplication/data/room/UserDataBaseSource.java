package com.android.myapplication.data.room;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.myapplication.utils.DebugUtil;

/**
 * on 2020/4/20
 */
public class UserDataBaseSource {
    private static volatile UserDataBaseSource INSTANCE;
    private UserDao mUserDao;
    private AppExecutors mAppExecutors;

    public UserDataBaseSource(@NonNull UserDao mUserDao, @NonNull AppExecutors mAppExecutors) {
        this.mUserDao = mUserDao;
        this.mAppExecutors = mAppExecutors;
    }


    public static UserDataBaseSource getInstance(@NonNull AppExecutors mAppExecutors, @NonNull UserDao taskDao) {
        if (INSTANCE == null) {
            synchronized (UserDataBaseSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserDataBaseSource(taskDao, mAppExecutors);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 查找任何的bean(没有数据时会报错！)：
     * 如果数据库里有一条数据就返回这条数据
     * 如果有多条信息，则返回第一条数据
     */
    public void getSingleBean(UserDataCallback callback) {
        Runnable runnable = () -> {
            try {
                User user = mUserDao.findSingleBean();
                mAppExecutors.mainThread().execute(() -> {
                    if (user == null)
                        callback.onDataNotAvailable();
                    else
                        callback.getData(user);
                });
            } catch (Exception e) {
                callback.onDataNotAvailable();
                DebugUtil.error(e.getMessage());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * 先删除后再添加: 重新登录时
     */
    public void addData(@NonNull User user) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    int success = mUserDao.deleteAll();
//                    DebugUtil.error("----success:" + success);
                    mUserDao.addUser(user);
                } catch (Exception e) {
                    DebugUtil.error(e.getMessage());
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * 清除数据库
     */
    public void deleteAllData() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mUserDao.deleteAll();
                } catch (Exception e) {
                    DebugUtil.error(e.getMessage());
                }
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }
}
