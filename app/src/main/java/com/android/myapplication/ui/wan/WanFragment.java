package com.android.myapplication.ui.wan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.android.myapplication.R;
import com.android.myapplication.base.BaseFragment;
import com.android.myapplication.databinding.FragmentContentBinding;
import com.android.myapplication.ui.wan.child.HomeFragment;
import com.android.myapplication.view.MyFragmentPagerAdapter;
import com.android.myapplication.viewmodel.menu.NoViewModel;

import java.util.ArrayList;

/**
 * on 2020/4/24
 */
public class WanFragment extends BaseFragment<NoViewModel, FragmentContentBinding> {
    private ArrayList<String> mTitleList = new ArrayList<>(4);
    private ArrayList<Fragment> mFragments = new ArrayList<>(4);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoading();
        initFragmentList();
        /**
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         */
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mFragments, mTitleList);
        bindingView.vpGank.setOffscreenPageLimit(3);
        bindingView.vpGank.setAdapter(myFragmentPagerAdapter);
        //myFragmentPagerAdapter.notifyDataSetChanged();
        bindingView.tabGank.setupWithViewPager(bindingView.vpGank);
        showContentView();
    }


    private void initFragmentList() {
        mTitleList.clear();
        mTitleList.add("玩安卓");
        mTitleList.add("发现");
        mTitleList.add("知识体系");
        mTitleList.add("导航");
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(HomeFragment.newInstance());
      /*  mFragments.add(HomeFragment.newInstance());
        mFragments.add(WanFindFragment.newInstance());
//        mFragments.add(KnowledgeTreeFragment.newInstance());
        mFragments.add(TreeFragment.newInstance());
        mFragments.add(NavigationFragment.newInstance());*/
    }

    @Override
    public int setContent() {
        return R.layout.fragment_content;
    }
}
