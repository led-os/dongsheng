package aplug.web.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import acore.logic.AppCommon;
import acore.logic.load.LoadManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import aplug.basic.XHConf;
import aplug.web.view.XHWebView;
import xh.basic.internet.UtilInternet;
import xh.basic.tool.UtilString;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class WebviewManager {
	private final String ERROR_HTML_URL = "file:///android_asset/error.html";
	private Activity act;
	private LoadManager loadManager;
	private List<XHWebView> mWwebArray;
	private boolean state=true;
	
	/**
	 * 初始化
	 * @param act
	 * @param loadManager
	 * @param state---true为正常web页面 false:为电商首页
	 */
	public WebviewManager(Activity act,LoadManager loadManager,boolean state){
		this.act = act;
		this.loadManager = loadManager;
		mWwebArray = new ArrayList<XHWebView>();
		this.state= state;
	}

	public XHWebView createWebView(int id) {
		if(act == null){
			return null;
		}
		XHWebView webview = null;
		if(id > 0){
			webview = (XHWebView) act.findViewById(id);
		}
		if (webview == null)
			webview = new XHWebView(act);
		CookieSyncManager.createInstance(act);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();//移除无过期时间的cookie
		//对webview进行放大------在pad上运行
//		float scale=Tools.getDimen(act,R.dimen.dp_1);
//		if(scale >= 1.6) {
//			webview.setInitialScale(300);
//		}
		//初始化WebSetting
		initWebSetting(webview);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		//设置WebViewClient
		setWebViewClient(webview);
		//设置WebChromeClient
		setWebChromeClient(webview);
		mWwebArray.add(webview);
		return webview;
	}

	public void setJSObj(XHWebView webview,JsBase jsObj) {
		webview.addJavascriptInterface(jsObj, jsObj.TAG);
	}
	
	public void setJSObjs(XHWebView webview,JsBase[] jsObjs) {
		for(JsBase jsObj : jsObjs){
			setJSObj(webview, jsObj);
		}
	}

	/**
	 * 初始化WebSetting
	 * @param webview
	 */
	@SuppressWarnings("deprecation")
	private void initWebSetting(XHWebView webview) {
		WebSettings settings = webview.getSettings();
		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setAppCacheEnabled(false);
		settings.setDomStorageEnabled(true);
		settings.setAllowFileAccess(true);


		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setSavePassword(false);
		settings.setDefaultTextEncodingName("utf-8");
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
	}

	/**
	 * 设置WebViewClient
	 * @param webview
	 */
	private void setWebViewClient(final XHWebView webview) {
		webview.setWebViewClient(new WebViewClient() {
			private Timer timer;
			private Handler handler = new Handler();

			@Override
			public void onPageStarted(final WebView view, String url, Bitmap favicon) {
				timer = new Timer();
				TimerTask tt = new TimerTask() {

					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (webview.getProgress() < 90) {
									if(!state){
										loadManager.loadOver(UtilInternet.REQ_OK_STRING, 1,true);
									}
								}
							}
						});
					}
				};
				timer.schedule(tt, XHConf.net_timeout);
				if(!ERROR_HTML_URL.equals(url)){
					webview.setUrl(url);
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (JSAction.loadAction.length() > 0) {
					view.loadUrl("javascript:" + JSAction.loadAction + ";");
					JSAction.loadAction = "";
				}
				if(url.indexOf(StringManager.api_exchangeList) != 0 && url.indexOf(StringManager.api_scoreList) != 0){
					//读取title设置title
					view.loadUrl("javascript:window.appCommon.setTitle(document.title);");
				}
				loadManager.loadOver(UtilInternet.REQ_OK_STRING, 1,true);
				if(timer != null){
					timer.cancel();
					timer.purge();
				}
				// 获取焦点已让webview能打开键盘，评论页输入框不在webview，所以不获取焦点
				if (url.indexOf("subjectComment.php") == -1) {
					view.requestFocus();
				}
				// 读取cookie的sessionId
				CookieManager cookieManager = CookieManager.getInstance();
				Map<String, String> map = UtilString.getMapByString(cookieManager.getCookie(url),";", "=");
				String sessionId = UtilInternet.cookieMap.get("USERID");
				if (map.get("USERID") != null && !map.get("USERID").equals(sessionId == null ? "" : sessionId)) {
					UtilInternet.cookieMap.put("USERID", map.get("USERID"));
				}
				/**
				 * 有问题客户端关闭该功能
				 */
				//获取网页高度，重新设置webview高度
//				webview.loadUrl("javascript:appCommon.resize(document.body.getBoundingClientRect().height)");
			}
			
			// 当前页打开
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				if(state){
					loadManager.setLoading(new OnClickListener() {
						@Override
						public void onClick(View v) {
							AppCommon.openUrl(act, url, true);
						}
					});
				}else{
					AppCommon.openUrl(act, url, true);
				}
				return true;
			}

			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler sslhandler, SslError error) {
				sslhandler.proceed();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				webview.loadUrl(ERROR_HTML_URL);
			}
		});
	}

	/**
	 * 设置WebChromeClient
	 * @param webview
	 */
	private void setWebChromeClient(final XHWebView webview) {
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				Tools.showToast(view.getContext(), message);
				result.cancel();
				loadManager.hideProgressBar();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
				showTip(message, result);
				return true;
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
			}
			
			//弹出提示
			private void showTip(String message, final JsResult result) {
				Builder builder = new Builder(act);
				builder.setTitle("提示");
				builder.setMessage(message);
				builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}

				});
				builder.setNeutralButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}

				}).setCancelable(false).create().show();
			}
			
		});
	}
	
}
