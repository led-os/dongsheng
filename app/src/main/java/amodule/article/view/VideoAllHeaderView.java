package amodule.article.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xiangha.R;

import java.util.Map;

import acore.override.helper.XHActivityManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import amodule.dish.view.DishHeaderView;
import third.video.VideoPlayerController;

/**
 * PackageName : amodule.article.view
 * Created by MrTrying on 2017/6/19 15:29.
 * E_mail : ztanzeyu@gmail.com
 */

public class VideoAllHeaderView extends LinearLayout {
    private VideoHeaderView videoHeaderView;
    private CustomerView customerView;
    private VideoInfoView videoInfoView;

    public VideoAllHeaderView(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        initView();
    }

    public VideoAllHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(LinearLayout.VERTICAL);
        initView();
    }

    public VideoAllHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(LinearLayout.VERTICAL);
        initView();
    }

    private void initView(){
        videoHeaderView = new VideoHeaderView(getContext());
        videoHeaderView.initView(XHActivityManager.getInstance().getCurrentActivity());
        addView(videoHeaderView);

        videoInfoView = new VideoInfoView(getContext());
        videoInfoView.setType(mCurrType);
        addView(videoInfoView);

        customerView = new CustomerView(getContext());
        customerView.setType(mCurrType);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, Tools.getDimen(getContext(), R.dimen.dp_5),0,Tools.getDimen(getContext(), R.dimen.dp_4));
        addView(customerView,layoutParams);

    }

    public void setData(Map<String, String> mapVideo){
        if(mapVideo == null){
            setVisibility(GONE);
            return;
        }

        setVisibility(VISIBLE);
        videoHeaderView.setData(mapVideo, new DishHeaderView.DishHeaderVideoCallBack() {
            @Override
            public void videoImageOnClick() {

            }

            @Override
            public void getVideoControl(VideoPlayerController mVideoPlayerController, RelativeLayout dishVidioLayout, View view_oneImage) {
                callBack.getVideoPlayerController(mVideoPlayerController);
//                dishVidioLayout=dishVidioLayouts;
//                view_oneImage=view_oneImages;
//                setViewOneState();
//                dishTitleViewControl.setVideoContrl(mVideoPlayerController);
            }
        });
        //设置videoinfo数据
        videoInfoView.setData(mapVideo);
        //设置用户数据
        if (mapVideo.containsKey("customer") && !TextUtils.isEmpty(mapVideo.get("customer"))) {
            Map<String, String> mapUser = StringManager.getFirstMap(mapVideo.get("customer"));
            customerView.setType(mCurrType);
            customerView.setData(mapUser);
        }
    }

    private String mCurrType;
    public void setType(String type) {
        mCurrType = type;
    }

    public void onResume() {
        if(videoHeaderView!=null)
            videoHeaderView.onResume();
    }
    public void onPause() {
        if(videoHeaderView!=null)
            videoHeaderView.onPause();
    }

    private VideoViewCallBack callBack;
    public interface VideoViewCallBack{
        public void getVideoPlayerController(VideoPlayerController mVideoPlayerController);
        public void gotoRequest();
    }

    public void setCallBack(VideoViewCallBack callBack) {
        this.callBack = callBack;
    }
}
