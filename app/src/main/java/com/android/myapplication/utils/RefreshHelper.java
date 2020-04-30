package com.android.myapplication.utils;

import android.support.v7.widget.StaggeredGridLayoutManager;

import com.android.myapplication.view.byview.NeteaseRefreshHeaderView;

import me.jingbin.library.ByRecyclerView;
import me.jingbin.library.decoration.GridSpaceItemDecoration;

/**
 * on 2020/4/29
 */
public class RefreshHelper {
    public static void initStaggeredGrid(ByRecyclerView recyclerView,int spanCunt,int spacing){
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCunt,StaggeredGridLayoutManager.VERTICAL));
        // 如果每个item高度一致设置后效率更高
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(spanCunt,spacing));
        recyclerView.setRefreshHeaderView(new NeteaseRefreshHeaderView(recyclerView.getContext()));
    }
}
