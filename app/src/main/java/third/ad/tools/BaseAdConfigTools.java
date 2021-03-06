package third.ad.tools;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import acore.logic.XHClick;
import acore.override.XHApplication;
import acore.tools.StringManager;
import acore.tools.Tools;
import aplug.basic.InternetCallback;
import aplug.basic.ReqInternet;

/**
 * Date:2018/3/16.
 * Desc:
 * Author:SLL
 * Email:
 */

public class BaseAdConfigTools {


    BaseAdConfigTools() {

    }

    private void requestStatistics(String url, LinkedHashMap<String, String> params,String event) {
        ReqInternet.in().doPost(url, params, new InternetCallback() {
            @Override
            public void loaded(int flag, String url, Object returnObj) {
//                if("show".equals(event)){
//                    Log.i("tzy", "loaded: deleteAll");
//                    AdStatistics.getInstance().deleteAll();
//                }
            }
        });
    }

    /**
     * @param event：行为事件
     * @param gg_position_code：广告位id
     * @param gg_position_id：adPositionId
     * @param gg_business：广告商
     *
     * @param gg_business_id：自由广告id（只有自有才有该字段）
     */
    public void postStatistics(@NonNull String event, @NonNull String gg_position_code,@NonNull String gg_position_id,
                               @NonNull String gg_business, @NonNull String gg_business_id) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //时间
        map.put("app_time", Tools.getAssignTime("yyyy-MM-dd HH:mm:ss", 0));
        //行为事件
        map.put("event", event);
        //广告位code
        if (!TextUtils.isEmpty(gg_position_code)) {
            map.put("gg_position_code", gg_position_code);
        }
        //广告位id
        if (!TextUtils.isEmpty(gg_position_id)) {
            map.put("gg_position_id", gg_position_id);
        }
        //广告商
        if (!TextUtils.isEmpty(gg_business)) {
            map.put("gg_business", gg_business);
        }
        //广告商id
        if (!TextUtils.isEmpty(gg_business_id)) {
            map.put("gg_business_id", gg_business_id);
        }
        JSONObject jsonObject = MapToJsonEncode(map);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

//        Log.i("tongji", "gg_business: " + gg_business);
//        requestStatistics(StringManager.api_monitoring_9,params);
        switch (event) {
            case "click":
            case "download":
            case "show":
                StringBuffer sb = new StringBuffer();
                sb.append("ad_").append(event).append("_625");
                params.put("log_json", new JSONArray().put(jsonObject).toString());
                requestStatistics(StringManager.api_adsNumber, params,sb.toString());
                XHClick.mapStat(XHApplication.in(), sb.toString(), gg_business, gg_position_code);
                break;
        }
    }

    private static JSONObject MapToJsonEncode(Map<String, String> maps) {
        JSONObject jsonObject = new JSONObject();
        if (maps == null || maps.size() <= 0)
            return jsonObject;
        Iterator<Map.Entry<String, String>> enty = maps.entrySet().iterator();
        try {
            while (enty.hasNext()) {
                Map.Entry<String, String> entry = enty.next();
                jsonObject.put(entry.getKey(), Uri.encode(entry.getValue()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
