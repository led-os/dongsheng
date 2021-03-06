package aplug.basic;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import acore.logic.LoginManager;
import acore.logic.load.LoadManager;
import acore.override.XHApplication;
import acore.tools.FileManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import third.push.xg.XGPushServer;
import xh.basic.internet.InterCallback;

/**
 * 网络层回调的公共位置
 */
public class XHInternetCallBack extends InterCallback {
    private static Map<String, String> mapCookie = new ConcurrentSkipListMap<String, String>();//基础cookie的存储
    public static boolean isCookieChange = false;//cookie 是否需要变化记录

    public XHInternetCallBack() {
        super();
    }

    @Override
    public void loaded(int i, String s, Object o) {
    }
    /**
     * 处理基础cookie
     */
    public static void handlerBaseCookie() {
        if(mapCookie.size()>0)mapCookie.clear();
       String device= ToolsDevice.getPhoneDevice(XHApplication.in()) + ToolsDevice.getNetWorkType(XHApplication.in()) + "#"
                + ToolsDevice.getAvailMemory(XHApplication.in()) + "#" + ToolsDevice.getPackageName(XHApplication.in()) + "#"
                + StringManager.appID + "#" + LoadManager.tok + ";";
        mapCookie.put("device",device);
        mapCookie.put("xhCode", ToolsDevice.getXhCode(XHApplication.in()));//有疑问，测试处理
        mapCookie.put("xgCode" , XGPushServer.getXGToken(XHApplication.in()));
        mapCookie.put("lang" , ToolsDevice.getCurrentLanguage(XHApplication.in()));
        mapCookie.put("timeZone" , ToolsDevice.getCurrentTimeZone());
        mapCookie.put("imei" , ToolsDevice.getIMEI(XHApplication.in()));
        mapCookie.put("android_id" , ToolsDevice.getAndroidId(XHApplication.in()));
        handlerChangeData();
    }
    private static void handlerChangeData(){
        String location = getLocation();
        mapCookie.put("geo", TextUtils.isEmpty(location) ? "" : location);
    }
    /**
     * 获取当前cookie
     */
    public static Map<String,String> getCookieMap() {
        if (mapCookie.size() == 0) handlerBaseCookie();
        if (LoginManager.userInfo.containsKey("userCode")) {
            mapCookie.put("userCode", LoginManager.userInfo.get("userCode"));
        }else if (mapCookie.containsKey("userCode")) mapCookie.remove("userCode");
        if (isCookieChange) {
            handlerChangeData();
            isCookieChange = false;
            Log.i("xianghaTag","mapCookie:::"+mapCookie.toString());
        }
        String key= (String) FileManager.loadShared(XHApplication.in(),FileManager.key_header_mode,FileManager.key_header_mode);
        if(!TextUtils.isEmpty(key)){
            mapCookie.put("mode", key);
        }else if(mapCookie.containsKey("mode")){
            mapCookie.remove("mode");
        }
        return mapCookie;
    }
    public static void setCookie() {
    }
    public static void clearCookie(){
        if(mapCookie!=null&&mapCookie.size()>0)mapCookie.clear();
    }
    public static String getCookieStr() {
        return getCookieStr(null);
    }
    /**
     * 获取当前cookie
     */
    public static String getCookieStr(Map<String,String> headerMap) {
        Map<String,String> mapTemp = getCookieMap();
        if(headerMap!=null&&headerMap.size()>0){
            mapTemp.putAll(headerMap);
        }
        String cookieTemp= Tools.MapToJson(mapTemp).toString();
        return Uri.encode(cookieTemp);
    }

    private static String getLocation() {
        String location = FileManager.loadShared(XHApplication.in(), FileManager.file_location, FileManager.file_location)
                .toString();
        return location;
    }
    public static void setIsCookieChange(boolean isRefreshState){
        isCookieChange= isRefreshState;
    }
}
