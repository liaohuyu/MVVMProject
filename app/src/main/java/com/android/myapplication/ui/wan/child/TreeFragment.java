package com.android.myapplication.ui.wan.child;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.myapplication.R;
import com.android.myapplication.adapter.TreeAdapter;
import com.android.myapplication.base.BaseFragment;
import com.android.myapplication.databinding.FragmentWanAndroidBinding;
import com.android.myapplication.databinding.HeaderItemTreeBinding;
import com.android.myapplication.http.rx.RxBus;
import com.android.myapplication.http.rx.RxCodeConstants;
import com.android.myapplication.utils.CommonUtils;
import com.android.myapplication.utils.DataUtil;
import com.android.myapplication.utils.ToastUtil;
import com.android.myapplication.viewmodel.wan.TreeViewModel;

import me.jingbin.library.decoration.SpacesItemDecoration;

/**
 * on 2020/5/6
 */
public class TreeFragment extends BaseFragment<TreeViewModel, FragmentWanAndroidBinding> {
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private TreeAdapter mTreeAdapter;

    @Override
    public int setContent() {
        return R.layout.fragment_wan_android;
    }

    public static TreeFragment newInstance() {
        return new TreeFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsPrepared = true;
        initRefreshView();
        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initRefreshView() {
        bindingView.srlWan.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
        bindingView.srlWan.setOnRefreshListener(() -> {
            bindingView.srlWan.postDelayed(this::getTree, 150);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseActivity());
        bindingView.xrvWan.setLayoutManager(layoutManager);
        mTreeAdapter = new TreeAdapter();
        bindingView.xrvWan.setAdapter(mTreeAdapter);
        HeaderItemTreeBinding oneBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header_item_tree, null, false);
        View root = oneBinding.getRoot();
        bindingView.xrvWan.addHeaderView(root);
        oneBinding.tvPosition.setOnClickListener(v -> {
            if (!mTreeAdapter.isSelect()) {
                oneBinding.tvPosition.setText("选择类别");
                mTreeAdapter.setSelect(true);
                mTreeAdapter.notifyDataSetChanged();
                bindingView.xrvWan.addItemDecoration(new SpacesItemDecoration(getBaseActivity()).setNoShowDivider(1, 0).setDrawable(R.drawable.shape_line));
            } else {
                oneBinding.tvPosition.setText("发现页内容定制");
                mTreeAdapter.setSelect(false);
                mTreeAdapter.notifyDataSetChanged();
                if (bindingView.xrvWan.getItemDecorationCount() > 0)
                    bindingView.xrvWan.removeItemDecorationAt(0);
            }
        });

        bindingView.xrvWan.setOnItemChildClickListener((view, position) -> {
            if (mTreeAdapter.isSelect()) {
                if (mTreeAdapter.getSelectedPosition() == position) {
                    ToastUtil.showToastLong("当前已经是\"" + mTreeAdapter.getData().get(position).getName() + "\"");
                    return;
                }
                oneBinding.tvPosition.setText("发现页内容订制");
                mTreeAdapter.setSelect(false);
                mTreeAdapter.notifyDataSetChanged();
                if (bindingView.xrvWan.getItemDecorationCount() > 0)
                    bindingView.xrvWan.removeItemDecorationAt(1);
                layoutManager.scrollToPositionWithOffset(position + bindingView.xrvWan.getCustomTopItemViewCount(), 0);//不管在不在屏幕都要弄到第一个
                RxBus.getDefault().post(RxCodeConstants.FIND_CUSTOM, position);
            }
        });

    }

    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return;
        }
        showLoading();
        bindingView.srlWan.postDelayed(this::getTree, 150);
        mIsFirst = false;
    }

    private void getTree() {
        viewModel.getTree().observe(this, treeBean -> {
            showContentView();
            if (bindingView.srlWan.isRefreshing())
                bindingView.srlWan.setRefreshing(false);
            if (treeBean != null && treeBean.getData() != null && treeBean.getData().size() > 0) {
                if (mTreeAdapter.getItemCount() == 0)
                    DataUtil.putTreeData(getBaseActivity(), treeBean);
                mTreeAdapter.setNewData(treeBean.getData());
                bindingView.xrvWan.loadMoreComplete();
            } else {
                if (mTreeAdapter.getData().size() == 0)
                    showError();
                else
                    bindingView.xrvWan.loadMoreComplete();
            }
        });
    }
}
