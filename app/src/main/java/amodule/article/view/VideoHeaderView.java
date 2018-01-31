package amodule.article.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.CleanVideoPlayer;
import com.xiangha.R;
import com.shuyu.gsyvideoplayer.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.Map;

import acore.logic.AppCommon;
import acore.logic.XHClick;
import acore.override.XHApplication;
import acore.override.helper.XHActivityManager;
import acore.tools.FileManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import amodule.dish.view.DishHeaderViewNew;
import amodule.dish.view.DishVideoImageView;
import amodule.dish.view.VideoDredgeVipView;
import aplug.basic.LoadImage;
import aplug.basic.SubBitmapTarget;
import third.ad.scrollerAd.XHAllAdControl;
import third.ad.tools.AdPlayIdConfig;
import third.video.AdVideoController;
import third.video.VideoPlayerController;

import static third.ad.scrollerAd.XHScrollerAdParent.ADKEY_GDT;
import static third.ad.scrollerAd.XHScrollerAdParent.ID_AD_ICON_GDT;

/**
 * PackageName : amodule.article.view
 * Created by MrTrying on 2017/6/20 11:55.
 * E_mail : ztanzeyu@gmail.com
 */

public class VideoHeaderView extends RelativeLayout {
    //转码状态1-未转，2-正转，3-已转
//    private final static String STATUS_UNTRANSCOD = "1";
//    private final static String STATUS_TRANSCODING = "2";
    private final static String STATUS_TRANSCODED = "3";
    private Activity activity;
    private Context context;

    private RelativeLayout dishVidioLayout,adParentLayout,ad_type_video;
    private FrameLayout adLayout;

    private VideoPlayerController mVideoPlayerController = null;//视频控制器
    private DishHeaderViewNew.DishHeaderVideoCallBack callBack;

    private Map<String, String> mapAd;//广告数据
    private boolean isAutoPaly = false;//默认自动播放
    private boolean isOnResuming = false;//默认自动播放
    private XHAllAdControl xhAllAdControl;
    private int num = 4;
    private String status = "1";
    protected View view_Tip;
    public boolean isNetworkDisconnect = false;

    public VideoHeaderView(Context context) {
        super(context);
        inflateView();
    }

