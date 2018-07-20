package amodule.main.view.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiangha.R;

import java.util.Map;

import acore.logic.FavoriteHelper;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import acore.widget.multifunction.IconTextSpan;
import aplug.basic.SubAnimTarget;
import aplug.basic.SubBitmapTarget;
import xh.basic.tool.UtilImage;

public class HomeStaggeredGridItem extends HomeItem {

    private ImageView mImg;
    private ImageView mPlayIcon;
    private ConstraintLayout mContentLayout;
    private TextView mTitle,num_tv;
    private ImageView auther_userImg,img_fav;
    private boolean mIsVideo;
    private int waith;

    public HomeStaggeredGridItem(Context context) {
        this(context, null);
    }

    public HomeStaggeredGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeStaggeredGridItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, R.layout.home_staggered_grid_item);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void initView() {
        super.initView();
        mImg = (ImageView) findViewById(R.id.img);
        mPlayIcon = (ImageView) findViewById(R.id.icon_play);
        mContentLayout = (ConstraintLayout) findViewById(R.id.content_layout);
        mTitle = (TextView) findViewById(R.id.title);
        auther_userImg = (ImageView) findViewById(R.id.auther_userImg);
        img_fav = (ImageView) findViewById(R.id.img_fav);
        num_tv= (TextView) findViewById(R.id.num_tv);
        waith= Tools.getPhoneWidth()/2;
    }

    @Override
    public void setData(Map<String, String> dataMap, int position) {
        super.setData(dataMap, position);
        if(mDataMap == null)
            return;
        Map<String, String> imgMap = StringManager.getFirstMap(mDataMap.get("styleData"));
        if (imgMap.size() > 0) {
            String imgUrl = imgMap.get("url");
            if (!TextUtils.isEmpty(imgUrl)) {
                loadImage(imgUrl, mImg);
            }
            if(mDataMap.containsKey("waithImg")&&mDataMap.containsKey("heightImg")
                    &&!TextUtils.isEmpty(mDataMap.get("waithImg"))&&!TextUtils.isEmpty(mDataMap.get("heightImg"))){
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(Integer.parseInt(mDataMap.get("waithImg")),Integer.parseInt(mDataMap.get("heightImg")));
                mImg.setLayoutParams(layoutParams);
            }
        }
        if (mDataMap.containsKey("video")) {
            String video = mDataMap.get("video");
            if (!TextUtils.isEmpty(video)) {
                Map<String, String> videoMap = StringManager.getFirstMap(video);
                String videoUrl = videoMap.get("videoUrl");
                if (!TextUtils.isEmpty(videoUrl)) {
                    Map<String, String> videoUrlMap = StringManager.getFirstMap(videoUrl);
                    String defUrl = videoUrlMap.get("defaultUrl");
                    if (!TextUtils.isEmpty(defUrl)) {
                        mIsVideo = true;
                    }
                }
            }
        }
        mPlayIcon.setVisibility(mIsVideo ? View.VISIBLE : View.GONE);
        mTitle.setText("");
        String title = mDataMap.get("name");
        if (!TextUtils.isEmpty(title)) {
            mTitle.setVisibility(View.VISIBLE);
//            if (TextUtils.equals(mDataMap.get("isEssence"), "2")) {
//
//                IconTextSpan.Builder ib = new IconTextSpan.Builder(getContext());
//                ib.setBgColorInt(getResources().getColor(R.color.icon_text_bg));
//                ib.setTextColorInt(getResources().getColor(R.color.c_white_text));
//                ib.setText("精选");
//                ib.setRadius(2f);
//                ib.setRightMargin(3);
//                ib.setBgHeight(14f);
//                ib.setTextSize(10f);
//                StringBuffer sb = new StringBuffer(" ");
//                sb.append(title);
//                SpannableStringBuilder ssb = new SpannableStringBuilder(sb.toString());
//                ssb.setSpan(ib.build(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                mTitle.setText(ssb);
//            } else {
//                mTitle.setText(title);
//            }
                mTitle.setText(title);
        } else {
            mTitle.setVisibility(View.GONE);
        }

        mContentLayout.setVisibility(!TextUtils.isEmpty(title) || (mUserName != null && !TextUtils.isEmpty(mUserName.getText())) ? View.VISIBLE : View.GONE);
        setImgFav();
        if(mDataMap.containsKey("favorites")){
            num_tv.setText(mDataMap.get("favorites"));
        }
        handleImgUse();
        img_fav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFav();
            }
        });
    }

    @Override
    protected void resetData() {
        super.resetData();
        mIsVideo = false;
    }

    @Override
    protected SubAnimTarget getSubAnimTarget(ImageView v, String url) {
        return new SubAnimTarget(v) {
            @Override
            protected void setResource(Bitmap bitmap) {
                if (bitmap != null && v.getTag(TAG_ID) != null && v.getTag(TAG_ID).equals(url)) {
                    if (v.getId()==R.id.img){
                        int heightImg = (waith / bitmap.getWidth()) * bitmap.getHeight();
                        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(waith,heightImg);
                        mDataMap.put("waithImg",String.valueOf(waith));
                        mDataMap.put("heightImg",String.valueOf(heightImg));
                        v.setLayoutParams(layoutParams);
                    }else if (v.getId() == R.id.iv_userImg || v.getId() == R.id.auther_userImg ) {
                        v.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        bitmap = UtilImage.toRoundCorner(v.getResources(), bitmap, 1, ToolsDevice.dp2px(getContext(), 500));
                    }
                    v.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable drawable) {
                super.onLoadFailed(e, drawable);
                BuglyLog.i("image", "url = " + url + "  netStatus = " + ToolsDevice.getNetWorkSimpleType(getContext()));
                CrashReport.postCatchedException(e);
            }
        };
    }

    @Override
    protected SubBitmapTarget getTarget(ImageView v, String url) {
        return super.getTarget(v, url);
    }

    private void setImgFav(){
        img_fav.setImageResource(mDataMap.containsKey("isFavorites")&&"2".equals(mDataMap.get("isFavorites"))?R.drawable.icon_fav_active:R.drawable.icon_fav);
    }
    private void requestFav(){
        if(mDataMap.containsKey("isFavorites")&&!"2".equals(mDataMap.get("isFavorites"))){
            FavoriteHelper.instance().setFavoriteStatus(getContext(), mDataMap.get("code"), mDataMap.get("name"), FavoriteHelper.TYPE_VIDEO, new FavoriteHelper.FavoriteStatusCallback() {
                @Override
                public void onSuccess(boolean isFav) {
                    mDataMap.put("isFavorites","2");
                    setImgFav();
                }

                @Override
                public void onFailed() {
                }
            });
        }
    }
    private void handleImgUse(){
        if(mDataMap.containsKey("customer")){
            Map<String,String> map= StringManager.getFirstMap(mDataMap.get("customer"));
            if(map!=null&&!map.isEmpty()&&map.containsKey("img")){
                loadImage(map.get("img"),auther_userImg);
            }
        }
    }
}
