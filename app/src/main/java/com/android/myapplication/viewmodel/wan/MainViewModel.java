package com.android.myapplication.viewmodel.wan;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.android.myapplication.base.BaseViewModel;
import com.android.myapplication.bean.wanandroid.BaseResultBean;
import com.android.myapplication.bean.wanandroid.CoinUserInfoBean;
import com.android.myapplication.data.UserUtil;
import com.android.myapplication.http.HttpClient;
import com.android.myapplication.utils.DataUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends BaseViewModel {
    //问题反馈是否已读
    public final ObservableField<Boolean> isReadOk = new ObservableField<>();
    //赞赏入口是否开发
    public final ObservableField<Boolean> isShowAdmire = new ObservableField<>();

    private final MutableLiveData<CoinUserInfoBean> coin = new MutableLiveData<>();//是可以在xml直接用的

    public MainViewModel(@NonNull Application application) {
        super(application);
        isShowAdmire.set(DataUtil.isShowAdmire());
    }

    public void getUserInfo() {
        UserUtil.getUserInfo(user -> {
            if (user != null) {
                execute(HttpClient.Builder.getWanAndroidServer().getCoinUserInfo(), new Observer<BaseResultBean<CoinUserInfoBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(BaseResultBean<CoinUserInfoBean> coinUserInfoBeanBaseResultBean) {
                        if (coinUserInfoBeanBaseResultBean != null && coinUserInfoBeanBaseResultBean.getData() != null) {
                            CoinUserInfoBean infoBean = coinUserInfoBeanBaseResultBean.getData();
                            infoBean.setUsername(user.getUsername());
                            coin.setValue(infoBean);
                            UserUtil.getUserInfo(user1 -> {
                                if (user1 != null) {
                                    user1.setCoinCount(infoBean.getCoinCount());
                                    user1.setRank(infoBean.getRank());
                                    UserUtil.setUserInfo(user1);
                                }
                            });
                        } else
                            coin.setValue(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        coin.setValue(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                coin.setValue(null);
            }
        });
    }

    public MutableLiveData<CoinUserInfoBean> getCoin() {
        return coin;
    }
}
