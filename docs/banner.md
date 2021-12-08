# 横幅广告

### 集成最佳实践：

- 适用场景：一般会在 APP 页面的底部或中部呈现。
- 常见尺寸：640*100
- 穿山甲横幅**不支持在列表中展示**，具体表现为，当在列表上下滑动时，原本正常展示的banner广告，会变成灰色纯色色块且无法响应点击。如果希望在列表展示广告，推荐使用模板信息流方式接入，或者SDK渠道中不配置穿山甲渠道。
- 如果banner放在有触摸交互的view中时，请确保非交互情况下`onInterceptTouchEvent`事件返回为`false`，否则会出现广告无法点击问题。
- 若开启了自动刷新，广告所在页面移除时，务必调用广告`destroy()`方法，移除广告资源

### 请求广告

请求广告核心方法，详细请参考demo中代码：

```java
public class BannerActivity extends BaseActivity {
    RelativeLayout rl;
    EasyADController ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        //banner父布局
        rl = findViewById(R.id.banner_layout);

        //初始化广告处理封装类
        ad = new EasyADController(this);
        //加载banner
        ad.loadBanner("banner_config.json", rl);
    }
}
```

EasyADController中相关处理代码：

```java

    /**
     * 加载并展示banner广告
     *
     * @param adContainer banner广告的承载布局
     */
    public void loadBanner(String jsonFileName, ViewGroup adContainer) {
        //必须：核心事件监听回调
        EABannerListener listener = new EABannerListener() {
            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展现");
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
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");
            }

        };
        EasyAdBanner easyAdBanner = new EasyAdBanner(mActivity, adContainer, listener);
        baseAD = easyAdBanner;
        //如果集成穿山甲，这里必须配置，建议尺寸要和穿山甲后台中的"代码位尺寸"宽高比例一致，值单位为dp，这里示例使用的广告位宽高比为640：100。
        int adWidth = ScreenUtil.px2dip(mActivity, ScreenUtil.getScreenWidth(mActivity));
        int adHeight = (int) (((double) adWidth / (double) 640) * 100);
        //如果高度传入0代表自适应高度
        easyAdBanner.setCsjExpressSize(adWidth, adHeight);
        //必须：设置策略信息
        easyAdBanner.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easyAdBanner.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }
```

### 主要API

* EasyAdBanner

|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdBanner(Activity activity, ViewGroup adContainer, EABannerListener listener) | 构造方法，需要传入广告载体布局adContainer
|setRefreshInterval(int refreshInterval) | 设置定时刷新时间，单位为秒，仅优量汇、穿山甲生效，不设置默认不进行定时刷新
|setCsjExpressSize(int width, int height) |设置穿山甲模板尺寸，建议尺寸要和穿山甲后台中的"代码位尺寸"宽高比例一致，值单位为dp
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告

* EABannerListener
 
|方法名 | 方法介绍
|:------------- |:---------------|  
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 