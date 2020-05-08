package com.android.myapplication.viewmodel.wan;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.myapplication.base.BaseViewModel;
import com.android.myapplication.bean.wanandroid.TreeBean;
import com.android.myapplication.http.HttpClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * on 2020/5/6
 */
public class TreeViewModel extends BaseViewModel {
    public TreeViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<TreeBean> getTree() {
        final MutableLiveData<TreeBean> data = new MutableLiveData<>();
        Disposable subscribe = HttpClient.Builder.getWanAndroidServer().getTree()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(treeBean -> {
                    data.setValue(treeBean);
                }, throwable -> {
                    data.setValue(null);
                });
        addDisposable(subscribe);
        return data;
    }
}
