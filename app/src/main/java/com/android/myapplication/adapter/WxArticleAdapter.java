package com.android.myapplication.adapter;

import com.android.myapplication.R;
import com.android.myapplication.base.binding.BaseBindingAdapter;
import com.android.myapplication.base.binding.BaseBindingHolder;
import com.android.myapplication.bean.wanandroid.WxarticleItemBean;
import com.android.myapplication.databinding.ItemWxarticleBinding;
import com.android.myapplication.utils.CommonUtils;

/**
 * on 2020/5/6
 */
public class WxArticleAdapter extends BaseBindingAdapter<WxarticleItemBean, ItemWxarticleBinding> {
    private int id;
    private int selectPosition = 0;
    private int lastPosition = 0;

    public WxArticleAdapter() {
        super(R.layout.item_wxarticle);
    }

    @Override
    protected void bindView(BaseBindingHolder holder, WxarticleItemBean bean, ItemWxarticleBinding binding, int position) {
        if (bean != null) {
            if (bean.getId() == id) {
                binding.tvTitle.setTextColor(CommonUtils.getColor(R.color.colorTheme));
                binding.viewLine.setBackgroundColor(CommonUtils.getColor(R.color.colorTheme));
            } else {
                binding.tvTitle.setTextColor(CommonUtils.getColor(R.color.select_navi_text));
                binding.viewLine.setBackgroundColor(CommonUtils.getColor(R.color.colorSubtitle));
            }
            binding.setBean(bean);
            binding.clWxarticle.setOnClickListener(v -> {
                if (selectPosition != position) {
                    lastPosition = selectPosition;
                    id = bean.getId();
                    selectPosition = position;
                    notifyItemChanged(lastPosition);
                    notifyItemChanged(selectPosition);

                    if (listener != null)
                        listener.onSelected(position);
                }
            });
        }
    }

    private OnSelectListener listener;

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelected(int position);
    }

    public void setId(int id) {
        this.id = id;
    }
}
