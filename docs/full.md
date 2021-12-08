# 全屏视频广告

集成最佳实践：

- 使用场景基本类似激励视频，广告展示样式也和激励视频基本一致，区别是对于用户来说强制性更低，用户可以提前“跳过”广告。
- 支持提前加载广告（注意控制时机，不可过早提前），但要注意，一次展示需对应一个广告实例，切忌复用广告实例。
 

### 请求广告

加载广告核心方法，详细请参考demo中代码：

```java
public class FullScreenVideoActivity extends BaseActivity {
    EasyAdFullScreenVideo fullScreenVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
    }


    public void loadFull(View view) {
        fullScreenVideo = new EasyADController(this).initFullVideo("full_config.json");
        fullScreenVideo.loadOnly();

    }

    public void showFull(View view) {
        if (fullScreenVideo != null) {
            fullScreenVideo.show();
        } else {
            EasyADController.logAndToast(this, "需要先调用loadOnly()");
        }
    }

    public void loadAndShowFull(View view) {
        new EasyADController(this).initFullVideo("full_config.json").loadAndShow();
    }
}
```

EasyADController中相关处理代码：

```java
/**
     * 初始化获取展示全屏视频的广告对象。
     * 也可以选择先提前加载，然后在合适的时机再调用展示方法
     */
    public EasyAdFullScreenVideo initFullVideo(String jsonFileName) {

        //推荐：核心事件监听回调
        EAFullScreenVideoListener listener = new EAFullScreenVideoListener() {
            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");

            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onVideoComplete() {
                logAndToast(mActivity, "视频播放结束");
            }

            @Override
            public void onVideoSkipped() {
                logAndToast(mActivity, "跳过视频");
            }

            @Override
            public void onVideoCached() {
                //广告缓存成功，可以在此记录状态，但要注意：不一定所有的广告会返回该回调
                logAndToast(mActivity, "广告缓存成功");
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
        EasyAdFullScreenVideo easyFullScreenVideo = new EasyAdFullScreenVideo(mActivity, listener);
        baseAD = easyFullScreenVideo;
        //必须：设置策略信息
        easyFullScreenVideo.setData(getJson(mActivity, jsonFileName));

        return easyFullScreenVideo;
    }

```


### 主要API

* EasyAdFullScreenVideo


|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdFullScreenVideo(Activity activity, EAFullScreenVideoListener listener) | 构造方法
|setYlhMediaListener(UnifiedInterstitialMediaListener mediaListener) |**优量汇特有设置**，监听广告资源的运行情况
|setYlhVideoOption(VideoOption videoOption) |**优量汇特有设置**，设置视频播放配置
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告

* EAFullScreenVideoListener

|方法名 | 方法介绍
|:------------- |:---------------|  
|onVideoComplete()|视频播放结束
|onVideoSkipped()|用户点击了跳过，未看完提前关闭了视频
|onVideoCached()|视频缓存完成
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 
