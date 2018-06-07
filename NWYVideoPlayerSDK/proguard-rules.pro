#==================================【基本配置】====================================================================
#==================================【基本配置】====================================================================
# 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 不做预检验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify
# 有了verbose这句话，混淆后就会生成映射文件
-verbose
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*
# 避免混淆泛型
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#忽略警告
-ignorewarning
#==================================【项目配置】====================================================================
#==================================【项目配置】====================================================================
-keep class com.ningwuyue.sdk.nwyvideoplayersdk.init.*{*;}
-keep class com.ningwuyue.sdk.nwyvideoplayersdk.init.SvipplayEntity{*;}
-keep class com.ningwuyue.sdk.nwyvideoplayersdk.init.VipwebEntity{*;}
-keep class com.ningwuyue.sdk.nwyvideoplayersdk.init.AnthologyEntity{*;}


#对于带有回调函数的onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}
# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留了继承自Activity、Application这些类的子类
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.database.sqlite.SQLiteOpenHelper{*;}
# 如果有引用android-support-v4.jar包，可以添加下面这行
-keep public class com.null.test.ui.fragment.** {*;}
#如果引用了v4或者v7包
-dontwarn android.support.**
# 保留Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
    public void * (android.view.View);
}
# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}
# 保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}
# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

##表示不混淆任何一个View中的setXxx()和getXxx()方法，因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了。
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#表示不混淆Parcelable实现类中的CREATOR字段，毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    *;
}
# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
    void *(**On*Event);
}
#实体类
-keep class guoxin.app.android.bean.** { *; }
#内部方法
-keepattributes EnclosingMethod
# 过滤R文件的混淆：
-keep class **.R$* {*;}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }
#==================================【三方配置】====================================================================
#==================================【三方配置】====================================================================

#腾讯广点通-----------start---------
-keep class android-support-v4.**{*;}
-keep public class * extends android.support.v4.**
-keep class android.view.**{*;}
-keep class com.tencent.bugly.**{*;}
-keep class com.tencent.stat.**{*;}
-keep class com.tencent.smtt.**{*;}
-keep class com.tencent.beacon.**{*;}
-keep class com.tencent.mm.**{*;}
-keep class com.tencent.apkupdate.**{*;}
-keep class com.tencent.tmassistantsdk.**{*;}
-keep class org.apache.http.** { * ;}
-keep class com.qq.jce.**{*;}
-keep class com.qq.taf.**{*;}
-keep class com.tencent.connect.**{*;}
-keep class com.tencent.map.**{*;}
-keep class com.tencent.open.**{*;}
-keep class com.tencent.qqconnect.**{*;}
-keep class com.tencent.tauth.**{*;}
-keep class com.tencent.feedback.**{*;}
-keep class common.**{*;}
-keep class exceptionupload.**{*;}
-keep class mqq.**{*;}
-keep class qimei.**{*;}
-keep class strategy.**{*;}
-keep class userinfo.**{*;}
-keep class com.pay.**{*;}
-keep class com.demon.plugin.**{*;}
-keep class com.tencent.midas.**{*;}
-keep class oicq.wlogin_sdk.**{*;}
-keep class com.tencent.ysdk.**{*;}
-keep class com.tencent.ysdk.**{*;}
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class com.tencent.smtt.export.external.**{
  *;
}
-keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
*;
}
-keep class com.tencent.smtt.sdk.CacheManager {
public *;
}
-keep class com.tencent.smtt.sdk.CookieManager {
public *;
}
-keep class com.tencent.smtt.sdk.WebHistoryItem {
public *;
}
-keep class com.tencent.smtt.sdk.WebViewDatabase {
public *;
}
-keep class com.tencent.smtt.sdk.WebBackForwardList {
public *;
}
-keep public class com.tencent.smtt.sdk.WebView {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
public static final <fields>;
public java.lang.String getExtra();
public int getType();
}
-keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebView$PictureListener {
public <fields>;
public <methods>;
}
-keepattributes InnerClasses
-keep public enum com.tencent.smtt.sdk.WebSettings$** {
  *;
}
-keep public enum com.tencent.smtt.sdk.QbSdk$** {
  *;
}
-keep public class com.tencent.smtt.sdk.WebSettings {
  public *;
}
-keepattributes Signature
-keep public class com.tencent.smtt.sdk.ValueCallback {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebViewClient {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.DownloadListener {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebChromeClient {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
public <fields>;
public <methods>;
}
-keep class com.tencent.smtt.sdk.SystemWebChromeClient{
public *;
}
# 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
public protected *;
}
# 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
public protected *;
}
-keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
public protected *;
}
-keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebIconDatabase {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.WebStorage {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.DownloadListener {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.QbSdk {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.CookieSyncManager {
public <fields>;
public <methods>;
}

-keep public class com.tencent.smtt.sdk.Tbs* {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.utils.LogFileUtils {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.utils.TbsLog {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.utils.TbsLogClient {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.CookieSyncManager {
public <fields>;
public <methods>;
}
# Added for game demos
-keep public class com.tencent.smtt.sdk.TBSGamePlayer {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
public <fields>;
public <methods>;
}
-keep public class com.tencent.smtt.utils.Apn {
public <fields>;
public <methods>;
}
#腾讯广点通-----------start---------

#腾讯X5webview-------start-------
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.app.NotificationCompat**{
    public *;
}
#腾讯X5webview-------end-------
#glide---------start------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#glide---------end------
##播放器--------start-------
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class com.shuyu.gsyvideoplayer.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.**
##播放器--------end-------

#eventbus混淆------start------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#eventbus混淆------end------

#okhttputils------start------
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
#okhttputils------end------






