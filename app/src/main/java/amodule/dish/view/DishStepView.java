package amodule.dish.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.xiangha.R;

import java.util.ArrayList;
import java.util.Map;

import acore.tools.StringManager;
import acore.tools.Tools;
import amodule.dish.activity.DetailDish;
import aplug.basic.LoadImage;
import aplug.basic.SubBitmapTarget;

/**
 * adapter菜谱详情页
 */
public class DishStepView extends DishBaseView {
    public static String DISH_STYLE_STEP="dish_style_step";
    public static final  int DISH_STYLE_STEP_INDEX=1;
    private TextView itemText1;

    private ImageView loadProgress;
    private ImageView itemImg1,itemGif,itemGifHint;
    private TextView itemNum;
    private StepViewCallBack callback;

    private boolean isHasVideo = false;
    private int position;

    public DishStepView(Context context) {
        super(context, R.layout.a_dish_step_item);
    }

    public DishStepView(Context context, AttributeSet attrs, int layoutId) {
        super(context, attrs, R.layout.a_dish_step_item);
    }

    public DishStepView(Context context, AttributeSet attrs, int defStyleAttr, int layoutId) {
        super(context, attrs, defStyleAttr, R.layout.a_dish_step_item);
    }

    @Override
    public void init() {
        super.init();
        itemText1 = (TextView) findViewById(R.id.itemText1);
        loadProgress = (ImageView) findViewById(R.id.load_progress);
        itemImg1 = (ImageView) findViewById(R.id.itemImg1);
        itemGif = (ImageView) findViewById(R.id.a_dish_stem_item_gif);
        itemGifHint = (ImageView) findViewById(R.id.dish_step_gif_hint);
        itemNum = (TextView) findViewById(R.id.itemNum);
    }

    public void setData(Map<String, String> map, StepViewCallBack stepViewCallBack,int position) {
        this.position= position;
        this.callback = stepViewCallBack;
        String text = map.get("info").trim();
        text = text.replace("\n","").replace("\r","");
        itemText1.setText(text);
        itemNum.setText(map.get("num"));
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick();
            }
        });
        if(position==0) findViewById(R.id.img_view).setVisibility(View.VISIBLE);
        else findViewById(R.id.img_view).setVisibility(View.GONE);
        if(TextUtils.isEmpty(map.get("img"))|| DetailDish.showNumLookImage>=3){
            findViewById(R.id.img_view).setVisibility(View.GONE);
        }

