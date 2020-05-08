package com.android.myapplication.base;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * on 2020/4/29
 */
public class BaseListViewModel extends BaseViewModel {
    public BaseListViewModel(@NonNull Application application) {
        super(application);
    }

    public int mPage = 0;

    public int getPage() {
        return mPage;
    }

    public void setPage(int mPage) {
        this.mPage = mPage;
    }
}
