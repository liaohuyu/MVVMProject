package com.library.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.library.http.utils.CheckNetwork;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * on 2020/4/20
 * 使用时请在"CloudReaderApplication"下初始化。
 */
public class HttpUtils {
    private static HttpUtils instance;
    private Gson gson;
    private Context context;
    private ImplTokenGetListener listener;
    // wanandroid、gankio、时光网
    public final static String API_GANKIO = "https://gank.io/api/";
    public final static String API_DOUBAN = "Https://api.douban.com/";
    public final static String API_TING = "https://tingapi.ting.baidu.com/v1/restserver/";
    public final static String API_GITEE = "https://gitee.com/";
    public final static String API_WAN_ANDROID = "https://www.wanandroid.com/";
    public final static String API_QSBK = "http://m2.qiushibaike.com/";
    public final static String API_MTIME = "https://api-m.mtime.cn/";
    public final static String API_MTIME_TICKET = "https://ticket-api-m.mtime.cn/";
    /**
     * 分页数据，每页的数量
     */
    public static int per_page_more = 20;

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        HttpHead.init(context);
    }

    public Retrofit.Builder getBuilder(String apiUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(getOkClient())
                .baseUrl(apiUrl)
                //.addCallAdapterFactory(new NullOnEmptyConverterFactory())为什么要把这个转换成null
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder;
    }

    private Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            //builder.setLenient();//对格式要求不严格
            //builder.setFieldNamingStrategy(new AnnotateNaming()); todo 还不清楚有什么用
            builder.serializeNulls();//空对象序列化
            gson = builder.create();
        }
        return gson;
    }

    private static class AnnotateNaming implements FieldNamingStrategy {
        @Override
        public String translateName(Field field) {
            ParamNames a = field.getAnnotation(ParamNames.class);
            return a != null ? a.value() : FieldNamingPolicy.IDENTITY.translateName(field);
        }
    }

    private OkHttpClient getOkClient() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 50 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        if (sslContext != null) {
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            okBuilder.sslSocketFactory(socketFactory, (X509TrustManager) trustAllCerts[0]);
        }

        /*addInterceptor() 添加应用拦截器
            ● 不需要担心中间过程的响应,如重定向和重试.
            ● 总是只调用一次,即使HTTP响应是从缓存中获取.
            ● 观察应用程序的初衷. 不关心OkHttp注入的头信息如: If-None-Match.
            ● 允许短路而不调用 Chain.proceed(),即中止调用.
            ● 允许重试,使 Chain.proceed()调用多次.
            addNetworkInterceptor() 添加网络拦截器
            ● 能够操作中间过程的响应,如重定向和重试.
            ● 当网络短路而返回缓存响应时不被调用.
            ● 只观察在网络上传输的数据.
            ● 携带请求来访问连接.*/
        /*Application Interceptors
            使用 addInterceptor() 注册
            有无网络都会被调用到
            application 拦截器只会被调用一次，调用 chain.proceed() 得到的是重定向之后最终的响应信息，不会通过 chain.connection() 获得中间过程的响应信息
            允许 short-circuit (短路) 并且允许不去调用 chain.proceed() 请求服务器数据，可通过缓存来返回数据。
            Network Interceptors
            使用 addNetworkInterceptor() 注册
            无网络时不会被调用
            可以显示更多的信息，比如 OkHttp 为了减少数据的传输时间以及传输流量而自动添加的请求头 Accept-Encoding: gzip 希望服务器能返回经过压缩过的响应数据。
            chain.connection() 返回不为空的 Connection 对象可以查询到客户端所连接的服务器的IP地址以及TLS配置信息。

            作者：Demons_96
            链接：https://www.jianshu.com/p/1752753db538*/
        return okBuilder.readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                // 持久化cookie
                .addInterceptor(new ReceivedCookiesInterceptor(context))
                .addInterceptor(new AddCookiesInterceptor(context))
                //缓存
                .addNetworkInterceptor(new NetCacheInterceptor())
                .addInterceptor(new OfflineCacheInterceptor())
                // 添加缓存，无网访问时会拿缓存,只会缓存get请求
                // .addInterceptor(new AddCacheInterceptor(context))
                // .addInterceptor(new HttpHeadInterceptor())
                .cache(cache)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(BuildConfig.DEBUG ?
                                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }


    final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }};

    //有网缓存
    private static class NetCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            int maxAge = 60;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("Pragma")
                    .build();
        }
    }

    private static class ReceivedCookiesInterceptor implements Interceptor {
        private Context context;

        ReceivedCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Response originalResponse = chain.proceed(chain.request());
            //这里获取请求返回的cookie
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {

                List<String> d = originalResponse.headers("Set-Cookie");
//                Log.e("jing", "------------得到的 cookies:" + d.toString());

                // 返回cookie
                if (!TextUtils.isEmpty(d.toString())) {

                    SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorConfig = sharedPreferences.edit();
                    String oldCookie = sharedPreferences.getString("cookie", "");

                    HashMap<String, String> stringStringHashMap = new HashMap<>();

                    // 之前存过cookie
                    if (!TextUtils.isEmpty(oldCookie)) {
                        String[] substring = oldCookie.split(";");
                        for (String aSubstring : substring) {
                            if (aSubstring.contains("=")) {
                                String[] split = aSubstring.split("=");
                                stringStringHashMap.put(split[0], split[1]);
                            } else {
                                stringStringHashMap.put(aSubstring, "");
                            }
                        }
                    }
                    String join = StringUtils.join(d, ";");
                    String[] split = join.split(";");

                    // 存到Map里
                    for (String aSplit : split) {
                        String[] split1 = aSplit.split("=");
                        if (split1.length == 2) {
                            stringStringHashMap.put(split1[0], split1[1]);
                        } else {
                            stringStringHashMap.put(split1[0], "");
                        }
                    }

                    // 取出来
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stringStringHashMap.size() > 0) {
                        for (String key : stringStringHashMap.keySet()) {
                            stringBuilder.append(key);
                            String value = stringStringHashMap.get(key);
                            if (!TextUtils.isEmpty(value)) {
                                stringBuilder.append("=");
                                stringBuilder.append(value);
                            }
                            stringBuilder.append(";");
                        }
                    }

                    editorConfig.putString("cookie", stringBuilder.toString());
                    editorConfig.apply();
//                    Log.e("jing", "------------处理后的 cookies:" + stringBuilder.toString());
                }
            }

            return originalResponse;
        }
    }

    private static class AddCookiesInterceptor implements Interceptor {
        private Context context;

        AddCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            final Request.Builder builder = chain.request().newBuilder();
            SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String cookie = sharedPreferences.getString("cookie", "");
            builder.addHeader("Cookie", cookie);
            return chain.proceed(builder.build());
        }
    }

    private class OfflineCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!CheckNetwork.isNetworkConnected(context)) {
                int offlineCacheTime = 60 * 60 * 24 * 28;//离线的时候的缓存的过期时间
                request = request.newBuilder()
                        .header("Cache-Control", "public,only-if-cache,max-stale=" + offlineCacheTime)
                        .build();
            }
            return chain.proceed(request);
        }
    }

    private class HttpHeadInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            builder.addHeader("Accept", "application/json;versions=1");
            if (CheckNetwork.isNetworkConnected(context)) {
                int maxAge = 60;
                builder.addHeader("Cache-Control", "public, max-age=" + maxAge);
            } else {
                int maxStale = 60 * 60 * 24 * 28;
                builder.addHeader("Cache-Control", "public,,only-if-cached, max-stale=" + maxStale);
            }
            return chain.proceed(builder.build());
        }
    }

    private static class AddCacheInterceptor implements Interceptor {
        private Context context;

        AddCacheInterceptor(Context context) {
            super();
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            if (!CheckNetwork.isNetworkConnected(context)) {
                CacheControl.Builder cacheBuilder = new CacheControl.Builder();
                cacheBuilder.maxAge(0, TimeUnit.SECONDS);
                cacheBuilder.maxStale(365, TimeUnit.DAYS);
                CacheControl cacheControl = cacheBuilder.build();
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (CheckNetwork.isNetworkConnected(context)) {
                // read from cache
                int maxAge = 0;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                // tolerate 4-weeks stale
                int maxStale = 60 * 60 * 24 * 28;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }
}


