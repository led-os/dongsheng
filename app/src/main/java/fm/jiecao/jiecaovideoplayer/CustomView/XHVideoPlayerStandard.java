package fm.jiecao.jiecaovideoplayer.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xiangha.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 这里可以监听到视频播放的生命周期和播放状态
 * 所有关于视频的逻辑都应该写在这里
 * Created by Nathen on 2017/7/2.
 */
public class XHVideoPlayerStandard extends JCVideoPlayerStandard {
    private OnPlayPreparedCallback onPlayPreparedCallback = null;
    private OnPlayCompleteCallback onPlayCompleteCallback = null;
    private OnPlayStartCallback onPlayStartCallback = null;

    public XHVideoPlayerStandard(Context context) {
        super(context);
    }

    public XHVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.fullscreen) {
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //click quit fullscreen
            } else {
                //click goto fullscreen
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

    /**
     * ============================================================= 生命周期方法 =============================================================
     */

    @Override
    public void startVideo() {
        super.startVideo();
        if(onPlayStartCallback != null){
            onPlayStartCallback.onStart();
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if(onPlayPreparedCallback != null)
            onPlayPreparedCallback.onPrepared();
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
    }

    @Override
    public void onStatePlaybackBufferingStart() {
        super.onStatePlaybackBufferingStart();
    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        if(onPlayCompleteCallback != null){
            onPlayCompleteCallback.onComplte();
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

    @Override
    public void startWindowFullscreen() {
        super.startWindowFullscreen();
    }

    @Override
    public void startWindowTiny() {
        super.startWindowTiny();
    }

    /**
     * ============================================================= 生命周期方法 End =============================================================
     */

    /**
     * ============================================================= Get & Set =============================================================
     */

    public OnPlayCompleteCallback getOnPlayCompleteCallback() {
        return onPlayCompleteCallback;
    }


    public void setOnPlayCompleteCallback(OnPlayCompleteCallback onPlayCompleteCallback) {
        this.onPlayCompleteCallback = onPlayCompleteCallback;
    }

    public OnPlayStartCallback getOnPlayStartCallback() {
        return onPlayStartCallback;
    }

    public void setOnPlayStartCallback(OnPlayStartCallback onPlayStartCallback) {
        this.onPlayStartCallback = onPlayStartCallback;
    }

    public OnPlayPreparedCallback getOnPlayPreparedCallback() {
        return onPlayPreparedCallback;
    }

    public void setOnPlayPreparedCallback(OnPlayPreparedCallback onPlayPreparedCallback) {
        this.onPlayPreparedCallback = onPlayPreparedCallback;
    }
    /**
     * ============================================================= Get & Set END =============================================================
     */

    /**
     * ============================================================= interface =============================================================
     */

    public interface OnPlayPreparedCallback{
        public void onPrepared();
    }

    public interface OnPlayStartCallback{
        public void onStart();
    }
    public interface OnPlayCompleteCallback{
        public void onComplte();
    }

    /**
     * ============================================================= interface END =============================================================
     */

}
