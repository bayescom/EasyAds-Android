# Draw视频信息流广告

集成最佳实践：

- 适用场景：短视频类APP，上划切换时展示广告
- 支持提前加载广告（注意控制时机，不可过早提前），但要注意，一次展示需对应一个广告实例，切忌复用广告实例。

加载广告核心方法，详细请参考demo中代码：

```java
//初始化列表item
TestItem item = new TestItem(TYPE_AD_ITEM);
item.ad = new EasyADController(DrawActivity.this);
mDrawList.add(item);
mRecyclerAdapter = new DrawRecyclerAdapter(this, mDrawList);


//RecycleView得adapter中进行广告展示逻辑
        @Override
        public void onBindViewHolder(@NonNull DrawRecyclerAdapter.ViewHolder holder, int position) {
            View view = new View(mContext);
            TestItem item = null;
            if (mDataList != null) {
                item = mDataList.get(position);
                if (item.type == TYPE_COMMON_ITEM) {//普通draw信息
                    ………………
                } else if (item.type == TYPE_AD_ITEM && item.ad != null) {//广告
                    //请求并展示draw信息流广告
                    item.ad.loadDraw("draw_config.json",holder.videoLayout);
                }
            }

            if (item != null) {
                changeUIVisibility(holder, item.type);
            }
        }
```

EasyADController中相关处理代码：


```java
    public EasyAdDraw easyAdDraw;

    public void loadDraw(String jsonFileName, ViewGroup adContainer) {

        EADrawListener listener = new EADrawListener() {
            @Override
            public void onAdSucceed() {
                if (easyAdDraw != null) {
                    easyAdDraw.show();
                }
                logAndToast(mActivity, "广告加载成功");

            }

            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展示");

            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");

            }

            @Override
            public void onAdClose() { //此位置不存在广告关闭动作，当前回调不会触发
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }
        };
        easyAdDraw = new EasyAdDraw(mActivity, listener);
        baseAD = easyAdDraw;
        easyAdDraw.setAdContainer(adContainer);
        //必须：设置策略信息
        easyAdDraw.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easyAdDraw.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }

```



### 主要API

* EasyAdDraw

|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdDraw(Activity activity, EADrawListener listener) |构造方法
|setAdContainer(final ViewGroup adContainer) | 设置广告承载父布局
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告


* EADrawListener 


|方法名 | 方法介绍
|:------------- |:---------------|  
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 