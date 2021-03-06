package third.mall;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangha.R;

import java.util.Map;

import acore.logic.AppCommon;
import acore.logic.LoginManager;
import acore.logic.XHClick;
import acore.override.activity.base.BaseAppCompatActivity;
import acore.tools.FileManager;
import acore.tools.LogManager;
import acore.tools.Tools;
import amodule.main.Main;
import amodule.user.activity.login.LoginByAccout;
import aplug.basic.ReqInternet;
import aplug.basic.XHConf;
import aplug.basic.XHInternetCallBack;
import aplug.web.tools.JsAppCommon;
import aplug.web.tools.WebviewManager;
import aplug.web.view.XHWebView;
import third.mall.activity.ShoppingActivity;
import third.mall.alipay.MallPayActivity;
import third.mall.aplug.MallCommon;
import third.mall.aplug.MallReqInternet;
import third.mall.aplug.MallStringManager;
import xh.basic.tool.UtilFile;


/**
 * 商城activity页面
 * @author yu
 *
 */
public class MainMall extends BaseAppCompatActivity implements OnClickListener{
	public static final String KEY = "MainMall";

	// 加载管理
	private TextView mall_news_num;
	private TextView mall_news_num_two;
	public XHWebView webview = null;
	public static int webViewNum = 0;
	public WebviewManager webViewManager = null;
	private MallCommon common;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		setContentView(R.layout.a_mall);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		//在Main中保存首页的对象
//		Main.allMain.allTab.put(KEY, this);
		initActivity("",2, 0, 0, R.layout.a_mall);
		common= new MallCommon(this);
		initView();
//		init();
		XHClick.track(this,"浏览商城首页");
	}
	/**
	 * 初始化区分数据模块
	 */
	private void init() {
		if(Tools.isShowTitle()) {
			int topbarHeight = Tools.getDimen(this, R.dimen.topbar_height);
			int height = topbarHeight + Tools.getStatusBarHeight(this);

			RelativeLayout bar_title = (RelativeLayout) findViewById(R.id.mall_title_rela);
			RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
			bar_title.setLayoutParams(layout);
			bar_title.setPadding(0, Tools.getStatusBarHeight(this), 0, 0);
		}
	}
	/**
	 * 初始化布局
	 */
	private void initView() {
//		RelativeLayout mall_num_rela= (RelativeLayout) findViewById(R.id.mall_num_rela);
//		mall_num_rela.setOnClickListener(this);
//		mall_num_rela.setVisibility(View.VISIBLE);
		mall_news_num = (TextView) findViewById(R.id.mall_news_num);
		mall_news_num_two = (TextView) findViewById(R.id.mall_news_num_two);
//		findViewById(R.id.mall_order).setOnClickListener(this);
//		findViewById(R.id.btn_back).setOnClickListener(this);
//		findViewById(R.id.mall_title_rela).setOnClickListener(this);
//		findViewById(R.id.ed_search_layout_mall).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.back).setVisibility(View.VISIBLE);
		findViewById(R.id.shopping_layout).setOnClickListener(this);
		findViewById(R.id.shopping_layout).setVisibility(View.VISIBLE);
		//处理webView
		webViewManager = new WebviewManager(this,loadManager,false);
		webview = webViewManager.createWebView(R.id.XHWebview,false);
		webViewManager.setJSObj(webview, new JsAppCommon(this, webview,loadManager,null));
		// 设置加载
		loadManager.setLoading(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(MallCommon.ds_home_url)){
					MallCommon.getDsInfo(MainMall.this, loadManager);
				}else{
					loadData();
				}
			}
		});
	}
	public void loadData() {
		String homeMallUrl = MallStringManager.replaceUrl(MallCommon.ds_home_url);
		if (homeMallUrl.indexOf(MallStringManager.domain) > -1) {
			String cookieKey=MallStringManager.mall_web_apiUrl.replace(MallStringManager.appWebTitle, "");
			Map<String,String> mapCookie= XHInternetCallBack.getCookieMap();
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			for(String str:mapCookie.keySet()){
				String temp=str+"="+mapCookie.get(str);
				if(temp.indexOf("device")==0) temp=temp.replace(" ", "");
				LogManager.print(XHConf.log_tag_net,"d", "设置cookie："+temp);
				cookieManager.setCookie(cookieKey, temp);
			}
			CookieSyncManager.createInstance(this);
			CookieSyncManager.getInstance().sync();
			LogManager.print(XHConf.log_tag_net,"d", "设置webview的cookie："+mapCookie.toString());
		}
		LogManager.print(XHConf.log_tag_net,"d","------------------打开网页------------------\n"+MallCommon.ds_home_url);
		webview.loadUrl(homeMallUrl);
	}
	
	public void refresh(){
		loadData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("zyj","ma::::MallCommon.num_shopcat::"+MallCommon.num_shopcat);
		if(MallCommon.num_shopcat>0){
//			Main.setNewMsgNum(1,MallCommon.num_shopcat);
			if(MallCommon.num_shopcat>9){
				mall_news_num.setVisibility(View.GONE);
				mall_news_num_two.setVisibility(View.VISIBLE);
				if(MallCommon.num_shopcat>99)
					mall_news_num_two.setText("99+");
				else
					mall_news_num_two.setText(""+MallCommon.num_shopcat);
			}else{
				mall_news_num.setVisibility(View.VISIBLE);
				mall_news_num_two.setVisibility(View.GONE);
				mall_news_num.setText(""+MallCommon.num_shopcat);
			}
		}else{
//			Main.setNewMsgNum(1,0);
			mall_news_num.setVisibility(View.GONE);
			mall_news_num_two.setVisibility(View.GONE);
		}
//		Main.setNewMsgNum(Integer.parseInt(CommonBottomView.BOTTOM_TWO),MallCommon.num_shopcat);
		UtilFile.saveShared(this, FileManager.MALL_STAT, FileManager.MALL_STAT, "");
		if(LoginManager.isLogin()){
			MallCommon.getShoppingNum(this,mall_news_num,mall_news_num_two);
		}
		MallPayActivity.mall_state=false;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.mall_num_rela:
		case R.id.shopping_layout:
			if(LoginManager.userInfo.size() == 0){
				Intent intent = new Intent(MainMall.this, LoginByAccout.class);
				startActivity(intent);
			}else{
				XHClick.mapStat(this, "a_mall","购物车","");
				Intent intent= new Intent(MainMall.this,ShoppingActivity.class);
				intent=common.setStatistic("home_cart", intent);
				this.startActivity(intent);
			}

			break;
//		case R.id.mall_order:
//			if(LoginManager.userInfo.size() == 0){
//				Intent intent = new Intent(MainMall.this, UserLoginOptions.class);
//				startActivity(intent);
//			}else{
//				XHClick.mapStat(this, "a_mall","我的订单","");
//				Intent intent= new Intent(MainMall.this,MyOrderActivity.class);
//				intent.putExtra("icon_but", "home_order");
//				this.startActivity(intent);
//			}
//			break;
//		case R.id.mall_title_rela:
			case R.id.back:
				this.finish();
				break;
//		case R.id.ed_search_mall:
//		case R.id.ed_search_layout_mall:
//			XHClick.mapStat(this, "a_mall","搜索","");
//			Intent intent= new Intent(this,MallSearchActivity.class);
//			intent=common.setStatistic("home_search", intent);
//			this.startActivity(intent);
//
//			break;
		}
	}
	@Override
	public void finish() {
		//播放视频是退出需要loadUrl("")，重置web停止播放
		if(webview != null){
			webview.loadUrl("");
		}
		super.finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent != null){
			String url = intent.getStringExtra("url");
			if(!TextUtils.isEmpty(MallCommon.ds_home_url) && TextUtils.equals(MallCommon.ds_home_url,url)){
				loadData();
			}else if(!TextUtils.isEmpty(url)){
				AppCommon.openUrl(url,true);
			}
		}
	}

	@Override
	protected void onDestroy() {
		if(webview != null){
			webview.stopLoading();
			webview.removeAllViews();
			webview.destroy();
		}
		super.onDestroy();
	}
	

	public void scrollTop(){
		webview.setScrollY(0);
	}
}
