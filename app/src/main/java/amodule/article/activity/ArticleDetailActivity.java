package amodule.article.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiangha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acore.logic.LoginManager;
import acore.logic.XHClick;
import acore.logic.load.AutoLoadMore;
import acore.override.activity.base.BaseActivity;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import amodule.article.activity.edit.ArticleEidtActiivty;
import amodule.article.adapter.ArticleDetailAdapter;
import amodule.article.tools.ArticleAdContrler;
import amodule.article.view.ArticleContentBottomView;
import amodule.article.view.ArticleHeaderView;
import amodule.article.view.BottomDialog;
import amodule.article.view.CommentBar;
import amodule.main.Main;
import amodule.user.Broadcast.UploadStateChangeBroadcasterReceiver;
import amodule.user.activity.FriendHome;
import aplug.basic.InternetCallback;
import aplug.basic.ReqEncyptInternet;
import aplug.basic.ReqInternet;
import aplug.web.tools.JsAppCommon;
import aplug.web.tools.WebviewManager;
import aplug.web.view.XHWebView;
import cn.srain.cube.views.ptr.PtrClassicFrameLayout;
import third.share.BarShare;
import xh.windowview.XhDialog;

import static amodule.article.adapter.ArticleDetailAdapter.TYPE_KEY;
import static amodule.article.adapter.ArticleDetailAdapter.Type_comment;
import static amodule.article.adapter.ArticleDetailAdapter.Type_recommed;

/** 文章详情 */
public class ArticleDetailActivity extends BaseActivity {
    public static final String TYPE_ARTICLE = "1";
    public static final String TYPE_VIDEO = "2";

    private ListView listview;
    /** 头部view */
    private LinearLayout layout, linearLayoutOne, linearLayoutTwo, linearLayoutThree;
    private TextView mTitle;
    private ImageView rightButton;
    private PtrClassicFrameLayout refreshLayout;
    private ArticleContentBottomView articleContentBottomView;
    private ArticleHeaderView headerView;
    private XHWebView webView;
    private CommentBar mArticleCommentBar;
    private View adView;

    private ArticleAdContrler mArticleAdContrler;
    private ArticleDetailAdapter detailAdapter;

    private ArrayList<Map<String, String>> allDataListMap = new ArrayList<>();//评论列表和推荐列表对数据集合

    private Map<String, String> adDataMap;
    private Map<String, String> commentMap;
    private Map<String, String> shareMap = new HashMap<>();
    private String commentNum;
    private boolean isKeyboradShow = false;
    private boolean isAdShow = false;
    private String code = "";//请求数据的code
    private int page = 0;//相关推荐的page


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            code = bundle.getString("code");
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (loadManager != null)
            loadManager.hideProgressBar();
        refreshData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    /** 初始化 **/
    private void init() {
        initView();
        initData();
    }

    /** View部分初始化 **/
    private void initView() {
        initActivity("", 2, 0, 0, R.layout.a_article_detail);
        //处理状态栏引发的问题
        initStatusBar();
        //初始化title
        initTitle();
        //初始化刷新layout
        refreshLayout = (PtrClassicFrameLayout) findViewById(R.id.refresh_list_view_frame);
        //初始化listview
        initListView();
        //初始化评论框
        initCommentBar();
    }

