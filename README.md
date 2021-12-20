# EasyAds-Android 快速指引

## 1. 支持的SDK平台及广告位

| SDK平台 | 开屏 | 激励视频 | 横幅 | 插屏(弹窗) | 模板信息流 | 全屏视频 | draw信息流 |
|-------|---|---|---|---|---|---|---| 
| 穿山甲   | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 广点通   | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| 百青藤   | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| 快手    | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ |
 

## 2. 快速接入

下面介绍EasyAds的快速接入方法，开发中也可以参考[Example](https://github.com/bayescom/EasyAds-Android/tree/main/example)
下的示例工程，快速了解。

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
！！！！！这里需要添加！！！！
```

**方式二**：

将`EasyAds`项目以`Library`的形式导入APP项目工程。


### 2.2 SDK分发策略配置

配置SDK的分发策略，可方便的实现流量的切分操作，实现多SDK的混合执行策略。

在EasyAds中，我们通过JSON文件的方式配置SDK的分发策略，开发者可根据自身流量分发的需求，按照EasyAds中提供的JSON配置格式及方法设置流量分发策略。

以开屏广告对接穿山甲和优量汇两个SDK为例，配置选择80%流量穿山甲->优量汇的顺序请求，20%流量优量汇->穿山甲的顺序请求，配置如下所示。

其中，suppliers字段下配置媒体在穿山甲和优量汇平台申请的广告代码位信息，rules字段下配置流量分发策略及比例；

策略JSON的配置说明详细见：[SDK策略配置JSON说明](https://github.com/bayescom/EasyAds-Android/wiki/%E7%AD%96%E7%95%A5Json%E9%85%8D%E7%BD%AE%E8%AF%B4%E6%98%8E)

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

<font color=red>**注：**</font>

为了方便开发者配置流量分发策略，我们也提供了在线可视化的便捷工具[EasyTools](http://easyads.bayescom.cn/)，方便生成广告位的策略配置JSON。



### 2.3 获取广告

以下步骤，为获取广告的必要步骤，**适用于所有广告位**。

不同广告位置的不同实现，请参考example示例或者下方2.3部分说明。

####  2.2.1 初始化广告对象

初始化广告对象`EasyAdXXX`，以开屏为例

```
EasyAdSplash easySplash = new EasyAdSplash(mActivity, adContainer, listener);
```

####  2.2.2 初始化广告对象

调用共有方法`setData(String strategyJson)`，给当前位置广告**赋予执行策略**，详细配置说明参考下方`2.2策略配置说明`。

```
easySplash.setData(FileUtil.getJson(mActivity, "splash_config.json"));
```

* 步骤3.调用广告获取方法:

方式一：**请求并展示广告**。

```
easySplash.loadAndShow();
```

方式二：先发起请求广告：

```
easySplash.loadOnly();
```

待广告成功拉取后，或者在合适的时机，由APP自己决定调用展示广告方法：(**注意：广告会存在有效期，过久未调用展示，会导致广告失效**)

```
easySplash.show();
```

#### 2.3 各广告位详细说明

点击下方信息，了解各个位置包含的的详细配置参数，和实现建议。

* [开屏广告：EasyAdSplash](开屏广告)

* [插屏广告：EasyAdInterstitial](插屏广告)

* [横幅广告：EasyAdBanner](横幅广告)

* [原生模板、信息流广告：EasyAdNativeExpress](原生模板、信息流广告)

* [激励视频广告：EasyAdRewardVideo](激励视频广告)

* [全屏视频广告：EasyAdFullScreenVideo](全屏视频广告)

* [DRAW视频信息流广告：EasyAdDraw](DRAW视频信息流广告)


## 技术支持

QQ群：
<a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=E_IUfzy5PqOteuekOryWlfjZL6AQZuCE&jump_from=webapi"><img border="0" src="https://pub.idqqimg.com/wpa/images/group.png" alt="EasyAds开源社区群" title="EasyAds开源社区群"></a>

QQ群二维码：

![image](./docs/image/easyads_qq.png)

邮件技术支持：<easyads@bayescom.com>