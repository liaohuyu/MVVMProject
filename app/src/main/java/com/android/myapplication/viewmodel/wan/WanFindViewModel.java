package com.android.myapplication.viewmodel.wan;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.myapplication.app.Constants;
import com.android.myapplication.base.BaseListViewModel;
import com.android.myapplication.bean.wanandroid.ArticlesBean;
import com.android.myapplication.bean.wanandroid.TreeBean;
import com.android.myapplication.bean.wanandroid.WxarticleItemBean;
import com.android.myapplication.http.HttpClient;
import com.android.myapplication.utils.SPUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * on 2020/5/6
 */
public class WanFindViewModel extends BaseListViewModel {
    //content数据
    private final MutableLiveData<List<ArticlesBean>> data = new MutableLiveData<>();
    //title数据
    private final MutableLiveData<List<WxarticleItemBean>> dataTitle = new MutableLiveData<>();

    public MutableLiveData<List<ArticlesBean>> getData() {
        return data;
    }

    public MutableLiveData<List<WxarticleItemBean>> getDataTitle() {
        return dataTitle;
    }

    public WanFindViewModel(@NonNull Application application) {
        super(application);
        mPage = 1;
    }

    public void getWxArticle() {
        Disposable subscribe = HttpClient.Builder.getWanAndroidServer().getWxArticle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBaseResultBean -> {
                    if (listBaseResultBean != null && listBaseResultBean.getData() != null && listBaseResultBean.getData().size() > 0) {
                        dataTitle.setValue(listBaseResultBean.getData());
                    } else
                        dataTitle.setValue(null);
                }, throwable -> {
                    dataTitle.setValue(null);
                });
        addDisposable(subscribe);
    }

    public void getWxArticleDetail(int id) {
        Disposable subscribe = HttpClient.Builder.getWanAndroidServer().getWxArticleDetail(id, mPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bean -> {
                    if (bean != null && bean.getData() != null && bean.getData().getDatas() != null && bean.getData().getDatas().size() > 0)
                        data.setValue(bean.getData().getDatas());
                    else
                        data.setValue(null);
                }, throwable -> {
                    data.setValue(null);
                });
        addDisposable(subscribe);
    }

    /**
     * 定制页内容？
     */
    public boolean handleCustomData(TreeBean treeBean, int position) {
        if (treeBean != null && treeBean.getData() != null && treeBean.getData().size() > 0) {
            SPUtils.putInt(Constants.FIND_POSITION, position);
            mPage = 1;
            dataTitle.setValue(treeBean.getData().get(position).getChildren());
            return true;
        } else {
            SPUtils.remove(Constants.FIND_POSITION);
            return false;
        }
    }
}
