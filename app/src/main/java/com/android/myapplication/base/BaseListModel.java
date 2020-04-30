package com.android.myapplication.base;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * on 2020/4/29
 */
public class BaseListModel extends BaseViewModel {
    public BaseListModel(@NonNull Application application) {
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
