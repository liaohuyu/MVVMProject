package com.android.myapplication.ui.wan.child;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.myapplication.R;
import com.android.myapplication.adapter.WanAndroidAdapter;
import com.android.myapplication.base.BaseFragment;
import com.android.myapplication.bean.wanandroid.HomeListBean;
import com.android.myapplication.bean.wanandroid.WanAndroidBannerBean;
import com.android.myapplication.databinding.FragmentWanAndroidBinding;
import com.android.myapplication.databinding.HeaderWanAndroidBinding;
import com.android.myapplication.utils.CommonUtils;
import com.android.myapplication.utils.DensityUtil;
import com.android.myapplication.utils.GlideUtil;
import com.android.myapplication.utils.RefreshHelper;
import com.android.myapplication.view.webview.WebViewActivity;
import com.android.myapplication.viewmodel.wan.WanAndroidListViewViewModel;

import java.util.List;

import me.jingbin.sbanner.config.ScaleRightTransformer;
import me.jingbin.sbanner.holder.BannerViewHolder;

/**
 * on 2020/4/29
 */
public class HomeFragment extends BaseFragment<WanAndroidListViewViewModel, FragmentWanAndroidBinding> {
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private WanAndroidAdapter mAdapter;
    private boolean isLoadBanner = false;
    private int width;//banner的宽
    private HeaderWanAndroidBinding headerBinding;


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

        mIsPrepared = true;
        loadData();
    }

    private void initRefreshView() {
        RefreshHelper.initLinear(bindingView.xrvWan, true);
        headerBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header_wan_android, null, false);
        bindingView.srlWan.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
        mAdapter = new WanAndroidAdapter(getBaseActivity());
        bindingView.xrvWan.setAdapter(mAdapter);
        mAdapter.setNoImage(true);
        bindingView.xrvWan.addHeaderView(headerBinding.getRoot());
        width = DensityUtil.getDisplayWidth() - DensityUtil.dip2px(bindingView.xrvWan.getContext(), 150);
        float height = width / 1.8f;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        headerBinding.banner.setLayoutParams(lp);
        headerBinding.radioGroup.setVisibility(View.INVISIBLE);
        headerBinding.rb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            refresh(isChecked, true);
        });
        headerBinding.rb2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            refresh(isChecked, false);
        });
        bindingView.srlWan.setOnRefreshListener(this::swipeRefresh);
        bindingView.xrvWan.setOnLoadMoreListener(() -> {
            if (!bindingView.srlWan.isRefreshing()) {
                int page = viewModel.getPage();
                viewModel.setPage(++page);
                if (headerBinding.rb1.isChecked())
                    getHomeArticleList();
                else
                    getHomeProjectList();
            } else
                bindingView.xrvWan.loadMoreComplete();
        },300);
    }

    private void getHomeProjectList() {
        viewModel.getHomeProjectList().observe(this, homeObserver);
    }


    private void getHomeArticleList() {
        viewModel.getHomeArticleList(null).observe(this, homeObserver);
    }

    private Observer<HomeListBean> homeObserver = new Observer<HomeListBean>() {
        @Override
        public void onChanged(@Nullable HomeListBean bean) {
            if (bindingView.srlWan.isRefreshing())
                bindingView.srlWan.setRefreshing(false);
            if (bean != null && bean.getData() != null
                    && bean.getData().getDatas() != null
                    && bean.getData().getDatas().size() > 0) {
                if (viewModel.getPage() == 0) {
                    showContentView();
                    mAdapter.setNewData(bean.getData().getDatas());
                } else {
                    mAdapter.addData(bean.getData().getDatas());
                    bindingView.xrvWan.loadMoreComplete();
                }
            } else {
                if (mAdapter.getItemCount() == 0)
                    showError();
                else
                    bindingView.xrvWan.loadMoreEnd();//todo loadMoreFail 是不是更合适
            }
        }
    };

    private void showBannerView(List<WanAndroidBannerBean.DataBean> data) {
        if (!isLoadBanner) {
            headerBinding.banner
                    .setIndicatorRes(R.drawable.banner_red, R.drawable.banner_grey)
                    .setBannerAnimation(ScaleRightTransformer.class)
                    .setDelayTime(5000)
                    .setPages(data, CustomViewHolder::new)
                    .start();
            headerBinding.banner.startAutoPlay();
            isLoadBanner = true;
        } else
            headerBinding.banner.update(data);
    }

    class CustomViewHolder implements BannerViewHolder<WanAndroidBannerBean.DataBean> {

        private ImageView imageView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner_wanandroid, null);
            imageView = view.findViewById(R.id.iv_banner);
            return view;
        }

        @Override
        public void onBind(Context context, int position, WanAndroidBannerBean.DataBean data) {
            if (data != null) {
                DensityUtil.setWidthHeight(imageView, width, 1.8f);
                GlideUtil.displayEspImage(data.getImagePath(), imageView, 3);
                imageView.setOnClickListener(v -> {
                    WebViewActivity.loadUrl(getContext(), data.getUrl(), data.getTitle());
                });
            }
        }
    }

    //下拉刷新
    private void swipeRefresh() {
        bindingView.srlWan.postDelayed(() -> {
            viewModel.setPage(0);
            getWanAndroidBanner();
        }, 350);
    }

    //todo 请求失败就消失？
    private void getWanAndroidBanner() {
        viewModel.getWanAndroidBanner().observe(this, bean -> {
            if (bean != null) {
                headerBinding.rlBanner.setVisibility(View.VISIBLE);
                showBannerView(bean.getData());
            } else
                headerBinding.rlBanner.setVisibility(View.GONE);
            headerBinding.radioGroup.setVisibility(View.VISIBLE);
            if (headerBinding.rb1.isChecked())
                getHomeArticleList();
            else
                getHomeProjectList();
        });
    }

    private void refresh(boolean isChecked, boolean isArticle) {
        if (isChecked) {
            bindingView.srlWan.setRefreshing(true);
            viewModel.setPage(0);
            mAdapter.setNoImage(isArticle);
            if (isArticle)
                getHomeArticleList();
            else
                getHomeProjectList();
        }
    }

    @Override
    protected void loadData() {
        if (mIsPrepared && isLoadBanner) {
            onResume();
        }
        if (!mIsPrepared || !mIsVisible || !mIsFirst)
            return;
        bindingView.srlWan.setRefreshing(true);
        bindingView.srlWan.postDelayed(this::getWanAndroidBanner, 500);
        mIsFirst = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoadBanner)
            headerBinding.banner.startAutoPlay();
    }

    @Override
    protected void onInvisible() {
        if (mIsPrepared && isLoadBanner) {
            onPause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 不可见时轮播图停止滚动
        if (isLoadBanner) {
            headerBinding.banner.stopAutoPlay();
        }
    }

    //错误点击刷新
    @Override
    protected void onRefresh() {
        bindingView.srlWan.setRefreshing(true);
        getWanAndroidBanner();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isLoadBanner) {
            headerBinding.banner.stopAutoPlay();
            headerBinding.banner.releaseBanner();
            isLoadBanner = false;
        }
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter = null;
        }
    }
}
