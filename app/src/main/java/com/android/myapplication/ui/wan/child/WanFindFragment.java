package com.android.myapplication.ui.wan.child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.android.myapplication.R;
import com.android.myapplication.adapter.WanAndroidAdapter;
import com.android.myapplication.adapter.WxArticleAdapter;
import com.android.myapplication.app.Constants;
import com.android.myapplication.base.BaseFragment;
import com.android.myapplication.bean.wanandroid.TreeBean;
import com.android.myapplication.databinding.FragmentWanFindBinding;
import com.android.myapplication.http.rx.RxBus;
import com.android.myapplication.http.rx.RxCodeConstants;
import com.android.myapplication.utils.DataUtil;
import com.android.myapplication.utils.RefreshHelper;
import com.android.myapplication.utils.SPUtils;
import com.android.myapplication.utils.ToastUtil;
import com.android.myapplication.viewmodel.wan.WanFindViewModel;

import io.reactivex.disposables.Disposable;
import me.jingbin.library.ByRecyclerView;

/**
 * on 2020/5/6
 * 发现订阅
 */
public class WanFindFragment extends BaseFragment<WanFindViewModel, FragmentWanFindBinding> {

    private WxArticleAdapter wxArticleAdapter;
    private WanAndroidAdapter mContentAdapter;
    private int wxArticleId;
    private int currentPosition;
    private boolean isAddFooter;
    private boolean mIsPrepared;
    private boolean mIsFirst = true;

    @Override
    public int setContent() {
        return R.layout.fragment_wan_find;
    }

    public static WanFindFragment newInstance() {
        return new WanFindFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRefreshView();
        initRxBus();
        mIsPrepared = true;
        loadData();
    }


    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst)
            return;

        showLoading();
        bindingView.rvWxarticle.postDelayed(this::getData, 150);
        mIsFirst = false;
    }

    private void getData() {
        int anInt = SPUtils.getInt(Constants.FIND_POSITION, -1);
        if (anInt == -1)
            viewModel.getWxArticle();
        else if (!viewModel.handleCustomData(DataUtil.getTreeData(getBaseActivity()), anInt)) {
            viewModel.getWxArticle();
        }
    }

    private void initRefreshView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseActivity());
        bindingView.rvWxarticle.setLayoutManager(layoutManager);
        wxArticleAdapter = new WxArticleAdapter();
        bindingView.rvWxarticle.setAdapter(wxArticleAdapter);

        RefreshHelper.initLinear(bindingView.recyclerView, true, 1);

        mContentAdapter = new WanAndroidAdapter(getBaseActivity());
        mContentAdapter.setNoShowChapterName();
        bindingView.recyclerView.setAdapter(mContentAdapter);

        wxArticleAdapter.setOnSelectListener(this::selectItem);
        bindingView.recyclerView.setOnLoadMoreListener(() -> {
            int page = viewModel.getPage();
            viewModel.setPage(++page);
            viewModel.getWxArticleDetail(wxArticleId);
        });
        bindingView.recyclerView.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.setPage(1);
                viewModel.getWxArticleDetail(wxArticleId);
            }
        }, 300);

        onObserveViewModel();
    }

    @Override
    protected void onRefresh() {
        viewModel.getWxArticle();
    }

    private void onObserveViewModel() {
        viewModel.getDataTitle().observe(this, wxArticleItemBeans -> {
            if (wxArticleItemBeans != null && wxArticleItemBeans.size() > 0) {
                wxArticleAdapter.setNewData(wxArticleItemBeans);
                selectItem(0);
            } else
                showError();
        });
        viewModel.getData().observe(this, list -> {
            showContentView();
            if (list != null && list.size() > 0) {
                if (viewModel.getPage() == 1) {
                    bindingView.recyclerView.setLoadMoreEnabled(true);
                    bindingView.recyclerView.setRefreshEnabled(true);
                    bindingView.recyclerView.setFootViewEnabled(false);
                    mContentAdapter.setNewData(list);
                    bindingView.recyclerView.getLayoutManager().scrollToPosition(0);
                } else {
                    mContentAdapter.addData(list);
                    bindingView.recyclerView.loadMoreComplete();
                }
            } else {
                if (viewModel.getPage() == 1) {
                    bindingView.recyclerView.setLoadMoreEnabled(false);
                    bindingView.recyclerView.setRefreshEnabled(false);
                    if (!isAddFooter) {
                        bindingView.recyclerView.addFooterView(R.layout.layout_loading_empty);
                        isAddFooter = true;
                    } else
                        bindingView.recyclerView.setFootViewEnabled(true);
                    mContentAdapter.setNewData(null);
                } else
                    bindingView.recyclerView.loadMoreEnd();
            }
        });
    }

    private void selectItem(int position) {
        if (position < wxArticleAdapter.getData().size()) {
            wxArticleId = wxArticleAdapter.getData().get(position).getId();
            wxArticleAdapter.setId(wxArticleId);
            viewModel.setPage(1);
            viewModel.getWxArticleDetail(wxArticleId);
            bindingView.recyclerView.setRefreshEnabled(true);
        }
        wxArticleAdapter.notifyItemChanged(currentPosition);
        currentPosition = position;
    }

    private void initRxBus() {
        Disposable subscribe = RxBus.getDefault().toObservable(RxCodeConstants.FIND_CUSTOM, Integer.class)
                .subscribe(integer -> {
                    if (integer != null) {
                        if (!mIsFirst) {
                            TreeBean treeData = DataUtil.getTreeData(getBaseActivity());
                            if (viewModel.handleCustomData(treeData, integer))
                                ToastUtil.showToastLong("发现页内容已改为\"" + treeData.getData().get(integer).getName() + "\"");
                        } else
                            ToastUtil.showToastLong("发现页内容已更改，请打开查看~");
                    }
                });
        addSubscription(subscribe);
    }
}
