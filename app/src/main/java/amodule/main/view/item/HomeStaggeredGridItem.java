package amodule.main.view.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiangha.R;

import java.util.Map;

import acore.logic.FavoriteHelper;
import acore.tools.FileManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.widget.IconTextSpan;
import amodule.dish.activity.ShortVideoDetailActivity;
import aplug.basic.LoadImage;
import xh.basic.internet.img.transformation.RoundTransformation;

public class HomeStaggeredGridItem extends HomeItem {

    private ImageView mImg;
    private ConstraintLayout mImgContainer;
    private TextView mTitle,num_tv;
    private ImageView auther_userImg,img_fav;
    private boolean mIsVideo;
    private int[] mHeightRange = new int[]{getResources().getDimensionPixelSize(R.dimen.dp_130), getResources().getDimensionPixelSize(R.dimen.dp_230)};

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
        mImg = findViewById(R.id.img);
        mTitle = findViewById(R.id.title);
        auther_userImg = findViewById(R.id.user_header_img);
        img_fav = findViewById(R.id.img_fav);
        num_tv= findViewById(R.id.num_tv);
        mImgContainer = findViewById(R.id.staggered_container);
    }

    @Override
    public void setData(Map<String, String> dataMap, int position) {
        super.setData(dataMap, position);
        if(mDataMap == null)
            return;
        Map<String,String> mResourceData = StringManager.getFirstMap(mDataMap.get("resourceData"));
        if(mResourceData!=null && !mResourceData.isEmpty()){
            int imgWidth= Integer.parseInt(mResourceData.get("width"));
            int imgHeight= Integer.parseInt(mResourceData.get("height"));
            int realWidth = (Tools.getPhoneWidth() - getResources().getDimensionPixelSize(R.dimen.dp_50)) / 2;
            int realHeight = realWidth * imgHeight / imgWidth;

            Log.i("TAG", "setData: min = " + mHeightRange[0] + "   max = " + mHeightRange[1] + "   realH = " + realHeight);

            if (realHeight < mHeightRange[0]) {
                realHeight = mHeightRange[0];
            } else if (realHeight > mHeightRange[1]) {
                realHeight = mHeightRange[1];
            }



            Log.i("TAG", "setData2: min = " + mHeightRange[0] + "   max = " + mHeightRange[1] + "   realH = " + realHeight);

            ConstraintSet cs = new ConstraintSet();
            cs.constrainWidth(mImg.getId(), ConstraintSet.MATCH_CONSTRAINT);
            cs.constrainHeight(mImg.getId(), realHeight);
            cs.constrainMinHeight(mImg.getId(), mHeightRange[0]);
            cs.connect(mImg.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            cs.connect(mImg.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            cs.connect(mImg.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            cs.connect(mImg.getId(), ConstraintSet.BOTTOM, R.id.guideline, ConstraintSet.TOP);
            cs.applyTo(mImgContainer);
            mImg.postInvalidate();
            if(!TextUtils.isEmpty(mResourceData.get("gif"))) {
                mImg.setTag(TAG_ID, mResourceData.get("gif"));
                Glide.with(getContext()).load(mResourceData.get("gif")).asGif().placeholder(R.drawable.i_nopic).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImg);
            } else {
                mImg.setTag(TAG_ID, mResourceData.get("img"));
                LoadImage.with(getContext()).load(mResourceData.get("img")).setSaveType(FileManager.save_cache).setPlaceholderId(R.drawable.i_nopic).setErrorId(R.drawable.i_nopic).build().into(mImg);
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
        mTitle.setText("");
        String title = mDataMap.get("name");
        if (!TextUtils.isEmpty(title)) {
            mTitle.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mDataMap.get("isEssence"), "2")) {
                IconTextSpan.Builder ib = new IconTextSpan.Builder(getContext());
                ib.setBgColorInt(getResources().getColor(R.color.icon_text_bg));
                ib.setTextColorInt(getResources().getColor(R.color.c_white_text));
                ib.setText("精选");
                ib.setRadius(2f);
                ib.setRightMargin(3);
                ib.setBgHeight(14f);
                ib.setTextSize(10f);
                StringBuffer sb = new StringBuffer(" ");
                sb.append(title);
                SpannableStringBuilder ssb = new SpannableStringBuilder(sb.toString());
                ssb.setSpan(ib.build(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTitle.setText(ssb);
            } else {
                mTitle.setText(title);
            }
                mTitle.setText(title);
        } else {
            mTitle.setVisibility(View.GONE);
        }
        setImgFav();
        if(mDataMap.containsKey("favorites")){
            num_tv.setText(mDataMap.get("favorites"));
        }
        Map<String,String> map= StringManager.getFirstMap(mDataMap.get("customer"));
        LoadImage.with(getContext()).load(map.get("img")).setSaveType(FileManager.save_cache).setPlaceholderId(R.drawable.i_nopic).setErrorId(R.drawable.i_nopic).build().into(auther_userImg);
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

    private void setImgFav(){
        img_fav.setImageResource(mDataMap.containsKey("isFavorites")&&"2".equals(mDataMap.get("isFavorites"))?R.drawable.icon_fav_active:R.drawable.icon_fav);
        if(ShortVideoDetailActivity.favoriteLocalStates.containsKey(mDataMap.get("code"))&&
                !TextUtils.isEmpty(ShortVideoDetailActivity.favoriteLocalStates.get(mDataMap.get("code")))){//已经包含
            img_fav.setImageResource("2".equals(ShortVideoDetailActivity.favoriteLocalStates.get(mDataMap.get("code")))?R.drawable.icon_fav_active:R.drawable.icon_fav);
        }
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

//    @Override
//    protected SubAnimTarget getSubAnimTarget(ImageView v, String url) {
//        return new SubAnimTarget(v) {
//            @Override
//            protected void setResource(Bitmap bitmap) {
//                if (bitmap != null && v.getTag(TAG_ID) != null && v.getTag(TAG_ID).equals(url)) {
//                    if (v.getId() == R.id.user_header_img) {
//                        v.setImageBitmap(UtilImage.toRoundCorner(v.getResources(), bitmap, 1, ToolsDevice.dp2px(getContext(), 500)));
//                    } else {
//                        v.setImageBitmap(bitmap);
//                    }
//                }
//            }
//        };
//    }
}
