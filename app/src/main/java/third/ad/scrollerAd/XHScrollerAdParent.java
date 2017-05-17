package third.ad.scrollerAd;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.Map;

import acore.logic.LoginManager;
import acore.logic.XHClick;
import acore.override.XHApplication;
import third.ad.tools.AdConfigTools;

/**
 * 广告父类
 */
public abstract class XHScrollerAdParent {
    public static final String ADKEY_GDT = "sdk_gdt";
    public static final String ADKEY_INMOBI = "sdk_inmobi";
    public static final String ADKEY_API = "api_tfp";
    public static final String ADKEY_BANNER = "xh";

    public static final String TAG_GDT = "gdt";
    public static final String TAG_INMOBI = "inmobi";
    public static final String TAG_API = "api";
    public static final String TAG_BANNER = "personal";

    public int num;//当前存在的位置--针对的是一个广告位
    public String mAdPlayId="";//广告位置id
    public int index;//当前存在的位置---针对于广集合的位置
    public View view;
    public String key="";
    private boolean isQuanList=false;
    public XHScrollerAdParent(String mAdPlayId,int num){
        this.mAdPlayId=mAdPlayId;
        this.num= num;
    }
    /**
     * 广告曝光，onResume时调用
     */
    public abstract void onResumeAd(String oneLevel,String twoLevel);
    /**
     * 广告不显示：onPause时调用
     */
    public abstract void onPsuseAd();

    /**
     * 获取广告数据
     * @param  xhAdDataCallBack 请求数据回调
     */
    public abstract void getAdDataWithBackAdId(@NonNull XHAdDataCallBack xhAdDataCallBack);

    /**
     * 第三方点击
     */
    public abstract void onThirdClick(String oneLevel,String twoLevel);

    /**
     * 设置当地展示的view
     * @param view
     */
    public void setShowView(View view){
        if(this.view!=null)this.view=null;
        this.view=view;
    }

    //释放view，避免内存泄漏
    public void realseView(){
        this.view = null;
    }

    /**
     * 获取当前状态view状态
     * @return false view不为null,true view 为null
     */
    public boolean getViewState(){
        if(view!=null)
            return false;
        return true;
    }

    public void setIndexControl(int index){
        this.index= index;
    }

    protected void onAdClick(String oneLevel,String twoLevel,String threeLevel){
        postTongji(key, "0", "click");
        //umeng的统计
        XHClick.mapStat(XHApplication.in(), oneLevel, twoLevel, threeLevel);
        //自己网站的统计
        if(isQuanList)
            //美食圈列表
            AdConfigTools.getInstance().clickAds(mAdPlayId);
        else
            //其他统计
            AdConfigTools.getInstance().clickAds(mAdPlayId,key,"0");
        //统计点击
        if(!ADKEY_BANNER.equals(key)){
            XHClick.track(XHApplication.in(),"点击广告");
        }
    }

    /**
     * 广告展示统计
     */
    protected void onAdShow(String oneLevel,String twoLevel,String threeLevel){
        //自己网站上的统计
        postTongji(key, "0", "show");
        XHClick.mapStat(XHApplication.in(), oneLevel, twoLevel, threeLevel);
    }


    private void postTongji(String channel,String bannerId,String event){
        AdConfigTools.getInstance().postTongji("", channel, bannerId, event, "普通广告位");
    }

    /**
     * 请求数据回调
     */
    public interface XHAdDataCallBack{
        public void onSuccees(String type, Map<String,String> map);
        public void onFail(String type);
    }

    /**
     * 设置当前是否数据集合
     * @param state
     */
    public void setIsQuanList(boolean state){
        this.isQuanList= state;
    }

    /**
     * 判断是否显示
     * @return
     */
    public boolean isShow(){
        if(key.equals(ADKEY_BANNER)){//xh自有广告全部显示
            return true;
        }else{
            return LoginManager.isShowAd();
        }
    }
}
