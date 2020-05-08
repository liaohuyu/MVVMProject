package com.android.myapplication.http;

import com.android.myapplication.bean.UpdateBean;
import com.android.myapplication.bean.wanandroid.BaseResultBean;
import com.android.myapplication.bean.wanandroid.CoinUserInfoBean;
import com.android.myapplication.bean.wanandroid.HomeListBean;
import com.android.myapplication.bean.wanandroid.LoginBean;
import com.android.myapplication.bean.wanandroid.TreeBean;
import com.android.myapplication.bean.wanandroid.WanAndroidBannerBean;
import com.android.myapplication.bean.wanandroid.WxarticleDetailItemBean;
import com.android.myapplication.bean.wanandroid.WxarticleItemBean;
import com.library.http.HttpUtils;
import com.library.http.utils.BuildFactory;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * on 2020/4/22
 */
public interface HttpClient {
    class Builder {
        public static HttpClient getWanAndroidServer() {
            return BuildFactory.getInstance().create(HttpClient.class, HttpUtils.API_WAN_ANDROID);
        }

        public static HttpClient getGiteeServer() {
            return BuildFactory.getInstance().create(HttpClient.class, HttpUtils.API_GITEE);
        }
    }


    /**
     * 收藏网址
     *
     * @param name 标题
     * @param link 链接
     */
    @FormUrlEncoded
    @POST("lg/collect/addtool/json")
    Flowable<HomeListBean> collectUrl(@Field("name") String name, @Field("link") String link);

    /**
     * 删除收藏网站
     *
     * @param id 收藏网址id
     */
    @FormUrlEncoded
    @POST("lg/collect/deletetool/json")
    Flowable<HomeListBean> unCollectUrl(@Field("id") int id);

    /**
     * 取消收藏(首页文章列表)
     *
     * @param id 文章id
     */
    @POST("lg/uncollect_originId/{id}/json")
    Flowable<HomeListBean> unCollectOrigin(@Path("id") int id);

    /**
     * 获取个人积分，需登录后访问
     */
    @GET("lg/coin/userinfo/json")
    Observable<BaseResultBean<CoinUserInfoBean>> getCoinUserInfo();

    /**
     * 删除收藏网站
     *
     * @param id 收藏网址id
     */
    @FormUrlEncoded
    @POST("lg/uncollect/{id}/json")
    Flowable<HomeListBean> unCollect(@Path("id") int id, @Field("originId") int originId);

    /**
     * 收藏本站文章，errorCode返回0为成功
     *
     * @param id 文章id
     */
    @POST("lg/uncollect/{id}/json")
    Flowable<HomeListBean> collect(@Path("id") int id);

    /**
     * 检查更新
     */
    @GET("jingbin127/ApiServer/raw/master/update/update.json")
    Observable<UpdateBean> checkUpdate();

    @FormUrlEncoded  //Field post的实体
    @POST("user/register")
    Observable<LoginBean> register(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword);

    @FormUrlEncoded
    @POST("user/login")
    Observable<LoginBean> login(@Field("username") String username, @Field("password") String password);

    /**
     * 退出
     */
    @GET("user/logout/json")
    Flowable<LoginBean> logout();

    /**
     * 玩安卓轮播图
     */
    @GET("banner/json")
    Observable<WanAndroidBannerBean> getWanAndroidBanner();

    @GET("article/list/{page}/json")
        //?后参数用Query
    Observable<HomeListBean> getHomeList(@Path("page") int page, @Query("cid") Integer cid);

    @GET("article/listproject/{page}/json")
    Observable<HomeListBean> getProjectList(@Path("page") int page);

    @GET("wxarticle/chapters/json")
    Observable<BaseResultBean<List<WxarticleItemBean>>> getWxArticle();

    @GET("wxarticle/list/{id}/{page}/json")
    Observable<BaseResultBean<WxarticleDetailItemBean>> getWxArticleDetail(@Path("id") int id, @Path("page") int page);

    /**
     * 体系数据
     */
    @GET("tree/json")
    Observable<TreeBean> getTree();
}
