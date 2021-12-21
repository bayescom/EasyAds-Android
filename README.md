# EasyAds-Android 快速指引

## 1. 支持的SDK平台及广告位

| SDK平台 | 开屏 | 激励视频 | 横幅 | 插屏(弹窗) | 模板信息流 | 全屏视频 | draw信息流 |
|-------|---|---|---|---|---|---|---| 
| 穿山甲   | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 优量汇   | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| 百青藤   | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| 快手    | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ |
 

## 2. 快速接入

下面介绍EasyAds的快速接入方法，开发中也可以参考[Example](https://github.com/bayescom/EasyAds-Android/tree/main/example)下的示例工程，快速了解。

### 2.1 引入SDK

**方式一(推荐)**：
使用Gradle 添加依赖，在项目根目录的build.gradle 文件中添加 `JitPack` 仓库

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
在项目module目录下的build.gradle 文件中添加SDK内容依赖

```
    dependencies {
        //EasyAds sdk（必须）
        implementation "com.github.bayescom:EasyAds-Android:1.0"
        //穿山甲广告SDK（推荐）
        implementation(name: "open_ad_sdk_4105", ext: "aar")
        //优量汇广告SDK（推荐）
        implementation(name: "GDTSDK_4431", ext: "aar")
        //快手广告SDK（可选）
        implementation(name: "kssdk-ad_3318", ext: "aar")
        //百度广告SDK（可选）
        implementation(name: "Baidu_MobAds_SDK_918", ext: "aar")
    }
```

**方式二**：

将`EasyAds`项目以`Library`的形式导入APP项目工程。


### 2.2 SDK分发策略配置

配置SDK的分发策略，可方便的实现流量的切分操作，实现多SDK的混合执行策略。

在EasyAds中，我们通过JSON文件的方式配置SDK的分发策略，开发者可根据自身流量分发的需求，按照EasyAds中提供的JSON配置格式及方法设置流量分发策略。

以开屏广告对接穿山甲和优量汇两个SDK为例，配置选择80%流量穿山甲->优量汇的顺序请求，20%流量优量汇->穿山甲的顺序请求，配置如下所示。

其中，suppliers字段下配置媒体在穿山甲和优量汇平台申请的广告代码位信息，rules字段下配置流量分发策略及比例；

策略JSON的配置说明详细见：[SDK策略配置JSON说明](https://github.com/bayescom/EasyAds-Android/wiki/2.-SDK%E5%88%86%E5%8F%91%E7%AD%96%E7%95%A5%E9%85%8D%E7%BD%AEJSON)

不同广告位的JSON策略配置示例见：[不同广告位JSON配置示例](https://github.com/bayescom/EasyAds-Android/tree/main/example/src/main/assets)

```json
{
  "rules": [
    {
      "tag": "A",
      "sort": [
        1,
        2
      ],
      "percent": 80
    },
    {
      "tag": "B",
      "sort": [
        2,
        1
      ],
      "percent": 20
    }
  ],
  "suppliers": [
    {
      "tag": "csj",
      "adspotId": "穿山甲广告位ID",
      "appId": "穿山甲应用ID",
      "index": 1
    },
    {
      "tag": "ylh",
      "adspotId": "优量汇广告位ID",
      "appId": "优量汇应用ID",
      "index": 2
    }
  ]
}
```

**注：**
为了方便开发者配置流量分发策略，我们也提供了在线可视化的便捷工具[EasyTools](http://easyads.bayescom.cn/)，方便生成广告位的策略配置JSON。


### 2.3 获取广告

以下步骤，为获取广告的必要步骤，**适用于所有广告位**。 不同广告位置的不同实现，可参考[Example示例](https://github.com/bayescom/EasyAds-Android/tree/main/example/src/main/java/com/easyads/demo)；

####  2.3.1 初始化广告对象

初始化广告对象`EasyAdXXX`，以开屏为例

```
EasyAdSplash easySplash = new EasyAdSplash(mActivity, adContainer, listener);
```

####  2.3.2 设置执行策略

调用共有方法`setData(String strategyJson)`，给当前位置广告**赋予执行策略**，即2.2步骤中生成的SDK分发策略配置。

```
easySplash.setData(FileUtil.getJson(mActivity, "splash_config.json"));
```

#### 2.3.3 调用获取广告

方式一：**请求并展示广告**。

```
easySplash.loadAndShow();
```

方式二：先发起请求广告：

```
easySplash.loadOnly();
```

待广告成功拉取后，开发者可根据业务需求在合适的时机，决定调用展示广告方法。(**注意：广告会存在有效期，过久未调用展示，会导致广告失效**)

```
easySplash.show();
```

#### 2.3.4 各广告位集成实现

* [开屏广告：EasyAdSplash](https://github.com/bayescom/EasyAds-Android/wiki/3.1-%E5%BC%80%E5%B1%8F%E5%B9%BF%E5%91%8A)

* [插屏广告：EasyAdInterstitial](https://github.com/bayescom/EasyAds-Android/wiki/3.2-%E6%8F%92%E5%B1%8F%28%E5%BC%B9%E7%AA%97%29%E5%B9%BF%E5%91%8A)

* [横幅广告：EasyAdBanner](https://github.com/bayescom/EasyAds-Android/wiki/3.3-%E6%A8%AA%E5%B9%85%E5%B9%BF%E5%91%8A)

* [原生模板、信息流广告：EasyAdNativeExpress](https://github.com/bayescom/EasyAds-Android/wiki/3.4-%E5%8E%9F%E7%94%9F%E6%A8%A1%E6%9D%BF%E3%80%81%E4%BF%A1%E6%81%AF%E6%B5%81%E5%B9%BF%E5%91%8A)

* [激励视频广告：EasyAdRewardVideo](https://github.com/bayescom/EasyAds-Android/wiki/3.5-%E6%BF%80%E5%8A%B1%E8%A7%86%E9%A2%91%E5%B9%BF%E5%91%8A)

* [全屏视频广告：EasyAdFullScreenVideo](https://github.com/bayescom/EasyAds-Android/wiki/3.6-%E5%85%A8%E5%B1%8F%E8%A7%86%E9%A2%91%E5%B9%BF%E5%91%8A)

* [DRAW视频信息流广告：EasyAdDraw](https://github.com/bayescom/EasyAds-Android/wiki/3.7-DRAW%E8%A7%86%E9%A2%91%E4%BF%A1%E6%81%AF%E6%B5%81%E5%B9%BF%E5%91%8A)

## 3. 进阶设置

### 3.1 SDK全局配置

App多广告位情况下，想要优化SDK的广告位配置，可参考[SDK全局配置](https://github.com/bayescom/EasyAds-Android/wiki/4.-SDK%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AE)优化管理你的众多广告位。


### 3.2 自定义广告SDK渠道

想要使用其他的广告SDK，[自定义广告SDK渠道](https://github.com/bayescom/EasyAds-Android/wiki/5.-%E8%87%AA%E5%AE%9A%E4%B9%89%E5%B9%BF%E5%91%8ASDK%E6%B8%A0%E9%81%93)可以帮你轻松加入任何广告SDK。


## 4. SDK错误码 & 常见问题 

调试遇到问题？欢迎查询SDK错误码及常见问题FAQ，帮助你快速解决对接中遇到的问题。

[SDK错误码](https://github.com/bayescom/EasyAds-Android/wiki/6.1-SDK%E9%94%99%E8%AF%AF%E7%A0%81)

[常见问题](https://github.com/bayescom/EasyAds-Android/wiki/6.2-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)


## 技术支持

QQ群：
<a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=E_IUfzy5PqOteuekOryWlfjZL6AQZuCE&jump_from=webapi"><img border="0" src="https://pub.idqqimg.com/wpa/images/group.png" alt="EasyAds开源社区群" title="EasyAds开源社区群"></a>

QQ群二维码：

![image](https://e.gitee.com/bayescom/repos/bayescom/EasyAds/blob/main/image/easyads_qq.png)

邮件技术支持：<easyads@bayescom.com>