package amodule.dish.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.xiangha.R;

import java.io.InputStream;

import acore.tools.FileManager;
import aplug.basic.LoadImage;
import aplug.basic.SubBitmapTarget;
import xh.basic.tool.UtilImage;

/**
 * Created by Administrator on 2016/7/28.
 */

public class DishBaseView extends RelativeLayout {
    public int imgResource = R.drawable.i_nopic;
    public int roundImgPixels = 0, imgWidth = 0, imgHeight = 0,// 以像素为单位
            roundType = 1; // 1为全圆角，2上半部分圆角
    public boolean imgZoom = false; // 是否允许图片拉伸来适应设置的宽或高
    public String imgLevel = FileManager.save_cache; // 图片保存等级
    public int viewWidth = 0; // viewWidth的最小宽度
    public int viewHeight = 0; // viewHeight的最小宽度
    public boolean isAnimate = false;//控制图片渐渐显示

    public static final int TAG_ID = R.string.tag;
    public int playImgWH = 41;
    public Context context;

    public DishBaseView(Context context, int layoutId) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(layoutId, this, true);
        init();
    }

    public DishBaseView(Context context, AttributeSet attrs, int layoutId) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(layoutId, this, true);
        init();
    }

    public DishBaseView(Context context, AttributeSet attrs, int defStyleAttr, int layoutId) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(layoutId, this, true);
        init();
    }

    public void init() {

    }

    public void setViewImage(final ImageView v, String value) {
        v.setVisibility(View.VISIBLE);
        // 异步请求网络图片
        if (value.indexOf("http") == 0) {
            if (value.length() < 10)
                return;
            v.setImageResource(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.setTag(TAG_ID, value);
            if(context==null)return;
            BitmapRequestBuilder<GlideUrl, Bitmap> requestBuilder = LoadImage.with(context)
                    .load(value)
                    .setImageRound(roundImgPixels)
                    .setPlaceholderId(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon)
                    .setErrorId(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon)
                    .setSaveType(imgLevel)
                    .build();
            if(requestBuilder != null){
                requestBuilder.into(getTarget(v, value));
            }
        }
        // 直接设置为内部图片
        else if (value.indexOf("ico") == 0) {
            InputStream is = v.getResources().openRawResource(Integer.parseInt(value.replace("ico", "")));
            Bitmap bitmap = UtilImage.inputStreamTobitmap(is);
            bitmap = UtilImage.toRoundCorner(v.getResources(), bitmap, roundType, roundImgPixels);
            UtilImage.setImgViewByWH(v, bitmap, imgWidth, imgHeight, imgZoom);
        }
        // 隐藏
        else if (value.equals("hide") || value.length() == 0)
            v.setVisibility(View.GONE);
            // 直接加载本地图片
        else if (!value.equals("ignore")) {
            if (v.getTag(TAG_ID) != null && v.getTag(TAG_ID).equals(value))
                return;
            v.setTag(TAG_ID, value);
            BitmapRequestBuilder<GlideUrl, Bitmap> requestBuilder = LoadImage.with(context)
                    .load(value)
                    .setImageRound(roundImgPixels)
                    .setSaveType(imgLevel)
                    .build();
            if(requestBuilder != null){
                requestBuilder.placeholder(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon)
                    .error(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon)
                    .into(getTarget(v, value));
            }
        }
        // 如果为ignore,则忽略图片
    }

    public SubBitmapTarget getTarget(final ImageView v, final String url) {
        return new SubBitmapTarget() {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                ImageView img = null;
                if (v.getTag(TAG_ID).equals(url))
                    img = v;
                if (img != null && bitmap != null) {
                    // 图片圆角和宽高适应auther_userImg
                    if (v.getId() == R.id.iv_userImg || v.getId() == R.id.auther_userImg) {
                        v.setScaleType(ImageView.ScaleType.CENTER_CROP);
//						bitmap = UtilImage.toRoundCorner(v.getResources(), bitmap, 1, ToolsDevice.dp2px(context, 500));
                        v.setImageBitmap(UtilImage.makeRoundCorner(bitmap));
                    } else {
                        v.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        UtilImage.setImgViewByWH(v, bitmap, imgWidth, imgHeight, imgZoom);
                        if (isAnimate) {
//							AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//							alphaAnimation.setDuration(300);
//							v.setAnimation(alphaAnimation);
                        }
                    }
                }
            }
        };
    }

}
