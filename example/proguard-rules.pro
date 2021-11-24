# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#


    #指定代码的压缩级别
    -optimizationpasses 5
    #包名不混合大小写
    -dontusemixedcaseclassnames
    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses
     #优化  不优化输入的类文件
    -dontoptimize
     #混淆时是否做预校验
    -dontpreverify
     #混淆时是否记录日志
    -verbose
     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
    #保护注解
    -keepattributes *Annotation*
     #如果引用了v4或者v7包
    -dontwarn android.support.**
    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }
    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }
    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }
     # 保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }
    # 保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet, int);
    }
    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable
    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }
    #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
    #-keepclassmembers enum * {
    #  public static **[] values();
    #  public static ** valueOf(java.lang.String);
    #}

    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }
    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }
    #避免混淆泛型 如果混淆报错建议关掉
    #–keepattributes Signature

    #关闭所有日志 log, java.io.Print, printStackTrace
    -assumenosideeffects class android.util.Log {
        public static *** e(...);
        public static *** w(...);
        public static *** i(...);
        public static *** d(...);
        public static *** v(...);
    }
    -assumenosideeffects class java.io.PrintStream {
        public *** print(...);
        public *** println(...);
    }
    -assumenosideeffects class java.lang.Throwable {
        public *** printStackTrace(...);
    }

# 保持包名不被混淆
#    -keeppackagenames com.mercury.sdk
#重新打包所有重命名的类文件，将它们移动到给定包中。 如果包中没有参数或一个空字符串，包被完全删除。 此选项将覆盖-flattenpackagehierarchy选项。 它可以使处理后的代码更小，更不容易理解。 它的已弃用名称是-defaultpackage。 仅适用于混淆处理。
#    -flattenpackagehierarchy
#    -repackageclasses com.mercury.sdk
#    -keep class com.mercury.sdk.** {*;}

#   异常提示排除操作
    -dontwarn com.mercury.sdk.**
    -dontwarn androidx.**

#    预防性配置：骑士卡遇到的和push-sdk一起使用时暴露出来的个别闪退问题
    -dontwarn com.taobao.**
    -dontwarn anet.channel.**
    -dontwarn anetwork.channel.**
    -dontwarn org.android.**
    -dontwarn org.apache.thrift.**
    -dontwarn com.xiaomi.**
    -dontwarn com.huawei.**

    -keepattributes *Annotation*

    -keep class com.taobao.** {*;}
    -keep class org.android.** {*;}
    -keep class anet.channel.** {*;}
    -keep class com.umeng.** {*;}
    -keep class com.xiaomi.** {*;}
    -keep class com.huawei.** {*;}
    -keep class org.apache.thrift.** {*;}

    -keep class com.alibaba.sdk.android.**{*;}
    -keep class com.ut.**{*;}
    -keep class com.ta.**{*;}

    -keep public class **.R$*{
       public static final int *;
    }

#获取oaid所需相关混淆设置
    -keep, includedescriptorclasses class com.asus.msa.SupplementaryDID.** { *; }
    -keepclasseswithmembernames class com.asus.msa.SupplementaryDID.** { *; }
    -keep, includedescriptorclasses class com.asus.msa.sdid.** { *; }
    -keepclasseswithmembernames class com.asus.msa.sdid.** { *; }
    -keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
    -keep class com.bun.miitmdid.**{*;}
    -keep class com.bun.lib.**{*;}
    -keep class com.samsung.android.deviceidservice.**{*;}
    -keep class a.**{*;}

   -keep class android.support.** { *; }


-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.ss.sys.ces.* {*;}
-keep class com.ss.android.**{*;}
-keep class com.pgl.sys.ces.* {*;}

-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.app.NotificationCompat**{
    public *;
}
-keep class com.bun.miitmdid.** {*;}
-keep class com.easyads.supplier.** {*;}
-keep class com.kwai.** {*;}