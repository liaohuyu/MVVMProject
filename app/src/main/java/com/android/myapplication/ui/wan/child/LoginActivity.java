package com.android.myapplication.ui.wan.child;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.myapplication.R;
import com.android.myapplication.base.BaseActivity;
import com.android.myapplication.databinding.ActivityLoginBinding;
import com.android.myapplication.http.rx.RxBus;
import com.android.myapplication.http.rx.RxCodeConstants;
import com.android.myapplication.viewmodel.menu.LoginViewModel;

public class LoginActivity extends BaseActivity<LoginViewModel, ActivityLoginBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("玩安卓登录");
        showContentView();
        bindingView.setLifecycleOwner(this);//这样写就能让viewModel的参数变成liveData
        bindingView.setViewModel(viewModel);
    }

    public void register(View view) {
        viewModel.register().observe(this, this::loadSuccess);
        /*new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                loadSuccess(aBoolean);
            }
        }*/
    }

    public void login(View view) {
        viewModel.login().observe(this, this::loadSuccess);
    }

    /**
     * 注册或登录成功
     */
    public void loadSuccess(Boolean aBoolean) {
        if (aBoolean != null && aBoolean) {
            RxBus.getDefault().post(RxCodeConstants.LOGIN, true);
            finish();
        }
    }

    public static void start(Context mContext) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }


}
