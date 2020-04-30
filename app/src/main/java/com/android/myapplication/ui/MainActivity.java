package com.android.myapplication.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.myapplication.R;
import com.android.myapplication.app.ConstantsImageUrl;
import com.android.myapplication.base.BaseActivity;
import com.android.myapplication.bean.wanandroid.CoinUserInfoBean;
import com.android.myapplication.data.UserUtil;
import com.android.myapplication.databinding.ActivityMainBinding;
import com.android.myapplication.databinding.NavHeaderMainBinding;
import com.android.myapplication.http.rx.RxBus;
import com.android.myapplication.http.rx.RxBusBaseMessage;
import com.android.myapplication.http.rx.RxCodeConstants;
import com.android.myapplication.ui.wan.child.LoginActivity;
import com.android.myapplication.utils.BaseTools;
import com.android.myapplication.utils.CommonUtils;
import com.android.myapplication.utils.DialogBuild;
import com.android.myapplication.utils.GlideUtil;
import com.android.myapplication.utils.PerfectClickListener;
import com.android.myapplication.utils.SPUtils;
import com.android.myapplication.utils.UpdateUtil;
import com.android.myapplication.view.MyFragmentPagerAdapter;
import com.android.myapplication.view.OnLoginListener;
import com.android.myapplication.view.webview.WebViewActivity;
import com.android.myapplication.viewmodel.wan.MainViewModel;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> implements View.OnClickListener {

    public static boolean isLaunch;
    private ViewPager vpContent;
    private ImageView ivTitleTwo;
    private ImageView ivTitleOne;
    private ImageView ivTitleThree;
    private NavHeaderMainBinding bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showContentView();
        isLaunch = true;
        initView();
        initContentFragment();
        initDrawerLayout();
        initRxBus();
    }

    private void initRxBus() {
        Disposable subscribe = RxBus.getDefault().toObservable(RxCodeConstants.JUMP_TYPE_TO_ONE, RxBusBaseMessage.class)
                .subscribe(new Consumer<RxBusBaseMessage>() {
                    @Override
                    public void accept(RxBusBaseMessage rxBusBaseMessage) throws Exception {
                        setCurrentItem(2);
                    }
                });
        addSubscription(subscribe);
        Disposable subscribe1 = RxBus.getDefault().toObservable(RxCodeConstants.LOGIN, Boolean.class)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isLogin) throws Exception {
                        if (isLogin)
                            viewModel.getUserInfo();
                        else
                            viewModel.getCoin().setValue(null);
                    }
                });
        addSubscription(subscribe1);
    }

    private void initDrawerLayout() {
        bindingView.navView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = bindingView.navView.getHeaderView(0);
        bind = DataBindingUtil.bind(headerView);
        bind.setViewModel(viewModel);
        bindingView.setLifecycleOwner(this);

        bind.dayNightSwitch.setChecked(SPUtils.getNightMode());
        viewModel.isReadOk.set(SPUtils.isRead());

        GlideUtil.displayCircle(bind.ivAvatar, ConstantsImageUrl.IC_AVATAR);
        bind.llNavExit.setOnClickListener(this);
        bind.ivAvatar.setOnClickListener(this);

        bind.llNavHomepage.setOnClickListener(listener);
        bind.llNavScanDownload.setOnClickListener(listener);
        bind.llNavDeedback.setOnClickListener(listener);
        bind.llNavAbout.setOnClickListener(listener);
        bind.llNavLogin.setOnClickListener(listener);
        bind.llNavCollect.setOnClickListener(listener);
        bind.llInfo.setOnClickListener(listener);
        bind.llNavCoin.setOnClickListener(listener);
        bind.llNavAdmire.setOnClickListener(listener);
        bind.tvRank.setOnClickListener(listener);

        viewModel.getUserInfo();
        viewModel.getCoin().observe(this, new Observer<CoinUserInfoBean>() {
            @Override
            public void onChanged(@Nullable CoinUserInfoBean coinUserInfoBean) {
                if (coinUserInfoBean != null) {
                    bind.tvUsername.setText(coinUserInfoBean.getUsername());
                    bind.tvLevel.setText(String.format("Lv.%s", UserUtil.getLevel(coinUserInfoBean.getCoinCount())));
                    bind.tvRank.setText(String.format("排名 %s", coinUserInfoBean.getRank()));
                } else {
                    bind.tvUsername.setText("玩安卓登录");
                    bind.tvLevel.setText("Lv.1");
                    bind.tvRank.setText("");
                }
            }
        });
    }

    private PerfectClickListener listener = new PerfectClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            bindingView.drawerLayout.closeDrawer(GravityCompat.START);
            //为了先隐藏drawView
            bindingView.drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()) {
                        case R.id.ll_nav_homepage:
                            break;
                        case R.id.ll_nav_login:
                            DialogBuild.showItems(v, new OnLoginListener() {
                                @Override
                                public void loginWanAndroid() {
                                    LoginActivity.start(MainActivity.this);
                                }

                                @Override
                                public void loginGitHub() {
                                    WebViewActivity.loadUrl(v.getContext(), "https://github.com/login", "登录GitHub账号");
                                }
                            });
                            break;
                        case R.id.ll_info:
                            // 登录
                            if (!UserUtil.isLogin()) {
                                LoginActivity.start(MainActivity.this);
                            } else {
                                //MyCoinActivity.start(MainActivity.this);
                            }
                            break;
                    }
                }
            }, 260);


        }
    };

    private void initContentFragment() {
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        vpContent.setAdapter(myFragmentPagerAdapter);
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setCurrentItem(position);
            }
        });
        setCurrentItem(0);
    }

    /**
     * 切换页面
     *
     * @param position 分类角标
     */
    private void setCurrentItem(int position) {
        boolean isOne = false;
        boolean isTwo = false;
        boolean isThree = false;
        switch (position) {
            case 0:
                isOne = true;
                break;
            case 1:
                isTwo = true;
                break;
            case 2:
                isThree = true;
                break;
            default:
                isTwo = true;
                break;
        }
        vpContent.setCurrentItem(position);
        ivTitleOne.setSelected(isOne);
        ivTitleTwo.setSelected(isTwo);
        ivTitleThree.setSelected(isThree);
    }

    private void initView() {
        setNoTitle();
        setSupportActionBar(bindingView.include.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            //去除默认title显示
            actionBar.setDisplayShowTitleEnabled(false);
        vpContent = bindingView.include.vpContent;
        ivTitleOne = bindingView.include.ivTitleOne;
        ivTitleTwo = bindingView.include.ivTitleTwo;
        ivTitleThree = bindingView.include.ivTitleThree;
        bindingView.include.llTitleMenu.setOnClickListener(this);
        bindingView.include.ivTitleOne.setOnClickListener(this);
        bindingView.include.ivTitleTwo.setOnClickListener(this);
        bindingView.include.ivTitleThree.setOnClickListener(this);
        getClipContent();
        UpdateUtil.check(this, false);
    }

    //获得剪切板链接
    private void getClipContent() {
        String clipContent = BaseTools.getClipContent();
        if (!TextUtils.isEmpty(clipContent)) {
            DialogBuild.showCustom(bindingView.drawerLayout, clipContent, "打开其中链接", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WebViewActivity.loadUrl(MainActivity.this, clipContent, "加载中..");
                    BaseTools.clearClipboard();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_title_menu:
                // 开启菜单
                bindingView.drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_title_two:
                // 不然cpu会有损耗
                if (vpContent.getCurrentItem() != 1) {
                    setCurrentItem(1);
                }
                break;
            case R.id.iv_title_one:
                if (vpContent.getCurrentItem() != 0) {
                    setCurrentItem(0);
                }
                break;
            case R.id.iv_title_three:
                if (vpContent.getCurrentItem() != 2) {
                    setCurrentItem(2);
                }
                break;
            case R.id.iv_avatar:
                // 头像进入GitHub
                WebViewActivity.loadUrl(v.getContext(), CommonUtils.getString(R.string.string_url_cloudreader), "CloudReader");
                break;
            case R.id.ll_nav_exit:
                // 退出应用
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bindingView.drawerLayout.isDrawerOpen(GravityCompat.START))
                bindingView.drawerLayout.closeDrawer(GravityCompat.START);
            else
                moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLaunch = false;
        // 杀死该应用进程 需要权限 todo 如果是重建就GG
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
}
