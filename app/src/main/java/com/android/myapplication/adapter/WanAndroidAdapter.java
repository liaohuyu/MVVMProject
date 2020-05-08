package com.android.myapplication.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.android.myapplication.R;
import com.android.myapplication.base.binding.BaseBindingAdapter;
import com.android.myapplication.base.binding.BaseBindingHolder;
import com.android.myapplication.bean.wanandroid.ArticlesBean;
import com.android.myapplication.data.UserUtil;
import com.android.myapplication.data.model.CollectModel;
import com.android.myapplication.databinding.ItemWanAndroidBinding;
import com.android.myapplication.utils.PerfectClickListener;
import com.android.myapplication.utils.ToastUtil;
import com.android.myapplication.view.webview.WebViewActivity;
import com.android.myapplication.viewmodel.wan.WanNavigator;

/**
 * on 2020/4/29
 */
public class WanAndroidAdapter extends BaseBindingAdapter<ArticlesBean, ItemWanAndroidBinding> {

    private Activity activity;
    private CollectModel model;
    /**
     * 是我的收藏页进来的，全部是收藏状态。bean里面没有返回isCollect信息
     */
    public boolean isCollectList = false;
    /**
     * 不显示类别信息
     */
    public boolean isNoShowChapterName = false;
    /**
     * 不显示作者名字
     */
    public boolean isNoShowAuthorName = false;
    /**
     * 列表中是否显示图片
     */
    private boolean isNoImage = false;

    public WanAndroidAdapter(Activity activity) {
        super(R.layout.item_wan_android);
        this.activity = activity;
        model = new CollectModel();
    }

    @Override
    protected void bindView(BaseBindingHolder holder, ArticlesBean bean, ItemWanAndroidBinding binding, int position) {
        if (bean != null) {
            binding.setBean(bean);
            //Binding().setVariable(BR.book, data.get(position)); todo 这句话好像不要也没关系
            //Binding().executePendingBindings() 刷新界面的  最后调用
            binding.setAdapter(WanAndroidAdapter.this);
            if (!TextUtils.isEmpty(bean.getEnvelopePic()) && !isNoImage) {
                bean.setShowImage(true);
            } else {
                bean.setShowImage(false);
            }

            //todo 这里是没等数据返回的
            binding.vbCollect.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (UserUtil.isLogin(activity) && model != null) {
                        // 为什么状态值相反？因为点了之后控件已改变状态
//                            DebugUtil.error("-----binding.vbCollect.isChecked():" + binding.vbCollect.isChecked());
                        if (!binding.vbCollect.isChecked()) {
                            model.unCollect(isCollectList, bean.getId(), bean.getOriginId(), new WanNavigator.OnCollectNavigator() {
                                @Override
                                public void onSuccess() {
                                    if (isCollectList) {

                                        int indexOf = getData().indexOf(bean);
                                        // 移除数据增加删除动画
                                        getData().remove(indexOf);
                                        refreshNotifyItemRemoved(indexOf);
                                    } else {
                                        bean.setCollect(binding.vbCollect.isChecked());
                                    }
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
//                                        ToastUtil.showToastLong("收藏成功");
                                }

                                @Override
                                public void onFailure() {
//                                        ToastUtil.showToastLong("收藏失败");
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

    public void setCollectList() {
        this.isCollectList = true;
    }

    public void setNoShowChapterName() {
        this.isNoShowChapterName = true;
    }

    public void setNoShowAuthorName() {
        isNoShowAuthorName = true;
    }

    public void setNoImage(boolean isNoImage) {
        this.isNoImage = isNoImage;
    }

    public void openDetail(ArticlesBean bean) {
        WebViewActivity.loadUrl(activity, bean.getLink(), bean.getTitle());
    }

    public void openArticleList(ArticlesBean bean) {
        //ArticleListActivity.start(activity, bean.getChapterId(), bean.getChapterName());
    }
}
