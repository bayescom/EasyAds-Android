package com.easyads.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 在消息流中接入原生模板广告的示例
 */

public class NativeExpressRecyclerViewActivity extends BaseActivity {

    public static final int MAX_ITEMS = 50;
    public static int FIRST_AD_POSITION = 1; // 第一条广告的位置
    public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告

    private RecyclerView mRecyclerView;
    private List<RycItem> mNormalDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_native_express_recycler_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
    }

    private void initData() {
        for (int i = 0; i < MAX_ITEMS; ++i) {
            mNormalDataList.add(new RycItem("No." + i + " Normal Data"));
        }
        //添加广告的数目，这里定义了3条广告。
        int maxAD = 4;

        for (int i = 0; i < maxAD; i++) {
            int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
            if (position <= mNormalDataList.size()) {
                //将广告的NormalItem定义为默认的空标题item。
                //也可以不同item使用不同的广告位id作为广告标识，这样方便区分不同item的广告数据表现。
                RycItem adItem = new RycItem("");
                //核心步骤1：初始化广告处理类。
                adItem.ad = new EasyADController(this);
                mNormalDataList.add(position, adItem);
            }
        }

        //列表adapter创建，传入要渲染的数据信息
        CustomAdapter mAdapter = new CustomAdapter(this, mNormalDataList);
        mRecyclerView.setAdapter(mAdapter);
        //一定要加上该配置，防止item复用导致广告重复
        mRecyclerView.setItemViewCacheSize(500);
    }


    /**
     * RecyclerView的Adapter
     */
    static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        static final int TYPE_DATA = 0;
        static final int TYPE_AD = 1;
        private List<RycItem> mData;
        Activity mActivity;

        public CustomAdapter(Activity activity, List<RycItem> list) {
            mActivity = activity;
            mData = list;
        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position) {
            //核心步骤2：根据是否包含标题内容，来判断是否为广告item
            return TextUtils.isEmpty(mData.get(position).title) ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder customViewHolder, final int position) {
            int type = getItemViewType(position);
            if (TYPE_AD == type) {
                //核心步骤3：如果是广告布局，执行广告加载
                EasyADController ad = mData.get(position).ad;
                ad.loadNativeExpress("native_config.json", customViewHolder.container);
            } else {
                customViewHolder.title.setText(mData.get(position).getTitle());
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layoutId = (viewType == TYPE_AD) ? R.layout.item_express_ad : R.layout.item_data;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        static class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ViewGroup container; // 广告承载布局

            public CustomViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                container = view.findViewById(R.id.express_ad_container);
            }
        }
    }

    //列表子类
    public static class RycItem {
        private String title;

        public EasyADController ad; // 广告处理类

        public RycItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


}
