package com.android.myapplication.adapter;

import android.app.Activity;
import android.view.View;

import com.android.myapplication.R;
import com.android.myapplication.base.binding.BaseBindingAdapter;
import com.android.myapplication.base.binding.BaseBindingHolder;
import com.android.myapplication.bean.wanandroid.ArticlesBean;
import com.android.myapplication.data.UserUtil;
import com.android.myapplication.data.model.CollectModel;
import com.android.myapplication.databinding.ItemCategoryArticleBinding;
import com.android.myapplication.utils.PerfectClickListener;
import com.android.myapplication.utils.ToastUtil;
import com.android.myapplication.view.webview.WebViewActivity;
import com.android.myapplication.viewmodel.wan.WanNavigator;


/**
 * Created by jingbin on 2019/01/19.
 */

public class CategoryArticleAdapter extends BaseBindingAdapter<ArticlesBean, ItemCategoryArticleBinding> {

    private CollectModel model;
    private Activity activity;

    public CategoryArticleAdapter(Activity activity) {
        super(R.layout.item_category_article);
        model = new CollectModel();
        this.activity = activity;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, ArticlesBean bean, ItemCategoryArticleBinding binding, int position) {
        if (bean != null) {
            binding.setAdapter(this);
            binding.setBean(bean);
            binding.executePendingBindings();
            binding.vbCollect.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (UserUtil.isLogin(activity) && model != null) {
                        if (!binding.vbCollect.isChecked()) {
                            model.unCollect(false, bean.getId(), bean.getOriginId(), new WanNavigator.OnCollectNavigator() {
                                @Override
                                public void onSuccess() {
                                    bean.setCollect(binding.vbCollect.isChecked());
                                    ToastUtil.showToastLong("已取消收藏");
                                }

                                @Override
                                public void onFailure() {
                                    bean.setCollect(true);
                                    refreshNotifyItemChanged(position);
                                    ToastUtil.showToastLong("取消收藏失败");
                                }
                            });
                        } else {
                            model.collect(bean.getId(), new WanNavigator.OnCollectNavigator() {
                                @Override
                                public void onSuccess() {
                                    bean.setCollect(true);
                                }

                                @Override
                                public void onFailure() {
                                    bean.setCollect(false);
                                    refreshNotifyItemChanged(position);
                                }
                            });
                        }
                    } else {
                        bean.setCollect(false);
                        refreshNotifyItemChanged(position);
                    }
                }
            });
        }
    }

    public void openDetail(ArticlesBean bean) {
        WebViewActivity.loadUrl(activity, bean.getLink(), bean.getTitle());
    }
}