    public VideoHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView();
    }

    public VideoHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView();
    }

    private void inflateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_video_header_oneimage_port, null);
        addView(view);
    }

    public void initView(Activity activity) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        isAutoPaly = "wifi".equals(ToolsDevice.getNetWorkSimpleType(activity));
        //大图处理
        setViewSize(16,9);
        dishVidioLayout = (RelativeLayout) findViewById(R.id.video_layout);
        dredgeVipLayout = (RelativeLayout) findViewById(R.id.video_dredge_vip_layout);
        ad_type_video= (RelativeLayout) findViewById(R.id.ad_type_video);
        adParentLayout = (RelativeLayout) findViewById(R.id.video_ad_layout_parent);
        adParentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 根据宽高设置视频播放器大小
     * @param videoW
     * @param videoH
     */
    public void setViewSize(int videoW,int videoH){
        if(videoW > 0 && videoH > 0){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ToolsDevice.getWindowPx(activity).widthPixels * videoH / videoW);
            setLayoutParams(params);
            requestLayout();
        }
    }

    public void setData(Map<String, String> data, DishHeaderViewNew.DishHeaderVideoCallBack callBack, Map<String, String> detailPermissionMap) {
        if (callBack == null) {
            this.callBack = new DishHeaderViewNew.DishHeaderVideoCallBack() {
                @Override public void videoImageOnClick() { }
                @Override public void getVideoControl(VideoPlayerController mVideoPlayerController, RelativeLayout dishVidioLayout, View view_oneImage) { }
            };
        } else this.callBack = callBack;

        try {
            Map<String, String> videoData = StringManager.getFirstMap(data.get("video"));

            //重新设置视频大小
            float videoW = 0,videoH = 0;
            if(videoData.containsKey("width") && !TextUtils.isEmpty(videoData.get("width"))
                    && videoData.containsKey("height") && !TextUtils.isEmpty(videoData.get("height"))){
                videoW = Integer.parseInt(videoData.get("width"));
                videoH = Integer.parseInt(videoData.get("height"));
                setViewSize((int)videoW,(int)videoH);
            }

            status = videoData.get("status");
            videoData.put("title", data.get("title"));
            Map<String, String> videoUrlData = StringManager.getFirstMap(videoData.get("videoUrl"));
            String url = "";
            if (videoUrlData.isEmpty()) {
                Toast.makeText(getContext(), "视频播放失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (videoUrlData.containsKey("defaultUrl") && !TextUtils.isEmpty(videoUrlData.get("defaultUrl"))) {
                url = videoUrlData.get("defaultUrl");
            }
            videoData.put("url", url);
            setSelfVideo(videoData,detailPermissionMap);

            //设置全屏播放时的横竖屏状态
            mVideoPlayerController.setPortrait(isPortraitVideo(videoW,videoH));
        } catch (Exception e) {
            Toast.makeText(getContext(), "视频播放失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public boolean isPortraitVideo(float videoW,float videoH){
        if(videoW <= 0 || videoH <= 0)
            return false;
        //视频比例大于1：1则为竖屏视频
        return videoW/videoH <= 1/1f;
    }

    private void initAdTypeImg() {
        adLayout = (FrameLayout) findViewById(R.id.video_ad_layout);

        ArrayList<String> list = new ArrayList<>();
        String key = AdPlayIdConfig.ARTICLE_TIEPIAN;
        list.add(key);

        xhAllAdControl = new XHAllAdControl(list, new XHAllAdControl.XHBackIdsDataCallBack() {
            @Override
            public void callBack(Map<String, String> maps) {
                String temp = maps.get(AdPlayIdConfig.ARTICLE_TIEPIAN);
                mapAd = StringManager.getFirstMap(temp);
                if (mapAd != null && mapAd.size() > 0
                        && mVideoPlayerController != null) {
                    mVideoPlayerController.setShowAd(true);
                }
                if (isAutoPaly && mVideoPlayerController != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayerController.setOnClick();
                        }
                    });
                }
            }
        }, activity, "wz_tiepian");

    }

    /**
     * 处理广告展示
     *
     * @param map
     * @param view
     */
    private void setVideoAdData(final Map<String, String> map, final View view) {
        xhAllAdControl.onAdBind(0, view, "");
        final TextView mNum = (TextView) view.findViewById(R.id.ad_gdt_video_num);
        final ImageView mImageView = (ImageView) view.findViewById(R.id.ad_video_img);
        String imgUrl = null;
        if (map.containsKey("imgUrl")) imgUrl = map.get("imgUrl");
        if (TextUtils.isEmpty(imgUrl)) return;

        View gdtIcon = view.findViewById(ID_AD_ICON_GDT);
        if(gdtIcon != null){
            gdtIcon.setVisibility(ADKEY_GDT.equals(map.get("type"))?VISIBLE:GONE);
        }

        view.findViewById(R.id.ad_gdt_video_hint_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                adParentLayout.setVisibility(GONE);
                mVideoPlayerController.setShowAd(false);
                mVideoPlayerController.setOnClick();
            }
        });
        view.findViewById(R.id.ad_vip_lead).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCommon.openUrl(activity, StringManager.getVipUrl(true) + "&vipFrom=视频贴片广告会员免广告", true);
            }
        });

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                xhAllAdControl.onAdClick(0, "");
            }
        });
        //初始化倒计时
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mNum.setText("" + msg.what);
                if (msg.what == 0) {
                    view.setVisibility(View.GONE);
                    adParentLayout.setVisibility(GONE);
                    if (!mVideoPlayerController.isPlaying()) {
                        mVideoPlayerController.setShowAd(false);
                        if (isOnResuming)
                            mVideoPlayerController.setOnClick();
                    }
                }
            }
        };
        BitmapRequestBuilder<GlideUrl, Bitmap> bitmapRequest = LoadImage.with(XHApplication.in())
                .load(imgUrl)
                .setRequestListener(new RequestListener<GlideUrl, Bitmap>() {
                    @Override
                    public boolean onResourceReady(Bitmap arg0, GlideUrl arg1, Target<Bitmap> arg2, boolean arg3, boolean arg4) {
                        return false;
                    }

                    @Override
                    public boolean onException(Exception arg0, GlideUrl arg1, Target<Bitmap> arg2, boolean arg3) {
                        mImageView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .build();
        if (bitmapRequest != null)
            bitmapRequest.into(new SubBitmapTarget() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                    view.setVisibility(View.VISIBLE);
                    adParentLayout.setVisibility(VISIBLE);
                    mImageView.setVisibility(View.VISIBLE);
                    bitmap.getHeight();
                    mImageView.setImageBitmap(bitmap);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (; num > 0; num--) {
                                handler.sendEmptyMessage(num);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }).start();
                }
            });
    }

    private boolean setSelfVideo(final Map<String, String> selfVideoMap, Map<String, String> permissionMap) {
        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_WIDTH);
        boolean isUrlVaild = false;
        String videoUrl = selfVideoMap.get("url");
        String img = selfVideoMap.get("videoImg");
        if (!TextUtils.isEmpty(videoUrl)
                && videoUrl.startsWith("http")) {
            if(null == mVideoPlayerController){
                mVideoPlayerController = new VideoPlayerController(activity, dishVidioLayout, img,GSYVideoType.SCREEN_MATCH_WIDTH);
                GSYVideoManager.canChange = false;
            }

            if(permissionMap != null && permissionMap.containsKey("video")){
                Map<String,String> videoPermionMap = StringManager.getFirstMap(permissionMap.get("video"));
                Map<String,String> commonMap = StringManager.getFirstMap(videoPermionMap.get("common"));
                Map<String,String> timeMap = StringManager.getFirstMap(videoPermionMap.get("fields"));
                if(!TextUtils.isEmpty(timeMap.get("time"))){
                    limitTime = Integer.parseInt(timeMap.get("time"));
                    setVipPermision(commonMap);
                }
            }else{
                isContinue = true;
                isHaspause = false;
                dredgeVipLayout.setVisibility(GONE);
            }

            DishVideoImageView dishVideoImageView = new DishVideoImageView(activity);
            dishVideoImageView.setImageScaleType(img,"",ImageView.ScaleType.CENTER_INSIDE);

            mVideoPlayerController.setNewView(dishVideoImageView);
            mVideoPlayerController.initVideoView2(videoUrl, selfVideoMap.get("title"), dishVideoImageView);
            mVideoPlayerController.setStatisticsPlayCountCallback(new VideoPlayerController.StatisticsPlayCountCallback() {
                @Override
                public void onStatistics() {
                    XHClick.mapStat(getContext(), "a_ShortVideoDetail", "视频内容", "播放视频");
                    callBack.videoImageOnClick();
                }
            });
            //被点击回调
            mVideoPlayerController.setMediaViewCallBack(new VideoPlayerController.MediaViewCallBack() {
                @Override
                public void onclick() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setVideoAdData(mapAd, adLayout);
                        }
                    });
                }
            });
            callBack.getVideoControl(mVideoPlayerController, dishVidioLayout, this);
            callBack.videoImageOnClick();
            //转码未完成，重新设置点击
            if (!STATUS_TRANSCODED.equals(status)) {
                dishVideoImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tools.showToast(getContext(), "视频转码中，请稍后再试");
                    }
                });
            }
            isUrlVaild = true;
        }
        if (STATUS_TRANSCODED.equals(status))
            initVideoAd();
        return isUrlVaild;
    }
    private void initVideoAd(){
        if(!initAdTypeVideo()){
            initAdTypeImg();
        }
    }
    private AdVideoController adVideoController;
    private boolean initAdTypeVideo(){
        adVideoController= new AdVideoController(activity);
        Log.i("xianghaTag","initAdTypeVideo:::"+adVideoController.isAvailable()+"::"+(adVideoController.getAdVideoPlayer()!=null));
        if(adVideoController.isAvailable()&&adVideoController.getAdVideoPlayer()!=null){
            ad_type_video.addView(adVideoController.getAdVideoPlayer());
            ad_type_video.setVisibility(VISIBLE);
            adParentLayout.setVisibility(VISIBLE);
            mVideoPlayerController.setShowAd(true);
            handleTypeVideoCallBack();
            return true;
        }else{
            return false;
        }
    }
    private void handleTypeVideoCallBack(){
        if (isAutoPaly && mVideoPlayerController != null && isShowActivity()) {
            adParentLayout.setVisibility(VISIBLE);
            mVideoPlayerController.setShowAd(true);
            if(!ToolsDevice.getNetActiveState(context)){//无网络
                return;
            }
            adVideoController.start();
        }
        adVideoController.setOnStartCallback(new AdVideoController.OnStartCallback() {
            @Override
            public void onStart(boolean isRemoteUrl) {
                ad_type_video.setVisibility(View.VISIBLE);
                if(isRemoteUrl){//远程链接
                    if(ToolsDevice.getNetActiveState(context)) {
                        int netType = ToolsDevice.getNetWorkSimpleNum(context);
                        if(netType>1 &&
                                !"1".equals(FileManager.loadShared(context,FileManager.SHOW_NO_WIFI,FileManager.SHOW_NO_WIFI).toString())){
                            removeTipView();
                            if(view_Tip==null){
                                initNoWIFIView(context);
                                ad_type_video.addView(view_Tip);
                            }
                            adVideoController.onPause();
                        }
                    }else{
                        removeTipView();
                        if(view_Tip==null){
                            initNoNetwork(context);
                            ad_type_video.addView(view_Tip);
                        }
                        adVideoController.onPause();
                    }
                }
            }
        });
        adVideoController.setOnCompleteCallback(new AdVideoController.OnCompleteCallback() {
            @Override
            public void onComplete() {
                if(ad_type_video!=null) {
                    ad_type_video.removeAllViews();
                    ad_type_video.setVisibility(View.GONE);
                }
                adParentLayout.setVisibility(GONE);
                if(mVideoPlayerController!=null){
                    mVideoPlayerController.setShowAd(false);
                    mVideoPlayerController.setOnClick();
                }
            }
        });
        adVideoController.setOnErrorCallback(new AdVideoController.OnErrorCallback() {
            @Override
            public void onError() {
                if(ad_type_video!=null){
                    ad_type_video.removeAllViews();
                    ad_type_video.setVisibility(View.GONE);
                }
                adParentLayout.setVisibility(GONE);
                if(mVideoPlayerController!=null){
                    mVideoPlayerController.setShowAd(false);
                    mVideoPlayerController.setOnClick();
                }
            }
        });
        adVideoController.setNetworkNotifyListener(new CleanVideoPlayer.NetworkNotifyListener() {
            @Override
            public void wifiConnected() {
                removeTipView();
                adVideoController.onResume();
                isNetworkDisconnect = false;
            }
            @Override
            public void mobileConnected() {
                if(!"1".equals(FileManager.loadShared(context,FileManager.SHOW_NO_WIFI,FileManager.SHOW_NO_WIFI).toString())){
                    if(!isNetworkDisconnect){
                        removeTipView();
                        if(view_Tip==null){
                            initNoWIFIView(context);
                            ad_type_video.addView(view_Tip);
                        }
                        adVideoController.onPause();
                    }
                }else if(adVideoController.getAdVideoPlayer().getCurrentState() == GSYVideoPlayer.CURRENT_STATE_PAUSE){
                    removeTipView();
                    adVideoController.onResume();
                }
                isNetworkDisconnect = false;
            }
            @Override
            public void nothingConnected() {
                removeTipView();
                if(view_Tip == null){
                    initNoNetwork(context);
                    ad_type_video.addView(view_Tip);
                }
                adVideoController.onPause();
                isNetworkDisconnect = true;
            }
        });
    }
    @SuppressLint({"SetTextI18n", "InflateParams"})
    protected void initNoWIFIView(Context context){
        ViewGroup.LayoutParams layoutParams= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view_Tip=LayoutInflater.from(context).inflate(R.layout.tip_layout,null);
        view_Tip.setLayoutParams(layoutParams);
        TextView tipMessage= (TextView) view_Tip.findViewById(R.id.tipMessage);
        tipMessage.setText("现在是非WIFI，看视频要花费流量了");
        Button btnCloseTip = (Button) view_Tip.findViewById(R.id.btnCloseTip);
        btnCloseTip.setText("继续播放");
        view_Tip.findViewById(R.id.tipLayout).setOnClickListener(onClickListener);
        view_Tip.findViewById(R.id.btnCloseTip).setOnClickListener(onClickListener);
    }

    protected void initNoNetwork(Context context){
        ViewGroup.LayoutParams layoutParams= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view_Tip=LayoutInflater.from(context).inflate(R.layout.tip_layout,null);
        view_Tip.setLayoutParams(layoutParams);
        TextView tipMessage= (TextView) view_Tip.findViewById(R.id.tipMessage);
        tipMessage.setText("网络未连接，请检查网络设置");
        Button btnCloseTip = (Button) view_Tip.findViewById(R.id.btnCloseTip);
        btnCloseTip.setText("去设置");
        view_Tip.findViewById(R.id.tipLayout).setOnClickListener(disconnectClick);
        view_Tip.findViewById(R.id.btnCloseTip).setOnClickListener(disconnectClick);
    }
    private OnClickListener disconnectClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    };
    private OnClickListener onClickListener= new OnClickListener() {
        @Override
        public void onClick(View v) {
            removeTipView();
            adVideoController.onResume();
            new Thread(() -> FileManager.saveShared(context,FileManager.SHOW_NO_WIFI,FileManager.SHOW_NO_WIFI,"1")).start();
        }
    };

    protected void removeTipView(){
        if(view_Tip!=null){
            ad_type_video.removeView(view_Tip);
            view_Tip=null;
        }
    }
    private boolean  isShowActivity(){
        try {
            if ("amodule.article.activity.VideoDetailActivity".equals(XHActivityManager.getInstance().getCurrentActivity().getComponentName().getClassName()))
                return true;
        }catch (Exception e){return false;}
        return false;
    }
    boolean isContinue = false;
    boolean isHaspause = false;
    long currentTime = 0;
    int limitTime = 0;
    private RelativeLayout dredgeVipLayout;
    private VideoDredgeVipView vipView;
    private void setVipPermision(final Map<String, String> common){
        if(StringManager.getBooleanByEqualsValue(common,"isShow")
                ) return;
        final String url = common.get("url");
        if(TextUtils.isEmpty(url)) return;
        vipView = new VideoDredgeVipView(getContext());
        dredgeVipLayout.addView(vipView);
        vipView.setTipMessaText(common.get("text"));
        vipView.setDredgeVipText(common.get("button1"));
        vipView.setDredgeVipClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(url)){
                    AppCommon.openUrl(activity,url,true);
                    return;
                }
            }
        });
        mVideoPlayerController.setOnProgressChangedCallback(new GSYVideoPlayer.OnProgressChangedCallback() {
            @Override
            public void onProgressChanged(int progress, int secProgress, int currentTime, int totalTime) {
                int currentS = Math.round(currentTime / 1000f);
                int durationS = Math.round(totalTime / 1000f);
                if (currentS >= 0 && durationS >= 0) {
                    if (isHaspause) {
                        mVideoPlayerController.onPause();
//                        mVideoPlayerController.onResume();
                        return;
                    }
                    if ((currentS > limitTime
//                            || limitTime > durationS
                    ) && !isContinue) {
                        dredgeVipLayout.setVisibility(VISIBLE);
                        mVideoPlayerController.onPause();
//                        mVideoPlayerController.onResume();
                        isHaspause = true;
                    }
                }
            }
        });
    }

    public void onResume() {
        isOnResuming = true;
        if(mVideoPlayerController != null
                && (dredgeVipLayout == null || dredgeVipLayout.getVisibility() == GONE)){
            mVideoPlayerController.onResume();
        }
    }

    public void onPause() {
        isOnResuming = false;
        if(mVideoPlayerController != null){
            mVideoPlayerController.onPause();
        }
    }

    public void setLoginStatus(){
        if(vipView != null){
            vipView.setLogin();
        }
    }

    public boolean onBackPressed() {
        if(mVideoPlayerController != null){
            return mVideoPlayerController.onBackPressed();
        }
        return false;
    }

    public void onDestroy() {
        if(mVideoPlayerController != null){
            mVideoPlayerController.onDestroy();
        }
    }

    public int getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(int limitTime) {
        this.limitTime = limitTime;
    }
}
