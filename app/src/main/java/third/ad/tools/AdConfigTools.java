package third.ad.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.popdialog.util.FullScreenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import acore.logic.LoginManager;
import acore.logic.XHClick;
import acore.override.XHApplication;
import acore.tools.FileManager;
import acore.tools.StringManager;
import acore.tools.ToolsDevice;
import amodule.main.Main;
import aplug.basic.InternetCallback;
import aplug.basic.LoadImage;
import aplug.basic.ReqInternet;
import aplug.basic.SubBitmapTarget;
import third.ad.AdParent;
import third.ad.db.XHAdSqlite;
import third.ad.db.bean.AdBean;

import static third.ad.tools.AdPlayIdConfig.FULL_SRCEEN_ACTIVITY;

public class AdConfigTools {
    private volatile static AdConfigTools mAdConfigTools;

    private String showAdId = "cancel";

    public boolean isLoadOver = false;

    private AdConfigTools() {
    }

    public ArrayList<Map<String, String>> list = new ArrayList<>();//服务端广告集合

    public static AdConfigTools getInstance() {
        if (mAdConfigTools == null) {
            synchronized (AdConfigTools.class) {
                if (mAdConfigTools == null) {
                    mAdConfigTools = new AdConfigTools();
                }
            }
        }
        return mAdConfigTools;
    }

    public void getAdConfigInfo() {
        getAdConfigInfo(null);
    }

    public void getAdConfigInfo(InternetCallback callback){
        // 请求网络信息
        ReqInternet.in().doGet(StringManager.api_adData, new InternetCallback() {
            @Override
            public void loaded(int flag, String url, final Object returnObj) {
                if (flag >= ReqInternet.REQ_OK_STRING) {
                    //更新广告配置
                    XHAdSqlite adSqlite = XHAdSqlite.newInstance(XHApplication.in());
                    adSqlite.updateConfig((String) returnObj);
                    //更新全屏广告数据
                    Map<String,String> map = StringManager.getFirstMap(returnObj);
                    if(map.containsKey(FULL_SRCEEN_ACTIVITY)){
                        final String path = FileManager.getDataDir() + FULL_SRCEEN_ACTIVITY + ".xh";
                        FileManager.saveFileToCompletePath(path, map.get(FULL_SRCEEN_ACTIVITY), false);
                    }
                    isLoadOver = true;
                    if(callback != null){
                        callback.loaded(ReqInternet.REQ_OK_STRING,url,returnObj);
                    }
                }
            }
        });
    }

    /**
     * 请求美食圈列表广告
     *
     * @param context
     */
    public void setRequest(Context context) {
        String url = StringManager.api_getQuanList;
        ReqInternet.in().doGet(url, new InternetCallback() {
            @Override
            public void loaded(int flag, String url, Object msg) {
                if (flag >= ReqInternet.REQ_OK_STRING) {
                    list = StringManager.getListMapByJson(msg);
                }
            }
        });
    }

//    public String getAdConfigDataString(String adPlayId) {
//        String data = FileManager.readFile(FileManager.getDataDir() + FileManager.file_ad);
//        Map<String, String> map = StringManager.getFirstMap(data);
//        return map.get(adPlayId);
//    }

//    public Map<String, String> getAdConfigData(String adPlayId) {
//        String data = FileManager.readFile(FileManager.getDataDir() + FileManager.file_ad);
//        Map<String, String> map = StringManager.getFirstMap(data);
//        map = StringManager.getFirstMap(map.get(adPlayId));
//        return map;
//    }

    public AdBean getAdConfig(String adPlayId){
        XHAdSqlite adSqlite = XHAdSqlite.newInstance(XHApplication.in());
        return adSqlite.getAdConfig(adPlayId);
    }

    /**
     * 通过搜菜谱，输入指定指令显示对应广告
     *
     * @param ad
     */
    public void changeAd(String ad) {
        if ("gdt".equals(ad)) {
            showAdId = AdParent.ADKEY_GDT;
        } else if ("banner".equals(ad)) {
            showAdId = AdParent.ADKEY_BANNER;
        } else if ("cancel".equals(ad)) {
            showAdId = "cancel";
        }
    }

