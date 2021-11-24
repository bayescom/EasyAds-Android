#-keep class com.bytedance.sdk.openadsdk.** { *; }
#-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
#-keep class com.pgl.sys.ces.* {*;}
#-keep class com.bytedance.embed_dr.** {*;}
#-keep class com.bytedance.embedapplog.** {*;}
#-dontwarn com.ss.android.socialbase.downloader.**
#-dontwarn com.ss.android.crash.log.**

#3900版本及以上版本混淆规则如下：
#-keep class com.bytedance.sdk.openadsdk.** { *; }
#-keep class com.bytedance.frameworks.** { *; }
#
#-keep class ms.bd.c.Pgl.**{*;}
#-keep class com.bytedance.mobsec.metasec.ml.**{*;}
#
#-keep class com.ss.android.**{*;}
#
#-keep class com.bytedance.embedapplog.** {*;}
#-keep class com.bytedance.embed_dr.** {*;}
#
#-keep class com.bykv.vk.** {*;}

#4000版本混淆，aar中已支持，可以不添加
#-keepclassmembers class * {
#    *** getContext(...);
#    *** getActivity(...);
#    *** getResources(...);
#    *** startActivity(...);
#    *** startActivityForResult(...);
#    *** registerReceiver(...);
#    *** unregisterReceiver(...);
#    *** query(...);
#    *** getType(...);
#    *** insert(...);
#    *** delete(...);
#    *** update(...);
#    *** call(...);
#    *** setResult(...);
#    *** startService(...);
#    *** stopService(...);
#    *** bindService(...);
#    *** unbindService(...);
#    *** requestPermissions(...);
#    *** getIdentifier(...);
#   }
#
#-keep class com.bytedance.pangle.** {*;}
#-keep class com.bytedance.sdk.openadsdk.** { *; }
#-keep class com.bytedance.frameworks.** { *; }
#
#-keep class ms.bd.c.Pgl.**{*;}
#-keep class com.bytedance.mobsec.metasec.ml.**{*;}
#
#-keep class com.ss.android.**{*;}
#
#-keep class com.bytedance.embedapplog.** {*;}
#-keep class com.bytedance.embed_dr.** {*;}
#
#-keep class com.bykv.vk.** {*;}


