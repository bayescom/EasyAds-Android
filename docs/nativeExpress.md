# 原生模板广告

### 集成最佳实践：

- 适用场景：适用于有内容输出的 APP，比如新闻资讯类、视频类、音频类、社区类等，通常在内容流中或内容详情页中植入。在内容流中植入广告，需对广告展现位置、展现频次等反复测试，避免过度干扰的同时，确保高可见性和效果性。
- 建议广告承载布局高度自适应，如要设置固定**高度须知**：穿山甲模板有最低高度限制，已知为最低125dp，Mercury最低80dp，优量汇(广点通)最低约100dp。
- **瀑布流式内容信息流需单独设置广告宽度**，否则广点通可能展示异常，需增加如下设置代码：

```java
//需要特别注意广告父布局宽度要和自定义的宽度dp值一致，否则优量汇可能出现展示问题。
advanceNativeExpress.setExpressViewAcceptedSize(自定义宽度dp值, 0);
```
 
- 支持提前加载广告（注意**控制时机**，不可过早提前），但要注意，一次展示需对应一个广告实例，切忌复用广告实例。
- RecyclerView中使用的话请参考示例工程中的**NativeExpressRecyclerViewActivity**，建议在列表中每5-10个条目展示一条广告，且**每次只请求一条广告**，如果一定要请求多条请注意穿山甲支持的条数上限为3条。
- 如果在**LstView中**使用，需要同一位置广告不可重复请求，当在**getView**方法中执行广告加载方法时，需标记同一位置广告**是否已经请求过**，如果已请求过不再进行请求步骤，否则会出现重复无效请求导致页面闪烁。
- 列表数据刷新后，也需要**同步刷新广告**。


### 请求广告
请求广告核心方法，详细请参考demo中代码：

```java
public class NativeExpressActivity extends BaseActivity {
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_express);
        container = findViewById(R.id.native_express_container);

        new EasyADController(this).loadNativeExpress("native_config.json",container);
    }

    public void loadNEAD(View view) {
        new EasyADController(this).loadNativeExpress("native_config.json",container);
    }
}
```

EasyADController中相关处理代码：

```java
    public boolean hasNativeShow = false;
    boolean isNativeLoading = false;

    /**
     * 加载并展示原生模板信息流广告
     *
     * @param adContainer 广告的承载布局
     */
    public void loadNativeExpress(String jsonFileName, ViewGroup adContainer) {

        if (hasNativeShow) {//同一位置广告，已展示过不再重复发起请求
            EALog.d("loadNativeExpress hasNativeShow");
            return;
        }

        if (isNativeLoading) {//同一位置广告，正在请求中，不再重复请求
            EALog.d("loadNativeExpress isNativeLoading");
            return;
        }
        isNativeLoading = true;

        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }


        //推荐：核心事件监听回调
        EANativeExpressListener listener = new EANativeExpressListener() {
            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");

            }

            @Override
            public void onAdRenderSuccess() {
                logAndToast(mActivity, "广告渲染成功");

            }


            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdExposure() {
                hasNativeShow = true;
                isNativeLoading = false;
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                isNativeLoading = false;
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }


            @Override
            public void onAdRenderFailed() {
                isNativeLoading = false;
                logAndToast(mActivity, "广告渲染失败");
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }

        };
        //初始化
        final EasyAdNativeExpress easyNativeExpress = new EasyAdNativeExpress(mActivity, listener);
        baseAD = easyNativeExpress;
        easyNativeExpress.setAdContainer(adContainer);
        //必须：设置策略信息
        easyNativeExpress.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easyNativeExpress.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }
```



### 主要API

* EasyAdNativeExpress


|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdNativeExpress(Activity activity, EANativeExpressListener listener)|构造方法
|setAdContainer(ViewGroup container)|设置广告展示承载布局
|setVideoMute(boolean mute)|设置视频静音，默认true，仅优量汇生效，穿山甲在后台中配置
|setYlhMaxVideoDuration(int ylhMaxVideoDuration)|**优量汇特有设置**，设置视频最大时长，单位秒
|View getExpressADView()| 获取广告的实际展示view信息，数据来源于广告SDK回调
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告

* EANativeExpressListener


|方法名 | 方法介绍
|:------------- |:---------------|  
|onAdRenderFailed | 广告渲染失败
|onAdRenderSuccess| 广告渲染成功
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 