    public boolean isShowAd(String adPlayId, String adKey) {
        if ("cancel".equals(showAdId)) {
            String isGourmet = LoginManager.userInfo.get("isGourmet");
            //是美食家，但不是banner广告则返回不显示广告
            if ("2".equals(isGourmet) && !AdParent.ADKEY_BANNER.equals(adKey)) {
                return false;
            }
            AdBean adBean = getAdConfig(adPlayId);
            if(adBean != null){
                switch (adKey){
                    case AdParent.ADKEY_GDT:
                        return "2".equals(adBean.isGdt);
                    case AdParent.ADKEY_BANNER:
                        return "2".equals(adBean.isBanner);
                    default:break;
                }
            }
        } else if ("level".equals(showAdId)) {
            return true;
        } else {
            return adKey.equals(showAdId);
        }
        return false;
    }

    public void onAdShow(Context context, String channel, String twoLevel, String threeLevel) {
        if (TextUtils.isEmpty(twoLevel)) return;
        if (AdParent.TONGJI_TX_API.equals(channel))
            XHClick.mapStat(context, "ad_show", twoLevel, threeLevel);
    }

    public void onAdClick(Context context, String channel, String twoLevel, String threeLevel) {
        if (TextUtils.isEmpty(twoLevel)) return;
        if (AdParent.TONGJI_TX_API.equals(channel))
            XHClick.mapStat(context, "ad_click", twoLevel, threeLevel);
    }

    /**
     * 普通广告位统计
     *
     * @param adPlayId ： 广告位id
     * @param channel  ：渠道 baidu、jingdong、banner
     * @param bannerId ：bannerId
     * @param event    事件  click：点击   show：展现
     * @param adType   广告类型  普通广告位，开屏广告位
     */
    public void postTongji(String adPlayId, String channel, String bannerId, String event, String adType) {
        StringBuffer urlBuffer = new StringBuffer(StringManager.api_monitoring_5)
                .append("?").append("adType=").append(adType)
                .append("&").append("id=").append(adPlayId)
                .append("&").append("channel=").append(channel)
                .append("&").append("bannerId=").append(bannerId)
                .append("&").append("event=").append(event);
        ReqInternet.in().doGet(urlBuffer.toString(), new InternetCallback() {
            @Override
            public void loaded(int flag, String url, Object returnObj) {
            }
        });
    }

    /**
     * 美食圈列表广告统计
     *
     * @param context
     * @param map
     * @param onClickSite 点击的位置 overall：整体、user：用户、time：时间、quanName：圈子名称、content：评论、like：赞
     */
    public void postTongjiQuan(Context context, Map<String, String> map, String onClickSite, String event) {
        String url = StringManager.api_monitoring_5;
        if (TextUtils.isEmpty(onClickSite)) onClickSite = "overall";
        else if ("用户头像".equals(onClickSite)) {
            onClickSite = "user";
        } else if ("用户昵称".equals(onClickSite)) {
            onClickSite = "user";
        } else if ("贴子内容".equals(onClickSite)) {
            onClickSite = "overall";
        } else if ("评论".equals(onClickSite)) {
            onClickSite = "content";
        } else {
            onClickSite = "overall";
        }

        ReqInternet.in().doGet(url + "?adType=圈子广告位" + "&adid=" + map.get("showAdid") + "&cid=" + map.get("showCid") +
                "&mid=" + map.get("showMid") + "site=" + map.get("showSite") + "&event=" + event + "&clickSite=" + onClickSite, new InternetCallback() {
            @Override
            public void loaded(int flag, String url, Object msg) {
            }
        });
    }

    /**
     * 广告位 点击
     *
     * @param id       广告位id
     * @param adType   baidu jingdong banner
     * @param adTypeId 广告类型id，第三方广告可传0
     */
    public void clickAds(String id, String adType, String adTypeId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("type", "position");
        map.put("id", id);
        map.put("adType", adType);
        map.put("adTypeId", adTypeId);
        ReqInternet.in().doPost(StringManager.api_clickAds, map, new InternetCallback() {
            @Override
            public void loaded(int flag, String url, Object returnObj) {

            }
        });
    }

    /**
     * 生活圈列表 广告点击
     *
     * @param id 广告id
     */
    public void clickAds(String id) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("type", "quanList");
        map.put("id", id);
        ReqInternet.in().doPost(StringManager.api_clickAds, map, new InternetCallback() {
            @Override
            public void loaded(int flag, String url, Object returnObj) {

            }
        });
    }
}
