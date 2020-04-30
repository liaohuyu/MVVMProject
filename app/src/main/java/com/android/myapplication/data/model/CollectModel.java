package com.android.myapplication.data.model;

import com.android.myapplication.bean.wanandroid.HomeListBean;
import com.android.myapplication.http.HttpClient;
import com.android.myapplication.viewmodel.wan.WanNavigator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * on 2020/4/22
 */
public class CollectModel {
    //todo 这没和生命周期关联
    public void collect(int id, WanNavigator.OnCollectNavigator navigator) {
        HttpClient.Builder.getWanAndroidServer().collect(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeListBean>() {
                    @Override
                    public void accept(HomeListBean homeListBean) throws Exception {
                        if (homeListBean != null && homeListBean.getErrorCode() == 0)
                            navigator.onSuccess();
                        else
                            navigator.onFailure();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        navigator.onFailure();
                    }
                });
    }


    /**
     * @param isCollectList 是否是收藏列表
     * @param originId      如果是收藏列表的话就是原始文章的id，如果是站外文章就是-1
     * @param id            bean里的id
     */
    public void unCollect(boolean isCollectList, int id, int originId, WanNavigator.OnCollectNavigator navigator) {
        if (isCollectList) {
            unCollect(id, originId, navigator);
        } else {
            unCollect(id, navigator);
        }
    }

    /**
     * 收藏url
     */
    public void collectUrl(String name, String link, WanNavigator.OnCollectNavigator navigator) {
        HttpClient.Builder.getWanAndroidServer().collectUrl(name, link)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeListBean>() {
                    @Override
                    public void accept(HomeListBean bean) throws Exception {
                        if (bean != null && bean.getErrorCode() == 0) {
                            navigator.onSuccess();
                        } else {
                            navigator.onFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        navigator.onFailure();
                    }
                });
    }

    /**
     * 取消收藏
     */
    private void unCollect(int id, WanNavigator.OnCollectNavigator navigator) {
        HttpClient.Builder.getWanAndroidServer().unCollectOrigin(id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeListBean>() {
                    @Override
                    public void accept(HomeListBean homeListBean) throws Exception {
                        if (homeListBean != null && homeListBean.getErrorCode() == 0) {
                            navigator.onSuccess();
                        } else {
                            navigator.onFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 取消收藏，我的收藏页
     */
    private void unCollect(int id, int originId, WanNavigator.OnCollectNavigator navigator) {
        HttpClient.Builder.getWanAndroidServer().unCollect(id, originId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeListBean>() {
                    @Override
                    public void accept(HomeListBean bean) throws Exception {
                        if (bean != null && bean.getErrorCode() == 0) {
                            navigator.onSuccess();
                        } else {
                            navigator.onFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        navigator.onFailure();
                    }
                });
    }

}
