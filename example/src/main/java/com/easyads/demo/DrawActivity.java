package com.easyads.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.easyads.demo.draw.FullScreenVideoView;
import com.easyads.demo.draw.OnViewPagerListener;
import com.easyads.demo.draw.ViewPagerLayoutManager;
import com.easyads.demo.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DrawActivity extends BaseActivity {
    RecyclerView mRecyclerView;
    private LinearLayout mBottomLayout;
    private RelativeLayout mTopLayout;

    private static final int TYPE_COMMON_ITEM = 1;
    private static final int TYPE_AD_ITEM = 2;

    private int[] imgs = {R.mipmap.video11, R.mipmap.video12, R.mipmap.video13, R.mipmap.video14, R.mipmap.img_video_2};
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};

    private ViewPagerLayoutManager mLayoutManager;
    private DrawRecyclerAdapter mRecyclerAdapter;
    private List<TestItem> mDrawList = new ArrayList<>();
    private static final String TAG = "DrawActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Throwable ignore) {
        }
        setContentView(R.layout.activity_draw);


        mRecyclerView = findViewById(R.id.recycler);
        mBottomLayout = findViewById(R.id.bottom);
        mTopLayout = findViewById(R.id.top);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL, false);
        //设置正常视频数据
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) { //隔一个加载一个广告
                TestItem item = new TestItem(TYPE_AD_ITEM);
                item.ad = new EasyADController(DrawActivity.this);
                mDrawList.add(item);
                continue;
            }
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;

            TestItem item = new TestItem(TYPE_COMMON_ITEM);
            item.videoId = videos[index];
            item.imgId = imgs[index];
            mDrawList.add(item);
        }
        mRecyclerAdapter = new DrawRecyclerAdapter(this, mDrawList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        initListener();

    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                Log.d(TAG, "初始化完成");

                if (mDrawList != null && mDrawList.size() > 0) {
                    boolean isNormalVideo = mDrawList.get(0).isNormal();
                    if (isNormalVideo) {
                        playVideo();
                    }
                    changeBottomTopLayoutVisibility(isNormalVideo);
                }
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                if (mDrawList != null && mDrawList.size() > 0) {
                    boolean isNormalVideo = mDrawList.get(position).isNormal();
                    if (isNormalVideo) {
                        int index = isNext ? 0 : 1;
                        releaseVideo(index);
                    }


                }

            }

            @Override
            public void onPageSelected(final int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                if (mDrawList != null && mDrawList.size() > 0) {
                    boolean isNormalVideo = mDrawList.get(position).isNormal();
                    if (isNormalVideo) {
                        playVideo();
                    }
                    changeBottomTopLayoutVisibility(isNormalVideo);
                }
            }


        });
    }

    private void playVideo() {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(0);
        if (itemView == null) {
            return;
        }
        final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
        final View view = videoLayout.getChildAt(0);
        if (view == null || !(view instanceof VideoView)) {
            return;
        }
        final VideoView videoView = (VideoView) view;
        final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        videoView.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    mediaPlayer[0] = mp;
                    Log.e(TAG, "onInfo");
                    mp.setLooping(true);
                    imgThumb.animate().alpha(0).setDuration(200).start();
                    if (mp != null && videoView != null) {
                        //获取视频资源的宽度
                        int mVideoWidth = mp.getVideoWidth();
                        //获取视频资源的高度
                        int mVideoHeight = mp.getVideoHeight();
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoView.getLayoutParams();
                        if (mVideoWidth > 0 && mVideoHeight > 0 && layoutParams != null) {
                            int[] size = UIUtils.getScreenSize(DrawActivity.this.getApplicationContext());
                            layoutParams.width = mVideoWidth * size[1] / mVideoHeight;
                            layoutParams.height = size[1];
                            layoutParams.leftMargin = -(layoutParams.width - size[0]) / 2;
                            videoView.setLayoutParams(layoutParams);
                        }
                    }
                    return false;
                }
            });
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e(TAG, "onPrepared");

            }
        });


        imgPlay.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    Log.e(TAG, "isPlaying:" + videoView.isPlaying());
                    imgPlay.animate().alpha(1f).start();
                    videoView.pause();
                    isPlaying = false;
                } else {
                    Log.e(TAG, "isPlaying:" + videoView.isPlaying());
                    imgPlay.animate().alpha(0f).start();
                    videoView.start();
                    isPlaying = true;
                }
            }
        });
    }

    private void releaseVideo(int index) {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(index);
        if (itemView != null) {
            final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
            if (videoLayout == null) return;
            View view = videoLayout.getChildAt(0);
            if (view instanceof VideoView) {
                final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
                final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
                final ImageView imgPlay = itemView.findViewById(R.id.img_play);
                videoView.stopPlayback();
                imgThumb.animate().alpha(1).start();
                imgPlay.animate().alpha(0f).start();
            }
        }
    }

    private void changeBottomTopLayoutVisibility(boolean visibility) {
        mBottomLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mTopLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }


    public class DrawRecyclerAdapter extends RecyclerView.Adapter<DrawRecyclerAdapter.ViewHolder> {
        private Context mContext;
        private List<TestItem> mDataList;

        DrawRecyclerAdapter(Context context, List<TestItem> dataList) {
            this.mContext = context;
            this.mDataList = dataList;
        }

        @NonNull
        @Override
        public DrawRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DrawRecyclerAdapter.ViewHolder holder, int position) {
            View view = new View(mContext);
            TestItem item = null;
            if (mDataList != null) {
                item = mDataList.get(position);
                if (item.type == TYPE_COMMON_ITEM) {
                    holder.img_thumb.setImageResource(item.imgId);
                    view = getView();
                    ((VideoView) view).setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + item.videoId));
                    holder.videoLayout.removeAllViews();
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    holder.videoLayout.addView(view);

                } else if (item.type == TYPE_AD_ITEM && item.ad != null) {
                    //请求并展示draw信息流广告
                    item.ad.loadDraw("draw_config.json",holder.videoLayout);
                }
            }

            if (item != null) {
                changeUIVisibility(holder, item.type);
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType--" + position);

            return mDataList.get(position).type;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_thumb;
            CircleImageView img_head_icon;
            ImageView img_play;
            RelativeLayout rootView;
            FrameLayout videoLayout;
            LinearLayout verticalIconLauout;

            public ViewHolder(View itemView) {
                super(itemView);
                img_thumb = itemView.findViewById(R.id.img_thumb);
                videoLayout = itemView.findViewById(R.id.video_layout);
                img_play = itemView.findViewById(R.id.img_play);
                rootView = itemView.findViewById(R.id.root_view);
                verticalIconLauout = itemView.findViewById(R.id.vertical_icon);
                img_head_icon = itemView.findViewById(R.id.head_icon);

            }
        }

    }

    private View getView() {
        FullScreenVideoView videoView = new FullScreenVideoView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        return videoView;
    }

    private void changeUIVisibility(DrawRecyclerAdapter.ViewHolder holder, int type) {
        boolean visibilable = true;
        if (type == TYPE_AD_ITEM) {
            visibilable = false;
        }
        Log.d(TAG, "是否展示：visibilable=" + visibilable);
        holder.img_play.setVisibility(visibilable ? View.VISIBLE : View.GONE);
        holder.img_thumb.setVisibility(visibilable ? View.VISIBLE : View.GONE);

    }

    private static class TestItem {

        public int type = 0;
        public EasyADController ad;
        public int videoId;
        public int imgId;


        public TestItem(int type) {
            this.type = type;
        }


        boolean isNormal() {
            return type == TYPE_COMMON_ITEM;
        }
    }
}