    private void initStatusBar() {
        if (Tools.isShowTitle()) {
            final RelativeLayout bottomBarLayout = (RelativeLayout) findViewById(R.id.edit_controler_layout);
            //设置layout监听，处理键盘弹出的高度问题
            rl.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            int heightDiff = rl.getRootView().getHeight() - rl.getHeight();
                            Rect r = new Rect();
                            rl.getWindowVisibleDisplayFrame(r);
                            int screenHeight = rl.getRootView().getHeight();
                            int heightDifference = screenHeight - (r.bottom - r.top);
                            isKeyboradShow = heightDifference > 200;
                            heightDifference = isKeyboradShow ? heightDifference - heightDiff : 0;
                            bottomBarLayout.setPadding(0, 0, 0, heightDifference);
                        }
                    });
        }
        String color = Tools.getColorStr(this, R.color.common_top_bg);
        Tools.setStatusBarColor(this, Color.parseColor(color));
    }

    /** 初始化title */
    private void initTitle() {
        View leftClose = findViewById(R.id.leftClose);
        leftClose.setVisibility(View.VISIBLE);
        leftClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.colse_level = 1;
                ArticleDetailActivity.this.finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        int dp85 = Tools.getDimen(this,R.dimen.dp_85);
        mTitle.setPadding(dp85,0,dp85,0);
        rightButton = (ImageView) findViewById(R.id.rightImgBtn2);
        ImageView leftImage = (ImageView) findViewById(R.id.leftImgBtn);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) leftImage.getLayoutParams();
        layoutParams.setMargins(Tools.getDimen(this, R.dimen.dp_15), 0, 0, 0);
        leftImage.setLayoutParams(layoutParams);

        findViewById(R.id.back).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    /** 初始化评论框 */
    private void initCommentBar() {
        mArticleCommentBar = (CommentBar) findViewById(R.id.acticle_comment_bar);
        mArticleCommentBar.setCode(code);
        mArticleCommentBar.setType(getType());
        mArticleCommentBar.setOnCommentSuccessCallback(new CommentBar.OnCommentSuccessCallback() {
            @Override
            public void onCommentSuccess(boolean isSofa, Object obj) {
                try {
                    if (allDataListMap != null && allDataListMap.size() > 0) {
                        mArticleCommentBar.setEditTextShow(false);
                        Map<String, String> newData = StringManager.getFirstMap(obj);
                        if (newData != null) {
                            int commentCount = Integer.parseInt(commentNum);
                            commentMap.put("commentNum", "" + ++commentCount);
                            commentNum = String.valueOf(commentCount);
                            Map<String, String> dataMap = StringManager.getFirstMap(commentMap.get("data"));
                            ArrayList<Map<String, String>> list = StringManager.getListMapByJson(dataMap.get("list"));
                            list.add(0, newData);
                            JSONArray jsonArray = StringManager.getJsonByArrayList(list);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("list", jsonArray);
                            commentMap.put("data", jsonObject.toString());
                            detailAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /** 初始化ListView */
    private void initListView() {
        listview = (ListView) findViewById(R.id.listview);
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //设置触摸收起键盘
                    case MotionEvent.ACTION_MOVE:
                        if (TextUtils.isEmpty(mArticleCommentBar.getEditText().getText().toString()))
                            mArticleCommentBar.setEditTextShow(false);
                        ToolsDevice.keyboardControl(false, ArticleDetailActivity.this, mArticleCommentBar.getEditText());
                        break;
                }
                return false;
            }
        });
        //initListView
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        linearLayoutOne = new LinearLayout(this);
        linearLayoutOne.setOrientation(LinearLayout.VERTICAL);

        linearLayoutTwo = new LinearLayout(this);
        linearLayoutTwo.setOrientation(LinearLayout.VERTICAL);

        linearLayoutThree = new LinearLayout(this);
        linearLayoutThree.setOrientation(LinearLayout.VERTICAL);

        linearLayoutOne.setVisibility(View.GONE);
        linearLayoutTwo.setVisibility(View.GONE);
        linearLayoutThree.setVisibility(View.GONE);
        layout.addView(linearLayoutOne);
        layout.addView(linearLayoutTwo);
        layout.addView(linearLayoutThree);

        listview.addHeaderView(layout);
    }

    /** 数据初始化 **/
    private void initData() {
        if (TextUtils.isEmpty(code)) {
            Tools.showToast(this, "当前数据错误，请重新请求");
            return;
        }
        //初始化Adapter
        detailAdapter = new ArticleDetailAdapter(this, allDataListMap, getType(), code);
        detailAdapter.setOnRabSofaCallback(new ArticleDetailAdapter.OnRabSofaCallback() {
            @Override
            public void onRabSoaf() {
                mArticleCommentBar.doComment("抢沙发");
            }
        });
        detailAdapter.setmOnADCallback(new ArticleDetailAdapter.OnADCallback() {
            @Override
            public void onClick(View view, int index, String s) {
                if (mArticleAdContrler != null) mArticleAdContrler.onListAdClick(view, index, s);
            }

            @Override
            public void onBind(int index, View view, String s) {
                if (mArticleAdContrler != null) mArticleAdContrler.onListAdBind(index, view, s);
            }
        });
        //设置
        loadManager.setLoading(refreshLayout, listview, detailAdapter, true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshData(false);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (page >= 1) requestRelateData();
                    }
                }, new AutoLoadMore.OnListScrollListener() {
                    int srceenHeight = ToolsDevice.getWindowPx(ArticleDetailActivity.this).heightPixels;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (adView != null && !isAdShow) {
                            int[] location = new int[2];
                            adView.getLocationOnScreen(location);
                            if (location[1] > 0
                                    && location[1] < srceenHeight * 3 / 4) {
                                isAdShow = true;
                                mArticleAdContrler.onBigAdBind(adView);
                            }
                        }
                    }
                });
        View view = new View(this);
        view.setMinimumHeight(Tools.getDimen(this, R.dimen.dp_40));
        listview.addFooterView(view);
        //请求文章数据
        requestArticleData(false);
        //初始化广告
        initAD();
    }

    private void initAD() {
        //请求广告数据
        mArticleAdContrler = new ArticleAdContrler();
        mArticleAdContrler.initADData();
        mArticleAdContrler.setOnBigAdCallback(new ArticleAdContrler.OnBigAdCallback() {
            @Override
            public void onBigAdData(Map<String, String> adDataMap) {
                ArticleDetailActivity.this.adDataMap = adDataMap;
                showAd(adDataMap);
            }
        });
        mArticleAdContrler.setOnListAdCallback(new ArticleAdContrler.OnListAdCallback() {
            @Override
            public void onListAdData(Map<String, String> adDataMap) {

                if(isRelateDataOk){
                    mArticleAdContrler.handlerAdData(allDataListMap);
                }
            }
        });
    }

    public void showAd(Map<String, String> adDataMap){
        if (articleContentBottomView == null
                || isFinishing()
                || adView != null)
            return;
        adView = mArticleAdContrler.getBigAdView(adDataMap);
        articleContentBottomView.addViewToAdLayout(adView);
    }

    /**
     * 刷新数据
     *
     * @param onlyUser 是否只刷新用户数据
     */
    private void refreshData(boolean onlyUser) {
        if (!onlyUser)
            resetData();
        requestArticleData(onlyUser);
    }

    /** 重置数据 */
    private void resetData() {
        page = 0;
        shareMap.clear();
        if (commentMap != null) commentMap.clear();
        allDataListMap.clear();
        if (detailAdapter != null) detailAdapter.notifyDataSetChanged();
    }

    /** 请求网络 */
    private void requestArticleData(final boolean onlyUser) {
        loadManager.showProgressBar();
        StringBuilder params = new StringBuilder().append("code=").append(code).append("&type=HTML");
        ReqEncyptInternet.in().doEncypt(StringManager.api_getArticleInfo, params.toString(), new InternetCallback(this) {
            @Override
            public void loaded(int flag, String url, Object object) {
                if (flag >= ReqInternet.REQ_OK_STRING) {
                    analysArticleData(onlyUser, StringManager.getFirstMap(object));
                } else
                    toastFaildRes(flag, true, object);
                if (!onlyUser)
                    requestForumData(false);//请求
                linearLayoutThree.setVisibility(View.VISIBLE);
                refreshLayout.refreshComplete();
                loadManager.hideProgressBar();
            }
        });
    }

    /**
     * 解析文章数据
     *
     * @param mapArticle
     */
    private void analysArticleData(boolean onlyUser, @NonNull final Map<String, String> mapArticle) {
        if (mapArticle.isEmpty()) return;

        if (headerView == null)
            headerView = new ArticleHeaderView(ArticleDetailActivity.this);
        if (linearLayoutOne.getChildCount() == 0)
            linearLayoutOne.addView(headerView);
        headerView.setType(getType());
        headerView.setData(mapArticle);
        linearLayoutOne.setVisibility(View.VISIBLE);
        detailAdapter.notifyDataSetChanged();
        if (onlyUser)
            return;
        listview.setVisibility(View.VISIBLE);

        WebviewManager manager = new WebviewManager(this, loadManager, true);
        if (webView == null)
            webView = manager.createWebView(0);
        if (linearLayoutTwo.getChildCount() == 0)
            linearLayoutTwo.addView(webView);
        manager.setJSObj(webView, new JsAppCommon(this, webView, loadManager, barShare));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadDataWithBaseURL(getMAPI() + mapArticle.get("code"), mapArticle.get("html"), "text/html", "utf-8", null);
        linearLayoutTwo.setVisibility(View.VISIBLE);

        final Map<String, String> customerData = StringManager.getFirstMap(mapArticle.get("customer"));
        if (!TextUtils.isEmpty(customerData.get("nickName"))) {
            mTitle.setText(customerData.get("nickName"));
            mTitle.setVisibility(View.VISIBLE);
        }
        final String userCode = customerData.get("code");
        final boolean isAuthor = LoginManager.isLogin()
                && !TextUtils.isEmpty(LoginManager.userInfo.get("code"))
                && !TextUtils.isEmpty(userCode)
                && userCode.equals(LoginManager.userInfo.get("code"));

        rightButton.setImageResource(isAuthor ? R.drawable.i_ad_more : R.drawable.z_z_topbar_ico_share);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAuthor) {
                    showBottomDialog();
                } else {
                    openShare();
                    statistics("分享", "");
                }
            }
        });
        rightButton.setVisibility(View.VISIBLE);
        if (articleContentBottomView == null)
            articleContentBottomView = new ArticleContentBottomView(this);
        if (linearLayoutThree.getChildCount() == 0)
            linearLayoutThree.addView(articleContentBottomView);
        articleContentBottomView.setData(mapArticle);

        if (!isAuthor) {
            articleContentBottomView.setOnReportClickCallback(new ArticleContentBottomView.OnReportClickCallback() {
                @Override
                public void onReportClick() {
                    Intent intent = new Intent(ArticleDetailActivity.this, ReportActivity.class);
                    intent.putExtra("code", code);
                    intent.putExtra("type", getType());
                    intent.putExtra("userCode", userCode);
                    intent.putExtra("reportName", customerData.get("nickName"));
                    intent.putExtra("reportContent", mapArticle.get("title"));
                    intent.putExtra("reportType", "1");
                    startActivity(intent);
                }
            });
        }
        if (adDataMap != null)
            showAd(adDataMap);

        detailAdapter.notifyDataSetChanged();

        commentNum = mapArticle.get("commentNumber");
        mArticleCommentBar.setPraiseAPI(StringManager.api_likeArticle);
        mArticleCommentBar.setData(mapArticle);

        mapArticle.remove("html");
        mapArticle.remove("content");
        mapArticle.remove("raw");
        //处理分享数据
        shareMap = StringManager.getFirstMap(mapArticle.get("share"));
        handlerShareData();
    }

    /** 请求评论列表 */
    private void requestForumData(final boolean isRefresh) {
        String url = StringManager.api_forumList;
        String param = "from=1&type=" + getType() + "&code=" + code;
        ReqEncyptInternet.in().doEncypt(url, param, new InternetCallback(this) {
            @Override
            public void loaded(int flag, String url, Object object) {
                if (flag >= ReqInternet.REQ_OK_STRING) {
                    analysForumData(isRefresh, object);
                } else
                    toastFaildRes(flag, true, object);
                if (page < 1)
                    requestRelateData();
            }
        });
    }

    private void analysForumData(boolean isRefresh, Object object) {
        commentMap = StringManager.getFirstMap(object);
        commentMap.put(TYPE_KEY, String.valueOf(Type_comment));
        commentMap.put("data", object.toString());
        commentMap.put("commentNum", commentNum);
        if (isRefresh) {
            int commentCount = Integer.parseInt(commentNum);
            commentMap.put("commentNum", "" + ++commentCount);
        }
        if (commentMap != null && allDataListMap.indexOf(commentMap) < 0)
            allDataListMap.add(commentMap);
        Log.i("tzy", "index = " + allDataListMap.indexOf(commentMap));
        detailAdapter.notifyDataSetChanged();
    }

    /** 请求推荐列表 */
    private void requestRelateData() {
        String param = "code=" + code + "&page=" + ++page + "&pagesize=10";
        ReqEncyptInternet.in().doEncypt(StringManager.api_getArticleRelated, param, new InternetCallback(this) {
            @Override
            public void loaded(int flag, String url, Object object) {
                if (flag >= ReqInternet.REQ_OK_STRING) {
                    ArrayList<Map<String, String>> listMap = StringManager.getListMapByJson(object);
                    int size = listMap.size();
                    for (int i = 0; i < size; i++) {
                        Map<String, String> map = listMap.get(i);
                        map.put(TYPE_KEY, String.valueOf(Type_recommed));
                        map.put("isAd", "1");
                        List<Map<String, String>> styleDataList = StringManager.getListMapByJson(map.get("styleData"));
                        handlerStyleData(map, styleDataList);
                    }
                    analysRelateData(listMap);
                    mArticleAdContrler.handlerAdData(allDataListMap);
                    loadManager.changeMoreBtn(flag, 10, 0, 3, false);
                } else
                    toastFaildRes(flag, true, object);
            }
        });
    }

    private Map<String, String> handlerStyleData(Map<String, String> map, List<Map<String, String>> styleDataList) {
        for (int index = 0; index < styleDataList.size(); index++) {
            Map<String, String> data = styleDataList.get(index);
            if ("1".equals(data.get("type"))) {
                map.put("img", data.get("url"));
                map.put("videoIconShow", "1");
                return map;
            }
        }
        for (int index = 0; index < styleDataList.size(); index++) {
            Map<String, String> data = styleDataList.get(index);
            if ("2".equals(data.get("type"))) {
                map.put("img", data.get("url"));
                map.put("videoIconShow", "2");
                return map;
            }
        }
        //特殊处理gif图时，img字段没有值的情况
        if (TextUtils.isEmpty(map.get("img"))) {
            for (int index = 0; index < styleDataList.size(); index++) {
                Map<String, String> data = styleDataList.get(index);
                if ("3".equals(data.get("type"))) {
                    map.put("img", data.get("url"));
                    map.put("videoIconShow", "1");
                    return map;
                }
            }
        }
        return map;
    }

    private boolean isRelateDataOk = false;
    /**
     * 解析推荐数据
     *
     * @param ArrayRelate
     */
    private void analysRelateData(@NonNull ArrayList<Map<String, String>> ArrayRelate) {
        if (ArrayRelate.isEmpty()) return;
        for (Map<String, String> map : ArrayRelate) {
            String clickAll = map.get("clickAll");
            map.put("clickAll", "0".equals(clickAll) ? "" : clickAll + "浏览");
            String commentNumber = map.get("commentNumber");
            map.put("commentNumber", "0".equals(commentNumber) ? "" : commentNumber + "评论");
        }
        if (page == 1)
            ArrayRelate.get(0).put("showheader", "1");
        allDataListMap.addAll(ArrayRelate);
        isRelateDataOk = true;

        detailAdapter.notifyDataSetChanged();
    }

    private void showBottomDialog() {
        ToolsDevice.keyboardControl(false, this, mArticleCommentBar);
        BottomDialog dialog = new BottomDialog(this);
        dialog.addButton("分享", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShare();
                statistics("更多", "分享");
            }
        }).addButton("编辑", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleDetailActivity.this, ArticleEidtActiivty.class);
                intent.putExtra("code", code);
                startActivity(intent);
                statistics("更多", "编辑");
                ArticleDetailActivity.this.finish();
            }
        }).addButton("删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog();
            }
        });
        dialog.show();
    }

    private Bitmap shareImageBitmap = null;

    private void handlerShareData() {
        if (!TextUtils.isEmpty(shareMap.get("img"))) {
            shareMap.put("imgType", BarShare.IMG_TYPE_WEB);

        } else {
            shareMap.put("img", String.valueOf(R.drawable.umen_share_launch));
            shareMap.put("imgType", BarShare.IMG_TYPE_RES);
        }
    }

    public String getMAPI() {
        return StringManager.replaceUrl(StringManager.api_article);
    }

    private void openShare() {
        if (shareMap.isEmpty()) {
            Tools.showToast(this, "数据处理中，请稍候");
            return;
        }

        barShare = new BarShare(ArticleDetailActivity.this,  "视频详情", "");
        String title = shareMap.get("title");
        String content = shareMap.get("content");
        String clickUrl = shareMap.get("url");
        String type = BarShare.IMG_TYPE_RES;
        String shareImg = "" + R.drawable.umen_share_launch;
        if (shareImageBitmap != null) {
            barShare.setShare(title, content, shareImageBitmap, clickUrl);
        } else {
            type = shareMap.get("imgType");
            shareImg = shareMap.get("img");
            barShare.setShare(type, title, content, shareImg, clickUrl);
        }
        barShare.openShare();
    }

    private void openDeleteDialog() {
        final XhDialog dialog = new XhDialog(this);
        dialog.setTitle("确定删除这篇文章吗？")
                .setCanselButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                })
                .setSureButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        deleteThis();
                        statistics("更多", "删除");
                    }
                }).show();
    }

    private void deleteThis() {
        ReqEncyptInternet.in().doEncypt(StringManager.api_articleDel, "code=" + code,
                new InternetCallback(ArticleDetailActivity.this) {
                    @Override
                    public void loaded(int flag, String url, Object obj) {
                        if (flag >= ReqEncyptInternet.REQ_OK_STRING) {
                            //自动关闭
                            ArticleDetailActivity.this.finish();
                            if (FriendHome.isAlive) {
                                Intent broadIntent = new Intent();
                                broadIntent.setAction(UploadStateChangeBroadcasterReceiver.ACTION);
                                broadIntent.putExtra(UploadStateChangeBroadcasterReceiver.DATA_TYPE, "2");
                                broadIntent.putExtra(UploadStateChangeBroadcasterReceiver.ACTION_DEL, "2");
                                Main.allMain.sendBroadcast(broadIntent);
                            }
                        } else {
                            toastFaildRes(flag, true, obj);
                        }
                    }
                });
    }

    public String getType() {
        return TYPE_ARTICLE;
    }

    private void statistics(String twoLevel, String threeLevel) {
        XHClick.mapStat(this, "a_ArticleDetail", twoLevel, threeLevel);
    }
}