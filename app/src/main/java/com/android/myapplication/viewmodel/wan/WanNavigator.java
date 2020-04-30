package com.android.myapplication.viewmodel.wan;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * on 2020/4/22
 */
public interface WanNavigator {
    interface JokeModelNavigator {
        //void loadSuccess(List<DuzanZiBean> lists);

        void loadFailed();

        void addSubscription(Disposable disposable);
    }

    interface OnCollectNavigator {
        void onSuccess();

        void onFailure();
    }
}
