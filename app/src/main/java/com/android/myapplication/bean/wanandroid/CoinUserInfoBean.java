package com.android.myapplication.bean.wanandroid;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.myapplication.BR;

/**
 * on 2020/4/20
 */
public class CoinUserInfoBean extends BaseObservable {
    private int coinCount;
    private int rank;
    private int userId;
    private String username;

    @Bindable
    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
        notifyPropertyChanged(BR.coinCount);//BR 是编译阶段生成的一个类，功能与 R.java 类似
    }

    @Bindable
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
        notifyPropertyChanged(BR.rank);
    }

    @Bindable
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }
}
