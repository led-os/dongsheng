package amodule.article.activity.edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import acore.override.XHApplication;
import acore.tools.StringManager;
import acore.tools.Tools;
import amodule.article.activity.ArticleDetailActivity;
import amodule.article.activity.ArticleSelectActivity;
import amodule.article.db.UploadArticleSQLite;

/**
 * PackageName : amodule.article.activity
 * Created by MrTrying on 2017/5/19 09:19.
 * E_mail : ztanzeyu@gmail.com
 */
public class ArticleEidtActivity extends EditParentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView("发文章");
        initData(new UploadArticleSQLite(XHApplication.in().getApplicationContext()));
    }

    @Override
    public String getType() {
        return TYPE_ARTICLE;
    }

    @Override
    protected boolean canAddLink() {
        return true;
    }

    @Override
    protected boolean isEnableEditText() {
        return true;
    }

    @Override
    protected int getMaxImageCount() {
        return 50;
    }

    @Override
    protected int getMaxVideoCount() {
        return 5;
    }

    @Override
    protected int getMaxTextCount() {
        return 15000;
    }

    @Override
    protected int getMaxURLCount() {
        return 100;
    }

    @Override
    public String getEditApi() {
        return StringManager.api_getArticleInfo;
    }

    @Override
    public void onNextSetp() {
        String checkStr = checkData();
        if (TextUtils.isEmpty(checkStr)) {
            saveDraft();
            if(timer != null)timer.cancel();
            Intent intent = new Intent(this, ArticleSelectActivity.class);
            intent.putExtra("draftId", uploadArticleData.getId());
            intent.putExtra("dataType", EditParentActivity.DATA_TYPE_ARTICLE);
            startActivity(intent);
            finish();
        } else {
            Tools.showToast(this, checkStr);
        }
    }

    @Override
    public Class<?> getIntentClass() {
        return ArticleDetailActivity.class;
    }
}
