package com.android.myapplication.data;

import android.content.Context;

import com.android.myapplication.app.Constants;
import com.android.myapplication.data.impl.OnUserInfoListener;
import com.android.myapplication.data.room.Injection;
import com.android.myapplication.data.room.User;
import com.android.myapplication.data.room.UserDataCallback;
import com.android.myapplication.ui.wan.child.LoginActivity;
import com.android.myapplication.utils.SPUtils;
import com.android.myapplication.utils.ToastUtil;

/**
 * 用户登录处理
 */
public class UserUtil {
    public static void getUserInfo(OnUserInfoListener listener) {
        Injection.get().getSingleBean(new UserDataCallback() {
            @Override
            public void onDataNotAvailable() {
                if (listener != null)
                    listener.onSuccess(null);
            }

            @Override
            public void getData(User bean) {
                if (listener != null)
                    listener.onSuccess(bean);
            }
        });
    }

    /**
     * 更新用户信息
     */
    public static void setUserInfo(User bean) {
        Injection.get().addData(bean);
    }

    public static void handleLoginSuccess() {
        SPUtils.putBoolean(Constants.IS_LOGIN, true);
    }

    public static void handleLoginFailure() {
        SPUtils.putBoolean(Constants.IS_LOGIN, false);
        SPUtils.putString("cookie", "");
        SPUtils.remove("cookie");
    }

    /**
     * 是否登录，没有进入登录页面
     */
    public static boolean isLogin(Context context) {
        boolean isLogin = SPUtils.getBoolean(Constants.IS_LOGIN, false);
        if (!isLogin) {
            ToastUtil.showToastLong("请先登录~");
            LoginActivity.start(context);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return SPUtils.getBoolean(Constants.IS_LOGIN, false);
    }

    public static String getLevel(int coinCount) {
        return String.valueOf(coinCount / 100 + 1);
    }
}
