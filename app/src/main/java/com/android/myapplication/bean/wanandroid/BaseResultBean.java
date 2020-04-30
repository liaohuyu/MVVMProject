package com.android.myapplication.bean.wanandroid;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * on 2020/4/22
 */
public class BaseResultBean<T> extends BaseObservable {
    private T data;
    private int errCode;
    private String errorMsg;

    @Bindable
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Bindable
    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    @Bindable
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
