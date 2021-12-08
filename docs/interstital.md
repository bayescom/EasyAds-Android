# 插屏(弹窗)广告

集成最佳实践：

- 适用场景：一般暂停时（多见于视频类 APP 节目暂停）；页面间跳转、切换时（比如游戏类 APP 过关、工具类 APP 页面转换）；APP 退出时；返回上一级时等场景，均可适当植入插屏广告。
- 常见尺寸：插屏广告尺寸通常等于或大于手机屏幕的一半，多设计 600x600、600x500 等规格。
- 一些建议：不要突然展示广告，最好在用户完成应用内操作后自然而然的植入，比如游戏通关后、视频播放结束后；
无论是插屏广告植入场景，还是展示频次，媒体均需要反复测试，以达到最佳变现效果；
- 插屏支持提前加载广告（注意控制时机，不可过早提前），但要注意，一次展示需对应一个广告实例，切忌复用广告实例。


### 请求广告

请求广告核心方法，详细请参考demo中代码：

```java
public class InterstitialActivity extends BaseActivity {
    EasyAdInterstitial interstitialAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

    }

    public void loadAd(View view) {
        interstitialAD = new EasyADController(this).initInterstitial("interstitial_config.json");
        interstitialAD.loadOnly();
    }

    public void showAd(View view) {
        if (interstitialAD != null) {
            interstitialAD.show();
        } else {
            EasyADController.logAndToast(this, "需要先调用loadOnly()");
        }
    }

    public void loadAndShowAd(View view) {
        new EasyADController(this).initInterstitial("interstitial_config.json").loadAndShow();
    }
}
```

EasyADController中相关处理代码：

```java
/**
     * 初始话插屏广告。
     * 可以选择性先提前加载，然后在合适的时机再调用展示方法
     * 或者直接调用加载并展示广告
     * <p>
     * 注意！！！：穿山甲默认为"新插屏广告"
     */
    public EasyAdInterstitial initInterstitial(String jsonFileName) {
        //必须：核心事件监听回调
        EAInterstitialListener listener = new EAInterstitialListener() {

            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告就绪");
            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }


            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }


            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }
        };
        //初始化
        EasyAdInterstitial easyInterstitial = new EasyAdInterstitial(mActivity, listener);
        baseAD = easyInterstitial;
        //注意：穿山甲默认为"新插屏广告"，如果要使用旧版请打开这条设置
//        easyInterstitial.setCsjNew(false);
        //必须：设置策略信息
        easyInterstitial.setData(getJson(mActivity, jsonFileName));
        return easyInterstitial;
    }
```


### 主要API

* EasyAdInterstitial


|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdInterstitial(Activity activity, EAInterstitialListener listener)|构造方法
|setYlhMediaListener(UnifiedInterstitialMediaListener mediaListener) |**优量汇特有设置**，监听广告资源的运行情况
|setCsjNew(boolean csjNew)|**穿山甲特有设置**，是否为'新插屏广告'，不设置默认true
|setCsjExpressViewAcceptedSize(float expressViewWidth, float expressViewHeight)| **穿山甲特有设置**，期望模板广告view的size,单位dp；注意：参数请按照平台勾选的比例去进行请求。现有1:1，3:2 ，2:3 三种比例可供选择。
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告




* EAInterstitialListener


|方法名 | 方法介绍
|:------------- |:---------------|  
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 