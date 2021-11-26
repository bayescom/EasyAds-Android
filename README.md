# EasyAds 

APP开发者一般不会在广告集成上进行过多得探索，各种第三方服务又玩法五花八门，如何能使得APP即自由又放心得管理广告SDK？

EasyAds可以让APP开发者在利用广告变现时，做到高效、透明、简洁、安全，真正属于你自己的聚合广告SDK。


### 概述

EasyAds 目前支持以下广告类型，您可以根据开发需要选择合适的广告，不在支持列表的SDK，您也可以通过**自定义SDK渠道**，来统一管理。

| SDK平台| 开屏 | 激励视频 | 横幅| 插屏 | 模板信息流 | 全屏视频 | draw信息流
|:-------------|:---------------|  :---------------|  :---------------|  :---------------|  :---------------|  :---------------|   :---------------|  
| 穿山甲|✅ |✅ |✅ |✅|✅|✅  |✅
| 优量汇(广点通)  |✅ |✅ |✅ |✅ |✅ |✅ |❌
| 百度 |✅ |✅ |✅ | ✅ | ✅ | ✅ |❌
| 快手 |✅ |✅ | ❌ | ✅ | ✅ | ✅|✅


## 开始使用

### 1.引入SDK

建议使用Gradle 添加依赖，在项目根目录的build.gradle 文件中添加 `JitPack` 仓库

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

```

### 2.获取广告


####  2.1 获取广告的必要步骤：

以下步骤，**适用于所有广告位**，具体广告位置实现，请参考example目录中示例代码。

* 步骤1.初始化广告对象`EasyAdXXX`，以开屏为例

```
//adContainer为开屏广告展示的布局容器
EasyAdSplash easySplash = new EasyAdSplash(mActivity, adContainer, listener);
```

* 步骤2.调用共有方法`setData(String strategyJson)`，给当前位置广告**赋予执行策略**，详细配置方法参考下方`2.2策略配置说明`。

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

待广告成功拉取后，或者在合适的时机，由APP自己决定调用展示广告方法：(**注意广告会存在有效期，过久未调用展示，会导致广告失效**)

```
easySplash.show();
```


#### 2.2 策略配置说明


步骤2`setData(String strategyJson) `中的 `strategyJson`参数为SDK的策略配置信息，可通过修改配置内容来实现流量切分、顺序执行、分组等多种需求，对应的字段结构说明，[点击此处了解](docs/strategyJsonFormat.md)。

**为防止json配置异常导致无法执行广告加载，建议您通过以下几种方式来处理**：

* 方式一：通过[此处]()，一键获取到配置json信息，复制后直接使用。

* 方式二：修改`assets`下对应的`XXX_config.json`配置文件，将其中的广告信息和策略替换为自己的内容。




#### 2.3 各广告位详细说明

点击下方信息，了解各个位置包含的的详细配置参数，和实现建议。

* [开屏广告：EasyAdSplash]()

* [插屏广告：EasyAdInterstitial]()

* [横幅广告：EasyAdBanner]()

* [原生模板、信息流广告：EasyAdNativeExpress]()

* [激励视频广告：EasyAdRewardVideo]()

* [全屏视频广告：EasyAdFullScreenVideo]()

* [DRAW视频信息流广告：EasyAdDraw]()


#### 2.4 SDK渠道配置详细说明

个性化配置，满足不同需求场景。

* [全局核心配置类：EasyAds]()

* [百度渠道配置类：EasyBDManager]()

* [穿山甲渠道配置类：EasyCsjManger]()

* [快手渠道配置类：EasyKSManger]()


#### 2.5 自定义支持更多广告SDK渠道

**我能不能把小米广告SDK也集成进来，聚合统一管理？可以。**

如果穿山甲、优量汇、百度、快手这些SDK渠道无法满足您的需求，您可以通过自定义SDK渠道，来拓展需要支持的任意广告SDK。建议参考demo示例工程`custom`文件夹的`HuaWeiSplashAdapter`和`XiaoMiSplashAdapter`代码。

自定义步骤主要分为三步：

第一步：新建自定义支持的渠道类文件，假设命名为`ABCAdapter`，根据其广告类型，如果是开屏（已支持的广告类型），那么需要让`ABCAdapter`继承`EASplashCustomAdapter`开屏自定义基类。此时需要实现父类的下列抽象方法：

```
 /**
     * 抽象基础方法，子类仅关注对应的广告处理逻辑
     */
    //抽象方法，此方法内应执行广告加载方法
    protected abstract void doLoadAD();

    //抽象方法，此方法内应执行广告展示方法（如有）
    protected abstract void doShowAD();

    //抽象方法，此方法内应执行广告销毁操作（如有）
    protected abstract void doDestroy();
