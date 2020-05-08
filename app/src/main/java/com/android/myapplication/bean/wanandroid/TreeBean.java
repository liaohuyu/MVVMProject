package com.android.myapplication.bean.wanandroid;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.myapplication.BR;

import java.io.Serializable;
import java.util.List;

/**
 * @author jingbin
 * @data 2018/9/15
 * @description
 */

public class TreeBean extends BaseObservable implements Serializable {

    private static final long serialVersionUID = 1L;
    private int errorCode;
    private String errorMsg;
    private List<TreeItemBean> data;

    @Bindable
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        notifyPropertyChanged(BR.errorCode);
    }

    @Bindable
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        notifyPropertyChanged(BR.errorMsg);
    }

    @Bindable
    public List<TreeItemBean> getData() {
        return data;
    }

    public void setData(List<TreeItemBean> data) {
        this.data = data;
        notifyPropertyChanged(BR.data);
    }

}
