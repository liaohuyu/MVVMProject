package com.android.myapplication.base.binding;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import me.jingbin.library.adapter.BaseByRecyclerViewAdapter;

/**
 * on 2020/4/29
 */
public abstract class BaseBindingAdapter<T, B extends ViewDataBinding> extends BaseByRecyclerViewAdapter<T, BaseBindingHolder> {

    private int mLayoutId;

    public BaseBindingAdapter(@LayoutRes int mLayoutId) {
        this.mLayoutId = mLayoutId;
    }

    public BaseBindingAdapter(List<T> data, @LayoutRes int mLayoutId) {
        super(data);
        this.mLayoutId = mLayoutId;
    }

    @NonNull
    @Override
    public BaseBindingHolder<T, B> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, i);
    }

    private class ViewHolder extends BaseBindingHolder<T, B> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        protected void onBindingView(BaseBindingHolder holder, T bean, int position) {
            bindView(holder, bean, binding, position);
        }
    }

    protected abstract void bindView(BaseBindingHolder holder, T bean, B binding, int position);
}
