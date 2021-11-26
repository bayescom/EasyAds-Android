# 常见问题处理

## 提要

聚合SDK主要负责加载策略的分发逻辑和各广告SDK调用逻辑，不负责具体的广告展示，广告的展示逻辑主要是由各个广告SDK来负责。


---

Q: 项目构建时出现 “unexpected element < queries> found in < manifest>”异常?

A: 穿山甲新版本包引入了queries内容导致的，queries是Android11新引入的manifest基础成分，低版本的gradle无法识别，[点此查看详细说明](https://stackoverflow.com/questions/62969917/how-do-i-fix-unexpected-element-queries-found-in-manifest)。Google发布了以下插件包更新来支持此特性，建议升级Android Gradle Plugin 到以下版本：
 
- 3.3.3
- 3.4.3
- 3.5.4
- 3.6.4
- 4.0.1

---

Q: 请求不到广告怎么办？

A: 首先检查广告的策略配置是否正确，如果配置正确，根据SDK返回的异常code和msg，翻阅**SDK错误码**部分来分析错误原因。<br/>此外，APP应适配好无广告返回时的页面展示和跳转流程，因为不可能所有用户100%都有填充广告。

如果配置一切正常，可以检查是否为未适配http请求导致，Android 9.0开始应用默认不支持http的请求，可能导致广告请求失败，资源和应用下载出现如下报错，请媒体自行适配http请求
`java.io.IOException: Cleartext HTTP traffic to *** notpermitted`

---


Q: 广告拉取成功了，但是为什么没有展示回调？

A: 首先检查是否正常调用了展示方法。一切都正常的话，有可能下发了视频类广告，但未开启硬件加速，导致视频无法正常播放，可以检查日志中是否包含类似 `A TextureView or a subclass can only be used with hardware acceleration enabled.` 这样的日志信息，说明APP没有开启硬件加速。建议在应用`AndroidManifest`中的` application`  层级配置`android:hardwareAccelerated="true"` 以后重试即可。也可配置在指定activity上

---

Q: Native、Banner广告展示大小有问题

A: Native、Banner广告展示内容太大或太小、广告内容被截掉了一部分等，这些一般是代码中的宽高设置有问题导致的。建议首先**阅读广告位的最佳实践说明**，有明确解释广告位大小设置相关建议。 

---



