package com.android.myapplication.ui.wan.child;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.myapplication.R;
import com.android.myapplication.adapter.WanAndroidAdapter;
import com.android.myapplication.base.BaseFragment;
import com.android.myapplication.databinding.FragmentWanAndroidBinding;
import com.android.myapplication.viewmodel.wan.WanAndroidListViewModel;

/**
 * on 2020/4/29
 */
public class HomeFragment extends BaseFragment<WanAndroidListViewModel, FragmentWanAndroidBinding> {
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private WanAndroidAdapter mAdapter;
    private boolean isLoadBanner=false;
    private int width;//banner的宽


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public int setContent() {
        return R.layout.fragment_wan_android;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showContentView();
        initRefreshView();

        mIsPrepared=true;
        loadData();
    }

    private void initRefreshView() {
        //RefreshHelper
    }


}
