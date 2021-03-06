package amodule.article.activity.edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;

import acore.override.XHApplication;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import amodule.article.activity.ArticleUploadListActivity;
import amodule.article.activity.VideoDetailActivity;
import amodule.article.db.UploadVideoSQLite;

/**
 * Created by XiangHa on 2017/5/28.
 */

public class VideoEditActivity extends EditParentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView("发视频");
        initData(new UploadVideoSQLite(XHApplication.in().getApplicationContext()));
    }

    @Override
    public String getType() {
        return "2";
    }

    @Override
    protected boolean canAddLink() {
        return false;
    }

    @Override
    protected boolean isEnableEditText() {
        return false;
    }

    @Override
    protected int getMaxImageCount() {
        return 0;
    }

    @Override
    protected int getMaxVideoCount() {
        return 1;
    }

    @Override
    protected int getMaxTextCount() {
        return 5000;
    }

    @Override
    protected int getMaxURLCount() {
        return 0;
    }

    @Override
    public String getEditApi() {
        return StringManager.api_getVideoInfo;
    }

    @Override
    public void onNextSetp() {
        if(ToolsDevice.getNetActiveState(this)) {
            String checkStr = checkData();
            if (TextUtils.isEmpty(checkStr)) {
                uploadArticleData.setIsOriginal(2);
                saveDraft();
                if (timer != null) timer.cancel();
                Intent intent = new Intent(this, ArticleUploadListActivity.class);
                intent.putExtra("draftId", uploadArticleData.getId());
                intent.putExtra("dataType", EditParentActivity.DATA_TYPE_VIDEO);
                intent.putExtra("coverPath", uploadArticleData.getImg());
                intent.putExtra("isAutoUpload", true);
                String videoPath = "";
                ArrayList<Map<String, String>> videoArray = uploadArticleData.getVideoArray();
                if (videoArray.size() > 0) {
                    videoPath = videoArray.get(0).get("video");
                }
                intent.putExtra("finalVideoPath", videoPath);
                startActivity(intent);
                finish();
            } else {
                Tools.showToast(this, checkStr);
            }
        }else{
            Tools.showToast(this,"网络错误，请检查网络或重试");
        }
    }

    @Override
    protected String checkData() {
        if (TextUtils.isEmpty(editTitle.getText())) {
            return "标题不能为空";
        }
        boolean isHasVideo = mixLayout.hasVideo();
        if (!isHasVideo) {
            return "视频不能为空";
        }
        if(mixLayout.getTextCount() > getMaxTextCount()){
            return "文字最多" + getMaxTextCount() + "字";
        }
        return null;
    }

    @Override
    public Class<?> getIntentClass() {
        return VideoDetailActivity.class;
    }
}
