package com.android.myapplication.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.myapplication.R;
import com.android.myapplication.app.Constants;
import com.android.myapplication.bean.wanandroid.TreeItemBean;
import com.android.myapplication.bean.wanandroid.WxarticleItemBean;
import com.android.myapplication.ui.wan.child.CategoryDetailActivity;
import com.android.myapplication.utils.CommonUtils;
import com.android.myapplication.utils.DataUtil;
import com.android.myapplication.utils.SPUtils;
import com.android.myapplication.view.ThinBoldSpan;
import com.google.android.flexbox.FlexboxLayout;

import java.util.LinkedList;
import java.util.Queue;

import me.jingbin.library.adapter.BaseByViewHolder;
import me.jingbin.library.adapter.BaseRecyclerAdapter;

/**
 * on 2020/5/6
 */
public class TreeAdapter extends BaseRecyclerAdapter<TreeItemBean> {
    private boolean isSelect = false;
    private int selectedPosition = -1;
    private Queue<TextView> mFlexItemTextViewCaches = new LinkedList<>();
    private LayoutInflater mInflater;

    public TreeAdapter() {
        super(R.layout.item_tree);
    }

    @Override
    protected void bindView(BaseByViewHolder<TreeItemBean> holder, TreeItemBean bean, int position) {
        if (bean != null) {
            TextView tvTreeTitle = holder.getView(R.id.tv_tree_title);
            FlexboxLayout flTree = holder.getView(R.id.fl_tree);
            String name = DataUtil.getHtmlString(bean.getName());
            if (isSelect) {
                flTree.setVisibility(View.GONE);
                if (selectedPosition == position) {
                    name = name + "     ★★★";
                    tvTreeTitle.setTextColor(CommonUtils.getColor(R.color.colorTheme));
                } else
                    tvTreeTitle.setTextColor(CommonUtils.getColor(R.color.colorContent));
            } else {
                tvTreeTitle.setTextColor(CommonUtils.getColor(R.color.colorContent));
                flTree.setVisibility(View.VISIBLE);
                for (int i = 0; i < bean.getChildren().size(); i++) {
                    WxarticleItemBean childBean = bean.getChildren().get(i);
                    TextView child = createOrGetCacheFlexItemTextView(flTree);
                    child.setText(DataUtil.getHtmlString(childBean.getName()));
                    child.setOnClickListener(v -> CategoryDetailActivity.start(v.getContext(), childBean.getId(), bean));
                    flTree.addView(child);
                }
            }
            tvTreeTitle.setText(ThinBoldSpan.getDefaultSpanString(tvTreeTitle.getContext(), name));
        }
    }

    private TextView createOrGetCacheFlexItemTextView(FlexboxLayout fbl) {
        TextView view = mFlexItemTextViewCaches.poll();
        if (view != null)
            return view;
        if (mInflater == null) {
            mInflater = LayoutInflater.from(fbl.getContext());
        }
        return (TextView) mInflater.inflate(R.layout.layout_tree_tag, fbl, false);
    }

    /**
     * 复用需要有相同的BaseByViewHolder，且HeaderView部分获取不到FlexboxLayout，需要容错
     * <p>
     * 头布局无法转换  todo  有没有更好的方式来解决这个问题
     */
    @Override
    public void onViewRecycled(@NonNull BaseByViewHolder<TreeItemBean> holder) {
        super.onViewRecycled(holder);
        FlexboxLayout fbl = (FlexboxLayout) holder.getView(R.id.fl_tree);
        if (fbl != null) {
            for (int i = 0; i < fbl.getChildCount(); i++) {
                mFlexItemTextViewCaches.offer((TextView) fbl.getChildAt(i));//添加元素
            }
            fbl.removeAllViews();
        }
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
        if (isSelect) {
            selectedPosition = SPUtils.getInt(Constants.FIND_POSITION, -1);
        }
    }

    public boolean isSelect() {
        return isSelect;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