//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("gif","http://img.zcool.cn/community/012f4d5542e7de0000019ae98b8ef1.jpg");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        jsonArray.put(jsonObject);
//        map.put("video",jsonArray.toString());
        //数据
        itemGifHint.setVisibility(View.GONE);
        itemImg1.setVisibility(View.VISIBLE);
        loadProgress.clearAnimation();
        loadProgress.setVisibility(View.GONE);
        itemImg1.setImageResource(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon);
        itemGifHint.setImageResource(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon);

        isHasVideo = false;
        //视频信息
        if(map.containsKey("video")&&!TextUtils.isEmpty(map.get("video"))){
            findViewById(R.id.view_linear).setVisibility(View.VISIBLE);
            ArrayList<Map<String,String>> videoArray = StringManager.getListMapByJson(map.get("video"));
            if(videoArray.size() > 0){
                Map<String,String> videoMap = videoArray.get(0);
                final String gifUrl = videoMap.get("gif");
                final String imgUrl = videoMap.get("img");
                if(!TextUtils.isEmpty(gifUrl)){
                    isHasVideo = true;
                    loadImg(imgUrl);

                    itemImg1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadGif(gifUrl);
                        }
                    });
                    itemGif.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemGifHint.setVisibility(View.VISIBLE);
                            itemImg1.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }
         if(!isHasVideo){
             if( map.containsKey("img")&&!TextUtils.isEmpty(map.get("img"))){
                 findViewById(R.id.view_linear).setVisibility(View.VISIBLE);
                 itemImg1.setVisibility(View.VISIBLE);
                 setViewImage(itemImg1, map.get("img"));
                 if (map.containsKey("height") && Integer.parseInt(map.get("height")) > 0) {
                     setImageWH(itemImg1,Integer.parseInt(map.get("height")));
                 }
             }else{
                 findViewById(R.id.view_linear).setVisibility(View.GONE);
                 itemImg1.setVisibility(View.GONE);
             }

        }
    }

    public void stopGif(){
        itemGifHint.setVisibility(View.VISIBLE);
        itemImg1.setVisibility(View.VISIBLE);
    }

    private void loadImg(String imgUrl){
        if(!TextUtils.isEmpty(imgUrl)) {
            itemImg1.setImageResource(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon);
            itemImg1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemImg1.setTag(TAG_ID, imgUrl);
            BitmapRequestBuilder<GlideUrl, Bitmap> requestBuilder = LoadImage.with(context)
                    .load(imgUrl)
                    .setImageRound(roundImgPixels)
                    .setPlaceholderId(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon)
                    .setErrorId(roundImgPixels == 0 ? imgResource : R.drawable.bg_round_user_icon)
                    .setSaveType(imgLevel)
                    .build();
            if(requestBuilder != null){
                itemGifHint.setVisibility(View.VISIBLE);
                itemGifHint.setImageResource(R.drawable.i_dish_detail_gif_hint);
                requestBuilder.into(getTarget(itemImg1, imgUrl));
            }
        }
    }

    /**
     * 设置GIF
     * @param gifUrl
     */
    private void loadGif(final String gifUrl){
        if(!TextUtils.isEmpty(gifUrl)){
            if(itemGif.getTag() == null)
                itemGif.setTag(TAG_ID, gifUrl);
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.loading_anim);
            loadProgress.startAnimation(animation);
            loadProgress.setVisibility(VISIBLE);
            itemImg1.setVisibility(VISIBLE);
            itemGifHint.setVisibility(View.GONE);
            final GifRequestBuilder requestBuilder = Glide.with(getContext())
                    .load(gifUrl)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GifDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GifDrawable> target, boolean b) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(GifDrawable gifDrawable, String s, Target<GifDrawable> target, boolean b, boolean b1) {
                            if (itemGif.getTag(TAG_ID).equals(gifUrl)) {
                                loadProgress.clearAnimation();
                                loadProgress.setVisibility(GONE);
                                itemImg1.setVisibility(View.GONE);
                                itemGifHint.setVisibility(View.GONE);
                                setImageWH(itemGif, itemImg1.getHeight());
                            }
                            return false;
                        }
                    });
            if(itemGif != null){
                if (itemGif.getTag(TAG_ID).equals(gifUrl)){
                    requestBuilder.into(itemGif);
                }
            }
        }
    }

    private void setImageWH(ImageView imgView,int imgHeight){
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int dp_290= Tools.getDimen(context,R.dimen.dp_290);
        RelativeLayout.LayoutParams layoutParams;
        if(isHasVideo){
            imgView.setMinimumHeight(0);
            layoutParams = new RelativeLayout.LayoutParams((int) (imgHeight / 9.0 * 16),imgHeight);
        }
        else layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imgHeight > dp_290 ? dp_290 : imgHeight);
        int dp_12= Tools.getDimen(context,R.dimen.dp_12);
        int dp_8= Tools.getDimen(context,R.dimen.dp_8);
        int dp_23= Tools.getDimen(context,R.dimen.dp_23);
        layoutParams.setMargins(0,dp_12,dp_23,0);
        imgView.setLayoutParams(layoutParams);
    }



    @Override
    public SubBitmapTarget getTarget(final ImageView v, final String url) {
        return new SubBitmapTarget() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> arg1) {
                ImageView img = null;
                if (v.getTag(TAG_ID).equals(url))
                    img = v;
                if (img != null && bitmap != null) {
                    // 图片圆角和宽高适应
                    int imgHei = bitmap.getHeight();
                    v.setImageBitmap(bitmap);
                    setImageWH(v,imgHei);
                    if(callback!=null)callback.getHeight(String.valueOf(imgHei));
                }
            }
        };
    }


    public interface StepViewCallBack{
        public void getHeight(String height);
        public void onClick();
    }
}
