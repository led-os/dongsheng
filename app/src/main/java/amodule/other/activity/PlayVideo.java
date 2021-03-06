package amodule.other.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.xiangha.R;

import acore.override.activity.base.BaseAppCompatActivity;
import acore.tools.FileManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;

public class PlayVideo extends BaseAppCompatActivity {
    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    private String url, img, name;
    private StandardGSYVideoPlayer videoPlayer;
    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            url = getIntent().getStringExtra("url");
            img = getIntent().getStringExtra("img");
        } else {
            Tools.showToast(this, "此视频不存在");
            finish();
            return;
        }

        if ("null".equals(ToolsDevice.getNetWorkSimpleType(this))) {
            Toast.makeText(this, "网络未连接，请检查网络设置", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initActivity(name,2,0,0,R.layout.a_other_play_video);
        init();
    }

    private void init() {
        initTip();
        intiVideoView();
    }

    private void initTip() {
        TextView tipMessage= (TextView) findViewById(R.id.tipMessage);
        tipMessage.setText("现在是非WIFI，看视频要花费流量了");
        Button btnCloseTip = (Button) findViewById(R.id.btnCloseTip);
        btnCloseTip.setText("继续播放");
        findViewById(R.id.btnCloseTip).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            findViewById(R.id.tip_view).setVisibility(View.GONE);
            if (videoPlayer != null)
                videoPlayer.startPlayLogic();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileManager.saveShared(PlayVideo.this,FileManager.SHOW_NO_WIFI,FileManager.SHOW_NO_WIFI,"1");
                }
            }).start();
        }
    };

    private void intiVideoView() {
        videoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.video_player);
        videoPlayer.setUp(url,false,"");
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        orientationUtils.setEnable(false);
        orientationUtils.setRotateWithSystem(false);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });

        videoPlayer.setBottomProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_progress));
        videoPlayer.setDialogVolumeProgressBar(getResources().getDrawable(R.drawable.video_new_volume_progress_bg));
        videoPlayer.setDialogProgressBar(getResources().getDrawable(R.drawable.video_new_progress));
        videoPlayer.setBottomShowProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_seekbar_progress),
                getResources().getDrawable(R.drawable.video_new_seekbar_thumb));
        videoPlayer.setDialogProgressColor(Color.parseColor("#FFFFFF"), -11);

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if("wifi".equals(ToolsDevice.getNetWorkSimpleType(this)) || !"0".equals(FileManager.loadShared(PlayVideo.this,FileManager.SHOW_NO_WIFI,FileManager.SHOW_NO_WIFI)))
            videoPlayer.startPlayLogic();
        else {
            findViewById(R.id.tip_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoPlayer != null)
            videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer != null)
            videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放所有
        if (videoPlayer != null){
            videoPlayer.setStandardVideoAllCallBack(null);
            videoPlayer.releaseAllVideos();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (videoPlayer != null)
                videoPlayer.getFullscreenButton().performClick();
            return;
        }
        super.onBackPressed();
    }

}