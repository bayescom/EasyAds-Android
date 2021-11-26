# EasyBDManager

百度个性化配置信息API方法介绍

| 参数名| 参数类型|	基本介绍|  
|:------------- |:---------------|:---------------|  
|splashParameters |RequestParameters |开屏位置的个性化请求配置信息，[点此了解配置详情](https://union.baidu.com/miniappblog/2020/12/01/newAndroidSDK/#RequestParameters-Builder)
|nativeExpressParameters  |RequestParameters |信息流个性化请求配置信息，[点此了解配置详情](https://union.baidu.com/miniappblog/2020/12/01/newAndroidSDK/#BaiduNativeManager)|
|nativeExpressSmartStyle | StyleParams | 信息流个性化布局配置信息，[点此了解配置详情](https://union.baidu.com/miniappblog/2020/12/01/newAndroidSDK/#StyleParams) 
| interstitialType  | AdSize | 枚举类型，插屏广告的类型，当设置为视频贴片等广告类型时（AdSize.InterstitialForVideoBeforePlay和AdSize.InterstitialForVideoPausePlay），需配合下方的`interstitialVideoLayout`一起使用。
|interstitialVideoLayout | RelativeLayout | 视频贴片类型的插屏布局父布局，需特定interstitialType下生效
| fullScreenUseSurfaceView |boolean | 全屏视频是否使用SurfaceView来渲染，默认false
|rewardUseSurfaceView |boolean | 激励视频是否使用SurfaceView来渲染，默认false
| rewardDownloadAppConfirmPolicy |  int | 激励视频下载确认弹框设置，默认永不弹框