package third.ad.control;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import acore.override.helper.XHActivityManager;
import third.ad.option.AdOptionHomeDish;
import third.ad.tools.AdPlayIdConfig;

/**
 * Created by Fang Ruijiao on 2017/5/5.
 */
public class AdControlHomeDish extends AdControlParent{

    private static AdControlHomeDish mAdControlHomeDishUnload;

    private String statisticKey = "index_listgood";
    //推荐页面向下加载时的广告
    private static final Integer[] AD_INSTERT_INDEX_0 = new Integer[]{3, 9};
    //推荐页面向上加载时的广告
    private static final Integer[] AD_INSTERT_INDEX = new Integer[]{3, 9, 16, 24, 32, 40, 48, 56, 64, 72};

    private Map<Integer,AdOptionHomeDish> adControlMap; //广告控制集合
    private int currentControlTag = -1;
    private int adControlNum = 1;
    private int nextAdNum = 2;

    private AdControlHomeDish(){
        adControlMap = new HashMap<>();
        AdOptionHomeDish downLoadAdControl0 = new AdOptionHomeDish(AdPlayIdConfig.MAIN_HOME_RECOMENT_LIST_0, AD_INSTERT_INDEX_0);
        downLoadAdControl0.getAdData(XHActivityManager.getInstance().getCurrentActivity(),statisticKey,"0");
        adControlMap.put(0,downLoadAdControl0);
        AdOptionHomeDish downLoadAdControl1 = new AdOptionHomeDish(AdPlayIdConfig.MAIN_HOME_RECOMENT_LIST, AD_INSTERT_INDEX);
        downLoadAdControl1.getAdData(XHActivityManager.getInstance().getCurrentActivity(),statisticKey,"1");
        adControlMap.put(1,downLoadAdControl1);

        AdOptionHomeDish adControlParent = new AdOptionHomeDish(AdPlayIdConfig.MAIN_HOME_RECOMENT_LIST, AD_INSTERT_INDEX);
        adControlParent.getAdData(XHActivityManager.getInstance().getCurrentActivity(),statisticKey,String.valueOf(++adControlNum));
        adControlMap.put(adControlNum,adControlParent);
        Log.i("FRJ","首页加载数据");
    }

    public static AdControlHomeDish getInstance(){
        if(mAdControlHomeDishUnload == null){
            mAdControlHomeDishUnload = new AdControlHomeDish();
        }
        return mAdControlHomeDishUnload;
    }

    /**
     * 加载g广告数据
     * @param old_list ：原数据体
     * @param isBack ：是否是向上加载的数据
     * @return
     */
    @Override
    public ArrayList<Map<String, String>> getNewAdData(ArrayList<Map<String, String>> old_list,boolean isBack) {
        //向上加载数据,则循环找到广告
        AdOptionHomeDish adControlParent = getCurrentControl(isBack);
        if(adControlParent == null){
            return old_list;
        }else{
            old_list = adControlParent.getNewAdData(old_list,isBack);
            //判断是否需要提前加载广告数据
            if(isBack &&  adControlParent.getIsLoadNext() && currentControlTag > adControlMap.size() - nextAdNum){
                AdOptionHomeDish adControl = new AdOptionHomeDish(AdPlayIdConfig.MAIN_HOME_RECOMENT_LIST, AD_INSTERT_INDEX);
                adControl.getAdData(XHActivityManager.getInstance().getCurrentActivity(),statisticKey,String.valueOf(++adControlNum));
                adControlMap.put(adControlNum,adControl);
                Log.i("FRJ","预加载 控制类:" + currentControlTag);
            }
            return old_list;
        }
    }

    private AdOptionHomeDish getCurrentControl(boolean isBack){
        AdOptionHomeDish adControlHomeDish = null;
        if(isBack) {
            if (adControlMap.size() > nextAdNum) {
                if (currentControlTag < nextAdNum) {
                    currentControlTag = nextAdNum;
                }
                if (currentControlTag < adControlMap.size()) {
                    adControlHomeDish = adControlMap.get(currentControlTag);
                    if (!adControlHomeDish.getIsHasNewData()) {
                        currentControlTag++;
                        Log.i("FRJ", "这个控制类没有了数据，切换下一个currentControlTag :" + currentControlTag);
                        adControlHomeDish = getCurrentControl(isBack);
                    }
                }
            }
        }else{
            AdOptionHomeDish adOptionHomeDish1 = adControlMap.get(1);
            if(adOptionHomeDish1.getHasData())
                return adOptionHomeDish1;
            adControlHomeDish = adControlMap.get(0);
        }
        return adControlHomeDish;
    }

    @Override
    public void onAdClick(Map<String, String> map) {
        String controlTag = map.get("controlTag");
        Log.i("FRJ","onAdClick controlTag:" + controlTag);
        if(!TextUtils.isEmpty(controlTag)){
            AdOptionHomeDish adOptionHomeDish = adControlMap.get(Integer.parseInt(controlTag));
            adOptionHomeDish.onAdClick(map);
        }
    }

    @Override
    public void onAdHintClick(Activity act, Map<String, String> map, String eventID, String twoLevel) {
        String controlTag = map.get("controlTag");
        if(!TextUtils.isEmpty(controlTag)){
            AdOptionHomeDish adOptionHomeDish = adControlMap.get(Integer.parseInt(controlTag));
            adOptionHomeDish.onAdHintClick(act,map,eventID,twoLevel);
        }
    }

    @Override
    public void onAdShow(Map<String, String> map, View view) {
        String controlTag = map.get("controlTag");
        Log.i("FRJ","onAdShow controlTag:" + controlTag);
        if(!TextUtils.isEmpty(controlTag)){
            AdOptionHomeDish adOptionHomeDish = adControlMap.get(Integer.parseInt(controlTag));
            adOptionHomeDish.onAdShow(map,view);
        }
    }
}
