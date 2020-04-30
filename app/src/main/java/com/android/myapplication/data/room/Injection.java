package com.android.myapplication.data.room;

public class Injection {
    public static UserDataBaseSource get() {
        UserDataBase database = UserDataBase.getDatabase();
        return UserDataBaseSource.getInstance(new AppExecutors(), database.waitDao());
    }
}
