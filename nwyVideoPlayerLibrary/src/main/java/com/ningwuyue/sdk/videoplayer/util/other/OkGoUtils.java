package com.ningwuyue.sdk.videoplayer.util.other;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static com.ningwuyue.sdk.videoplayer.data.constant.Constant.HTTP_TIMEOUT_DEFALUT;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

public class OkGoUtils {
    public static void init(Application application) {
        if (application != null) {
            initOkGo(application);
        }
    }

    private static void initOkGo(Application application) {
        //1.
        // OkGo.getInstance().init(this);
        //2.构建OkHttpClient.Builder
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //3.配置log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("NWYVideoPlayer_Http");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        //4. 配置超时时间
        builder.readTimeout(HTTP_TIMEOUT_DEFALUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(HTTP_TIMEOUT_DEFALUT, TimeUnit.MILLISECONDS);
        builder.connectTimeout(HTTP_TIMEOUT_DEFALUT, TimeUnit.MILLISECONDS);

        //5. 配置Cookie，以下几种任选其一就行
        //使用sp保持cookie，如果cookie不过期，则一直有效
        // builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        //使用数据库保持cookie，如果cookie不过期，则一直有效
        // builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        //使用内存保持cookie，app退出后，cookie消失
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        //6. Https配置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        com.lzy.okgo.https.HttpsUtils.SSLParams sslParams1 = com.lzy.okgo.https.HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        //com.lzy.okgo.https.HttpsUtils.SSLParams sslParams2 = com.lzy.okgo.https.HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //com.lzy.okgo.https.HttpsUtils.SSLParams sslParams3 = com.lzy.okgo.https.HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //com.lzy.okgo.https.HttpsUtils.SSLParams sslParams4 = com.lzy.okgo.https.HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        //builder.hostnameVerifier(new SafeHostnameVerifier());

        //7. 配置OkGo

        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
       /* HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");*/
        //-------------------------------------------------------------------------------------//
        OkGo.getInstance().init(application)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0);                            //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        //.addCommonHeaders(headers)                      //全局公共头
        //.addCommonParams(params);
    }
}
