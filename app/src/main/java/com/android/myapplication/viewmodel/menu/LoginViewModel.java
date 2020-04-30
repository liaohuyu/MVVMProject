package com.android.myapplication.viewmodel.menu;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.myapplication.base.BaseViewModel;
import com.android.myapplication.bean.wanandroid.LoginBean;
import com.android.myapplication.data.UserUtil;
import com.android.myapplication.data.room.Injection;
import com.android.myapplication.http.HttpClient;
import com.android.myapplication.utils.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * on 2020/4/23
 */
public class LoginViewModel extends BaseViewModel {

    public final ObservableField<String> username = new ObservableField<>();
    // public final MutableLiveData<String> username = new MutableLiveData<>();
    public final ObservableField<String> password = new ObservableField<>();
    // public final MutableLiveData<String> password = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    * 调用 setValue 方法，Observer 的 onChanged 方法会在调用 serValue 方法的线程回调。而
    * postvalue 方法，Observer 的 onChanged 方法将会在主线程回调。
    * */
    public MutableLiveData<Boolean> register() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        if (!verifyData()) {
            data.setValue(false);
            return data;
        }
        HttpClient.Builder.getWanAndroidServer().register(username.get(), password.get(), password.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(LoginBean loginBean) {
                        if (loginBean != null && loginBean.getData() != null) {
                            Injection.get().addData(loginBean.getData());
                            UserUtil.handleLoginSuccess();
                            data.setValue(true);
                        } else {
                            if (loginBean != null)
                                ToastUtil.showToastLong(loginBean.getErrorMsg());
                            data.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        data.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return data;
    }

    public MutableLiveData<Boolean> login() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        if (!verifyData()) {
            data.setValue(false);
            return data;
        }
        HttpClient.Builder.getWanAndroidServer().login(username.get(), password.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(LoginBean bean) {
                        if (bean != null && bean.getData() != null) {
                            Injection.get().addData(bean.getData());
                            UserUtil.handleLoginSuccess();
                            data.setValue(true);
                        } else {
                            if (bean != null) {
                                ToastUtil.showToastLong(bean.getErrorMsg());
                            }
                            data.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        data.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return data;
    }

    private boolean verifyData() {
        if (TextUtils.isEmpty(username.get())) {
            ToastUtil.showToast("请输入用户名");
            return false;
        }
        if (TextUtils.isEmpty(password.get())) {
            ToastUtil.showToast("请输入密码");
            return false;
        }
        return true;
    }
}
