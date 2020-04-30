package com.android.myapplication.data.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

/**
 * 注意：1、建值如果为 String id; 使用getWid(),则会报错！！！
 * * 2、有一个key就要有对应的get和set
 * * 3、List<>不能简单使用，需要处理
 */
@Entity
public class User {
    /**
     * 通过主键来标识删除的
     * 主键递增：PrimaryKey(autoGenerate = true)
     */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "icon")
    private String icon;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "coinCount")
    private int coinCount;
    @ColumnInfo(name = "rank")
    private int rank;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User(int id, String email, String icon, String password, int type, String username, int coinCount, int rank) {
        this.id = id;
        this.email = email;
        this.icon = icon;
        this.password = password;
        this.type = type;
        this.username = username;
        this.coinCount = coinCount;
        this.rank = rank;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", username='" + username + '\'' +
                ", coinCount=" + coinCount +
                ", rank=" + rank +
                '}';
    }
}