```

为保证广告正常执行，有以下注意事项：

1.在`doLoadAD()`方法内执行广告请求方法时，一定要保证广告SDK已经执行过初始化方法了，媒体应用id，可以通过基类的`getAppID()`方法获取到。
2.一定要在广告成功、曝光、点击、失败时，调用基类对应`handleXXX`方法


第二步：将`ABCAdapter`通过EasyAds广告`addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) `方法添加进去。注意`sdkTag`需要和json策略中的`supplier-tag`一致。

第三步：修改策略json，添加自定义的渠道的`supplier`和 `rules`信息




### 3.问题排查

如果广告执行异常，可以通过`EasyAds.setDebug(boolean isDebug, EALogLevel logLevel)`方法，开启debug日志，筛选：`EasyAds-log`，查看错误码和错误信息，通过log信息来判断SDK执行情况。如果没有观测到错误码信息。可以选择启用更高级别的日志配置：`EALogLevel.MAX`。

#### 3.1 错误码

* com.easyads.model.EasyAdError 异常信息类，包含code、msg；各个code含义如下表所示：

|错误码 	  | 错误详情 | 排查方向
|:------------- |:---------------|  :---------------|  
|9901| 数据为空 |广告返回的数据为空，一般为渠道广告无填充。
|9902 | 加载某个SDK渠道时发生异常| 查看logcat下日志输出，寻找具体原因
|9903 |展示某个SDK渠道时发生异常| 查看logcat下日志输出，寻找具体原因
|9904|渲染某个SDK渠道时发生异常| 查看logcat下日志输出，寻找具体原因
|9905|当前策略中无有效的SDK渠道，或渠道信息为空 | 检查聚合策略配置信息，有可能是配错渠道或者没有配置
|9906| 策略调度时发生异常 | 查看logcat下日志输出，及时反馈我们帮助排查
|9907|未获取到策略 | 检查是否遗漏了`setData(String strategyJson)`策略配置，或者配置的json格式有问题导致未成功解析。
|9908|某个SDK渠道启动异常| 查看logcat下日志输出，及时反馈我们帮助排查
|9909|穿山甲渠道SDK超时且不再加载优先级更低的渠道 | 加载超时，检查网络是否正常。仅开屏中会出现，代表开屏广告位中配置了setCsjTimeOutQuit(true)，低频可忽略
|9910 | 穿山甲渠道SDK加载超时 | 加载超时，检查网络是否正常
|9911 | 百度SDK加载失败 | 查看logcat下日志输出，寻找具体原因
|9912 | 快手SDK加载失败，广告位id类型转换异常 | 一般是广告位转换出错，检查配置的快手渠道下广告位id是否为纯数字，非纯数字会引起该异常。
|9913 | 当前activity已被销毁，导致广告无可用载体 | 检查广告展示方法调用时机，有可能广告载体页面已经关闭了，但是又调用了展示方法
|9914 |快手SDK初始化失败 | 查看logcat下日志输出，寻找具体原因，检查广告位id信息配置是否正确
|9915 |广告渲染失败 |可能是网络缓慢导致素材未成功加载，或者页面实现问题，有布局遮挡了广告布局展示
|99_XXX | 某个SDK渠道返回的错误，其中XXX为该SDK渠道返回的具体错误码 | 翻阅各个平台下的错误码说明查找具体原因： <br/>穿山甲： [查看官方线上文档，点击跳转](https://www.pangle.cn/union/media/union/download/detail?id=4&docId=5de8d9b925b16b00113af0ed&osType=android)<br/>优量汇(广点通)： [查看官方线上文档，点击跳转](https://developers.adnet.qq.com/doc/android/union/union_debug#sdk%20%E9%94%99%E8%AF%AF%E7%A0%81) <br/> [查看官方线上文档，点击跳转；错误码详见19.2](https://union.baidu.com/miniappblog/2020/06/16/AndroidSDK/) <br/>快手： [查看官方线上文档，查看详细错误信息](https://static.yximgs.com/udata/pkg/KS-Android-KSAdSDk/doc/4701b963d40a77bc0f45fd71d30b57da.pdf)



#### 3.2 常见问题FAQ

[点击查看常见问题FAQ](docs/faq.md)

#### 3.3 联系我们


### 4.版本更新记录

|版本|日期|更新内容
|:------|:------|:-----|
|v1.0 |   |SDK发布，主要支持内容：<br/>1.统一聚合穿山甲、优量汇、百度、快手广告SDK<br/>2.支持本地导入策略配置，极简调用<br/>3.支持自定义支持更多广告SDK

