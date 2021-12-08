# 激励视频广告

集成最佳实践：

- 适用场景：激励视频广告在游戏 APP 使用较多，比如通关失败后通过观看激励视频广告，用户可获取复活机会。此外，一些非游戏类 APP，也可通过植入 5-15 秒（甚至更长）的激励视频广告，让用户通过主动观看来享受到一些福利奖励、付费功能/权限等——比如网盘类 APP 通过观看激励视频广告来获取空间；视频、音频、音乐类 APP 通过观看激励视频广告来获取视频/音频/音乐免费观看或收听特权；图片类 APP 通过观看激励视频广告来获取图片下载特权；阅读类 APP 通过观看激励视频广告来获取书籍/漫画阅读特权等。
- 常见尺寸：激励视频广告常见为竖屏，比例 9:16，尺寸 720x1280；也有些 APP 采用横版样式，比例 16:9，尺寸 1280x720。
- 一些建议：广告植入密度不要过于频繁，以免拉低媒体调性和用户好感度；
将广告植入在既能刺激用户主动观看，同时又兼顾不对用户正常使用体验产生影响的位置。
- 激励达成事件请在广告激励回调`onAdReward `中进行，如需服务器回调，需要翻阅个平台中文档说明来了解，本SDK不参与服务端回调功能。
- 支持提前加载广告（注意控制时机，不可过早提前），但要注意，一次展示需对应一个广告实例，切忌复用广告实例。


### 请求广告

加载广告核心方法，详细请参考demo中代码：

```java
public class RewardVideoActivity extends BaseActivity {
    EasyAdRewardVideo rewardVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);

    }

    public void onLoad(View view) {
        rewardVideo = new EasyADController(this).initReward("reward_config.json");
        rewardVideo.loadOnly();

    }

    public void onShow(View view) {
        if (rewardVideo != null) {
            rewardVideo.show();
        } else {
            EasyADController.logAndToast(this, "需要先调用loadOnly()");
        }
    }

    public void loadAndShow(View view) {
        new EasyADController(this).initReward("reward_config.json").loadAndShow();
    }
}
```

EasyADController中相关处理代码：

```java
 /**
     * 加载并展示激励视频广告。
     * 也可以选择性先提前加载，然后在合适的时机再调用展示方法
     */
    public EasyAdRewardVideo initReward(String jsonFileName) {
        //必须：核心事件监听回调
        EARewardVideoListener listener = new EARewardVideoListener() {
            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");
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


            @Override
            public void onVideoCached() {
                logAndToast(mActivity, "广告缓存成功");
            }

            @Override
            public void onVideoComplete() {
                logAndToast(mActivity, "视频播放完毕");
            }

            @Override
            public void onVideoSkip() {

            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdReward() {
                logAndToast(mActivity, "激励发放");
            }

            @Override
            public void onRewardServerInf(EARewardServerCallBackInf inf) {
                //优量汇和穿山甲支持回调服务端激励验证信息，详见RewardServerCallBackInf中字段信息
                logAndToast(mActivity, "onRewardServerInf" + inf);
            }
        };
        //初始化，注意需要时再初始化，不要复用。
        EasyAdRewardVideo easyRewardVideo = new EasyAdRewardVideo(mActivity, listener);
        baseAD = easyRewardVideo;
        //必须：设置策略信息
        easyRewardVideo.setData(getJson(mActivity, jsonFileName));
        return easyRewardVideo;
    }

```


### 主要API

* EasyAdRewardVideo


|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdRewardVideo(Activity activity, EARewardVideoListener listener)|构造方法
|setYlhVolumeOn(boolean vo)|**优量汇特有设置**，是否开启声音，默认false不开启
|setYlhSSVO(ServerSideVerificationOptions options)|**优量汇特有设置**，设置服务器检验选项，具体参考优量汇官方说明文档
|setCsjOrientation(int csjOrientation)|**穿山甲特有设置**，设置广告展示的屏幕方向，默认竖屏
|setCsjMediaExtra(String mediaExtra)|**穿山甲特有设置**，媒体补充信息，用于和服务端检验使用
|setCsjUserId(String userId)|**穿山甲特有设置**，用户id
|setCsjRewardName(String rewardName)| **穿山甲特有设置**，穿山甲奖励名称
|setCsjRewardAmount(int rewardAmount) |**穿山甲特有设置**，穿山甲奖励数量
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告

* EARewardVideoListener

|方法名 | 方法介绍
|:------------- |:---------------|  
|onVideoCached()|视频缓存成功
|onVideoComplete()|视频播放完毕
|onVideoSkip()|视频跳过，部分SDK会回调此方法
|onAdReward()|广告激励达成     
|onRewardServerInf(EARewardServerCallBackInf inf)|激励视频返回的服务器回调信息，穿山甲一直支持，优量汇自v4.330.1200 开始支持,百度9.13开始支持
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 
