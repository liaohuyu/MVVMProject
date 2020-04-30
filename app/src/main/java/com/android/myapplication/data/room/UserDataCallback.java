package com.android.myapplication.data.room;

/**
 * on 2020/4/20
 */
public interface UserDataCallback {
    //返回数据为null
    void onDataNotAvailable();

    //返回数据
    void getData(User bean);
}
