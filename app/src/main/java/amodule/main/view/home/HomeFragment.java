package amodule.main.view.home;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.utils.VDPlayPauseHelper;
import com.xiangha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import acore.logic.XHClick;
import acore.logic.load.AutoLoadMore;
import acore.logic.load.LoadManager;
import acore.override.activity.mian.MainBaseActivity;
import acore.tools.FileManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import amodule.main.adapter.AdapterHome;
import amodule.main.adapter.AdapterListView;
import amodule.main.bean.HomeModuleBean;
import amodule.main.view.HomeTabHScrollView;
import amodule.main.view.ReplayAndShareView;
import amodule.main.view.item.HomeAlbumItem;
import amodule.main.view.item.HomeItem;
import amodule.main.view.item.HomePostItem;
import amodule.main.view.item.HomeRecipeItem;
import amodule.main.view.item.HomeTxtItem;
import amodule.quan.view.VideoImageView;
import aplug.basic.InternetCallback;
import aplug.basic.ReqEncyptInternet;
import aplug.basic.ReqInternet;
import cn.srain.cube.views.ptr.PtrClassicFrameLayout;
import third.ad.control.AdControlHomeDish;
import third.ad.control.AdControlParent;
import third.ad.option.AdOptionList;
import third.ad.option.AdOptionParent;
import third.ad.tools.AdPlayIdConfig;
import third.share.BarShare;

import static amodule.main.activity.MainHome.tag;
import static com.xiangha.R.id.return_top;

public class HomeFragment extends Fragment{
    /** 保存板块信息的key */
    protected static final String MODULEDATA = "moduleData";
    private HomeModuleBean homeModuleBean;//数据的结构
    private LoadManager mLoadManager = null;
    private MainBaseActivity mActivity;
    private View mView;
    private PtrClassicFrameLayout refreshLayout;
    private ListView mListview;
    private ImageView returnTop;
    private View homeHeaderDataNum;//数据条数view
    private TextView show_num_tv;

    private boolean isAutoPaly = false;//是否是wifi状态
    /** 是否加载完成 */
    private boolean LoadOver = false;
    /** 是否初始化 */
    protected boolean isPrepared = false;
    /** 是否显示 */
    protected boolean isVisible;
    /** 当前在viewpager中位置 */
    private int position=-1;
    /** 当前二级内容中位置 */
    private int twoPosition=-1;
    /** 帖子的数据集合 */
    private ArrayList<Map<String, String>> mListData = new ArrayList<>();

    private String backUrl= "";//向上拉取数据集合
    private String nextUrl="";//下页拉取数据集合
//    private AdapterHome adapterHome;
    private AdapterListView adapterListView;
    private boolean isloadTwodata=false;//是否加载过二级数据
    private LinearLayout layout,linearLayoutOne,linearLayoutTwo,linearLayoutThree;//头部view
    private String reset_time="";//向上刷新的时间戳
    private String backUrlBefore="";//之前的数据体---目前只有推荐使用了

    private AdControlParent mAdControl;
    private int beforNum = 0;
    private boolean isRecoment = false,isDayDish = false,isSetIndex = false;
    private static final Integer[] AD_INSTERT_INDEX = new Integer[]{3,9,16,24,32,40,48,56,64,72};

    private VideoImageView mVideoImageView;
    private RelativeLayout mVideoLayout;
    private ReplayAndShareView mReplayAndShareView;

    private String statisticKey = null;
    private int mHeaderCount;
    /**正在播放的位置，默认-1，即没有正在播放的*/
    private int mPlayPosition = -1;
    private View mPlayParentView = null;
    private boolean compelClearData= false;//强制清楚数据
    private boolean isScrollData= false;//是否滚动数据
    private int scrollDataIndex=-1;//滚动数据的位置
    private boolean isRecom=false;//是否是推荐
    private long statrTime= -1;//开始的时间戳
    private boolean isNextUrl=true;//执行数据有问题时，数据请求，只执行一次。

    public static HomeFragment newInstance(HomeModuleBean moduleBean) {
        HomeFragment fragment = new HomeFragment();
        fragment.setPosition(moduleBean.getPosition());
        fragment.setmoduleBean(moduleBean);
        return (HomeFragment) setArgumentsToFragment(fragment, moduleBean);
    }
    /** 将储块信息存板到Argument中 */
    public static Fragment setArgumentsToFragment(Fragment fragment, HomeModuleBean moduleBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MODULEDATA, moduleBean);
        fragment.setArguments(bundle);
        return fragment;
    }
    public HomeModuleBean getCurrentModuleData() {
        HomeModuleBean moduleBean = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            moduleBean = (HomeModuleBean) bundle.getSerializable(MODULEDATA);
        }
        return moduleBean;
    }
    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainBaseActivity) activity;
        super.onAttach(activity);
        isAutoPaly = "wifi".equals(ToolsDevice.getNetWorkSimpleType(activity));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取数据
        homeModuleBean=getCurrentModuleData();
        mAdControl = getAdControl();
    }

    public AdControlParent getAdControl(){
        String type = homeModuleBean.getType(); //当前页的type
        Log.i("FRJ","type:" + type);
        if(TextUtils.isEmpty(type)){
            return null;
        }
        if("recom".equals(type)){ //推荐
            isRecoment = true;
            return AdControlHomeDish.getInstance().getTwoLoadAdData();
        }else{
            AdOptionParent adControlParent = null;
            String[] adPlayIds = new String[0];
            Integer[] adIndexs = AD_INSTERT_INDEX;
            boolean isSetAd = true;
            if("video".equals(type)){ //视频
                statisticKey = "sp_list";
                adPlayIds = AdPlayIdConfig.MAIN_HOME_VIDEO_LIST;
            }else if("article".equals(type)){ //涨知识
                statisticKey = "other_top_list";
                adPlayIds = AdPlayIdConfig.MAIN_HOME_ZHISHI_LIST;
            }else if("day".equals(type)){ //每日三餐
                statisticKey = "other_threeMeals_list";
                isDayDish = true;
                adPlayIds = AdPlayIdConfig.COMMEND_THREE_MEALS;
                adIndexs = new Integer[]{};
            }else if("dish".equals(type)){ //本周佳作
                statisticKey = "jz_list";
                adPlayIds = AdPlayIdConfig.MAIN_HOME_WEEK_GOOD_LIST;
            }else{
                isSetAd = false;
            }
            if(isSetAd && adControlParent == null){
                adControlParent = new AdOptionList(adPlayIds,adIndexs) {
                    @Override
                    public Map<String, String> getAdListItemData(String title, String desc, String iconUrl, String imageUrl, String adTag) {
                        return setAdData(title,desc,iconUrl,imageUrl,adTag);
                    }
                };
                adControlParent.getAdData(mActivity,statisticKey);

                return new AdControlParent(adControlParent);
            }
        }
        return null;
    }


    private Map<String, String> setAdData(String title, String desc, String iconUrl, String imageUrl, String adTag){
        Map<String, String> map = new HashMap<>();
        map.put("name", title);
        map.put("img", imageUrl);
        map.put("content",desc);
        map.put("allClick", String.valueOf(Tools.getRandom(6000,20000)));
        map.put("commentNum", String.valueOf(Tools.getRandom(5,20)));
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nickName",title);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("customer",jsonArray.toString());

        return map;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.home_fragment, null);
        //header布局
        initHeaderView();
        refreshLayout = (PtrClassicFrameLayout) mView.findViewById(R.id.refresh_list_view_frame);
        //解决横滑冲突
        refreshLayout.disableWhenHorizontalMove(true);
        homeHeaderDataNum=mView.findViewById(R.id.homeHeaderNum);
        show_num_tv= (TextView) mView.findViewById(R.id.show_num_tv);
        homeHeaderDataNum.setVisibility(View.GONE);
        mListview = (ListView) mView.findViewById(R.id.v_scroll);
        mListview.addHeaderView(layout);
        mHeaderCount++;
        returnTop = (ImageView) mView.findViewById(return_top);
        mLoadManager = mActivity.loadManager;
        LoadOver = false;
        isPrepared = true;
        preLoad();
        return mView;
    }

    /**
     * 初始化header布局
     */
    private void initHeaderView(){
        //initHeaderView
        layout= new LinearLayout(mActivity);
        layout.setOrientation(LinearLayout.VERTICAL);
        linearLayoutOne= new LinearLayout(mActivity);
        linearLayoutOne.setOrientation(LinearLayout.VERTICAL);
        linearLayoutTwo= new LinearLayout(mActivity);
        linearLayoutTwo.setOrientation(LinearLayout.VERTICAL);
        linearLayoutThree= new LinearLayout(mActivity);
        linearLayoutThree.setOrientation(LinearLayout.VERTICAL);
        linearLayoutOne.setVisibility(View.GONE);
        linearLayoutTwo.setVisibility(View.GONE);
        linearLayoutThree.setVisibility(View.GONE);
        layout.addView(linearLayoutOne);
        layout.addView(linearLayoutTwo);
        layout.addView(linearLayoutThree);
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    protected void onVisible() {
        preLoad();
        if (mLoadManager != null && mListview != null) {
            Button loadMore = mLoadManager.getSingleLoadMore(mListview);
            if (loadMore != null) {
                loadMore.setVisibility(mListData.size() == 0 ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }
    protected void onInvisible() {
    }
    protected void preLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        //填充各控件的数据
        //防止二次生成
        if (!LoadOver) {
            statrTime=System.currentTimeMillis();//第一次设置开始时间戳
            initData();
        }
    }


    /**
     * 初始化数据
     */
    private void initData() {
        adapterListView = new AdapterListView(mListview,mActivity,mListData,mAdControl);
        adapterListView.setHomeModuleBean(homeModuleBean);
        adapterListView.setViewOnClickCallBack(new AdapterHome.ViewClickCallBack() {
            @Override
            public void viewOnClick(boolean isOnClick) {
                refresh();
            }
        });
        adapterListView.setVideoClickCallBack(new HomeRecipeItem.VideoClickCallBack() {
            @Override
            public void videoOnClick(int position) {
                int firstVisiPosi = mListview.getFirstVisiblePosition();
                View parentView = mListview.getChildAt(position-firstVisiPosi + mHeaderCount);
                setVideoLayout(parentView,position);
            }
        });
        if(!LoadOver){
                mLoadManager.setLoading(refreshLayout, mListview, adapterListView, true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(statisticKey)){
                            mAdControl.getAdData(mActivity,statisticKey);
                        }
                        EntryptData(true);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EntryptData(!LoadOver);
                    }
                }, new AutoLoadMore.OnListScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        stopVideo();
                    }
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        isScrollData=true;
                        if(scrollDataIndex<(firstVisibleItem+visibleItemCount-1)) {
                            scrollDataIndex = (firstVisibleItem + visibleItemCount-1);
                        }
                        if (mPlayPosition != -1) {
                            //正在播放的视频滑出屏幕
                            if ((mPlayPosition + mHeaderCount) < firstVisibleItem || (mPlayPosition + mHeaderCount) > (firstVisibleItem + visibleItemCount - 1)) {
                                stopVideo();
                            }
                        }
                    }
                });
        }
        //处理推荐的置顶数据
        if(homeModuleBean.getType().equals("recom")) {
            String url = StringManager.API_RECOMMEND_TOP;
            ReqEncyptInternet.in().doEncyptAEC(url, "", new InternetCallback(mActivity) {
                @Override
                public void loaded(int flag, String url, Object object) {
                    if (flag >= ReqInternet.REQ_OK_STRING) {
                        handlerTopData(object);
                    }
                }
            });
        }

    }

    /**
     * 请求数据入口
     * @param refresh，是否刷新
     */
    private void EntryptData(final boolean refresh){
        String params="";
        LoadOver = true;
//        mLoadManager.showProgressBar();
        if(refresh){//向上翻页
            if(!isRecoment && mAdControl != null) mAdControl.refrush();
            setStatisticShowNum();
            if(!TextUtils.isEmpty(backUrl)){
                params=backUrl;
            }else{
                if(isRecom()&&!TextUtils.isEmpty((String) FileManager.loadShared(mActivity,homeModuleBean.getType(),"backUrl"))){
                    params=(String) FileManager.loadShared(mActivity,homeModuleBean.getType(),"backUrl");
                }else{
                    params= "type="+homeModuleBean.getType();
                    if(!TextUtils.isEmpty(homeModuleBean.getTwoType()))params+="&two_type="+homeModuleBean.getTwoType();
                }
            }
        }else{//向下翻页
            if(!TextUtils.isEmpty(nextUrl)){
               params=nextUrl;
            }else{
                params= "type="+homeModuleBean.getType();
                if(!TextUtils.isEmpty(homeModuleBean.getTwoType()))params+="&two_type="+homeModuleBean.getTwoType();
            }
        }
        loadData(refresh,params);
    }
    /**
     * 获取数据
     */
    private void loadData(final boolean refresh, String data){
        Log.i("zhangyujian","refresh::"+refresh+"::data:"+data);
        if (homeModuleBean != null && "recom".equals(homeModuleBean.getType()) && refresh)
            XHClick.mapStat(mActivity, "a_recommend", "刷新效果", "下拉刷新");
        linearLayoutOne.removeAllViews();
        String url= StringManager.API_RECOMMEND;

        //更新加载按钮状态
        mLoadManager.changeMoreBtn(mListview, ReqInternet.REQ_OK_STRING, -1, -1, LoadOver?2:1, refresh);
        LoadOver = true;
        if (refresh) {
            if(isRecom()) {
                mLoadManager.hideProgressBar();
                returnListTop();
            }else{
                if(TextUtils.isEmpty(backUrl)&&mListData.size()<=0)mLoadManager.showProgressBar();
                else  mLoadManager.hideProgressBar();
            }
        }
        ReqEncyptInternet.in().doEncyptAEC(url,data, new InternetCallback(mActivity) {
            @Override
            public void loaded(int flag, String url, Object object) {
                int loadCount = 0;
                if(flag>=ReqInternet.REQ_OK_STRING){
                    Log.i("FRJ","获取  服务端   数据回来了-------------");
                    boolean isRecom=false;//是否是推荐
                    //只处理推荐列表
                    if(homeModuleBean.getType().equals("recom"))isRecom=true;

                    ArrayList<Map<String,String>> listmaps= StringManager.getListMapByJson(object);
                    //当前数据有问题，直接return数据
                    if(!(listmaps==null||listmaps.size()<=0||!listmaps.get(0).containsKey("list")
                            ||StringManager.getListMapByJson(listmaps.get(0).get("list"))==null
                            ||StringManager.getListMapByJson(listmaps.get(0).get("list")).size()<=0)){
                        //存储当前backurl，
                        if (!TextUtils.isEmpty(backUrl) && refresh && isRecom) {
                            FileManager.saveShared(mActivity, homeModuleBean.getType(), "backUrl", backUrl);
                        }
                        //上拉数据，下拉数据
                        if (TextUtils.isEmpty(backUrl) || (!TextUtils.isEmpty(listmaps.get(0).get("backurl")) && refresh))
                            backUrl = listmaps.get(0).get("backurl");
                        if (TextUtils.isEmpty(nextUrl) || !TextUtils.isEmpty(listmaps.get(0).get("nexturl")) && !refresh)
                            nextUrl = listmaps.get(0).get("nexturl");
                        //当前只有向上刷新，并且服务端确认可以刷新数据
                        if (compelClearData || (refresh && !TextUtils.isEmpty(listmaps.get(0).get("reset")) && "2".equals(listmaps.get(0).get("reset")))) {
                            mListData.clear();
                        }
                        //初始化二级
                        initContextView(listmaps.get(0).get("trigger_two_type"));
                        if (listmaps != null && listmaps.size() > 0) {
                            ArrayList<Map<String, String>> listDatas = StringManager.getListMapByJson(listmaps.get(0).get("list"));
                            if (listDatas != null && listDatas.size() > 0) {
                                loadCount=listDatas.size();
                                if (refresh && isRecom) {
                                    int size = listDatas.size();
                                    //创建数据条数header
                                    if(mListData.size()>0)
                                        handlerHeaderView(size);
                                }
                                int oldDayDishIndex = -1;
                                if (refresh && mListData.size() > 0) {
                                    //如果需要加广告，插入广告
                                    if (mAdControl != null) {
                                        //插入广告
                                        listDatas = mAdControl.getNewAdData(listDatas, true);
                                    }
                                    mListData.addAll(0, listDatas);//插入到第一个位置
                                } else {
                                    //查询往期推荐的index：如果当前是每日推荐，并且还未给AdControl设置过加入的位置，则查询往期推荐的index，广告插到此条上面
                                    if (isDayDish && !isSetIndex) {
                                        int index = 0;
                                        for (Map<String, String> map : listDatas) {
                                            if (!TextUtils.isEmpty(map.get("pastRecommed"))) {
                                                oldDayDishIndex = index;
                                                break;
                                            }
                                            index++;
                                        }
                                        //如果当前是每日推荐，并且还未给AdControl设置过加入的位置，则设置
                                        if (oldDayDishIndex > 0 && mAdControl != null) {
                                            isSetIndex = true;
                                            mAdControl.setIndexs(new Integer[]{oldDayDishIndex});
                                        }
                                    }
                                    mListData.addAll(listDatas);//顺序插入
                                    //如果需要加广告，插入广告
                                    if (mAdControl != null) {
                                        //插入广告
                                        mListData = mAdControl.getNewAdData(mListData, false);
                                    }
                                }
                            }
                        }
                        //读取历史记录
                        if (isRecom) {//是首页并且刷新
                            String historyUrl = (String) FileManager.loadShared(mActivity, homeModuleBean.getType(), homeModuleBean.getType());
                            if (!TextUtils.isEmpty(historyUrl)) {
                                Map<String, String> map = StringManager.getMapByString(historyUrl, "&", "=");
                                //设置显示参数
                                int size = mListData.size();
                                for (int i = 0; i < size; i++) {
                                    if (i != 0 && mListData.get(i).containsKey("mark") && mListData.get(i).get("mark").equals(map.get("mark"))) {
                                        String time = map.get("reset_time");
                                        Log.i("zhangyujian", "mak:" + mListData.get(i).get("mark") + "::;" + mListData.get(i).get("name"));
                                        Map<String, String> backMap = StringManager.getMapByString(backUrl, "&", "=");
                                        String nowTime = backMap.get("reset_time");
                                        mListData.get(i).put("refreshTime", Tools.getTimeDifferent(Long.parseLong(nowTime), Long.parseLong(time)));
                                    } else {
                                        mListData.get(i).put("refreshTime", "");
                                    }
                                }
                            }
                        }
//                    beforNum += listmaps.size();

                        //只有推荐，刷新数据才进行保存历史记录，第一次刷新不存储，下一次存储上次的数据，
                        if (isRecom && refresh) {
                            backUrlBefore = backUrl;
                            if (!TextUtils.isEmpty(backUrlBefore)) {
                                FileManager.saveShared(mActivity, homeModuleBean.getType(), homeModuleBean.getType(), backUrlBefore);
                            }
                        }
                        adapterListView.notifyDataSetChanged();
                        //自动请求下一页数据
                        if(isRecom&&mListData.size()<=4){//推荐列表：低于等5的数据自动请求数据
                            Log.i("zhangyujian","自动下次请求:::"+mListData.size());
                            EntryptData(false);
                        }
                    }else if(isRecom){//置状态---刷新按钮
                        int size = mListData.size();
                        if(size>0){//推荐列表:有数据置状态
                            for (int i = 0; i < size; i++) {
                                mListData.get(i).put("refreshTime", "");
                            }
                            adapterListView.notifyDataSetChanged();
                        }else{//无数据时---请求下一页数据
                            if(listmaps!=null&&listmaps.size()>0&&listmaps.get(0).containsKey("nexturl")&&isNextUrl){
                                nextUrl = listmaps.get(0).get("nexturl");
                                isNextUrl=false;
                                EntryptData(false);
                            }
                        }

                    }
                }else{
                     toastFaildRes(flag, true, object);
                }
                mLoadManager.hideProgressBar();
                mLoadManager.changeMoreBtn(mListview, flag, LoadManager.FOOTTIME_PAGE, refresh?mListData.size():loadCount, 0, refresh);
                if(refresh){
                    refreshLayout.refreshComplete();
                }
                compelClearData=false;//强制刷新只能使用一次，一次数据后被置回去
            }
        });
    }

    /**
     * 初始化二级内容视图
     * @param type 选中的类型
     */
    private void initContextView(String type){

        if(!isloadTwodata&&!TextUtils.isEmpty(homeModuleBean.getTwoData())&&!TextUtils.isEmpty(type)){
            linearLayoutTwo.removeAllViews();
            isloadTwodata=true;
            HomeTabHScrollView homeTabHScrollView = new HomeTabHScrollView(mActivity);
            //处理二级数据体
            ArrayList<Map<String,String>> listMaps= StringManager.getListMapByJson(homeModuleBean.getTwoData());
            if(listMaps!=null&&listMaps.size()>0){
                homeModuleBean.setTwoType(type);
            }else return;
            for(int i=0;i<listMaps.size();i++){
                listMaps.get(i).put("position",String.valueOf(i));
            }
            homeTabHScrollView.setHomeModuleBean(homeModuleBean);
            homeTabHScrollView.setData(listMaps);
            homeTabHScrollView.setCallback(new HomeTabHScrollView.HomeDataChangeCallBack() {
                @Override
                public void indexChanged(Map<String, String> map) {
                    stopVideo();
                    if(map.get("two_type").equals(homeModuleBean.getTwoType())){
                        //是否刷新操作
                    }else{
                        homeModuleBean.setTwoType(map.get("two_type"));
                        homeModuleBean.setTwoTitle(map.get("title"));
                        homeModuleBean.setTwoTypeIndex(Integer.parseInt(map.get("position")));
                        adapterListView.setHomeModuleBean(homeModuleBean);
                        compelClearData=true;
                        //请求数据
                        backUrl="";
                        nextUrl="";
                        EntryptData(true);
//                        refresh();
                    }
                }
            });
            linearLayoutTwo.addView(homeTabHScrollView);
            linearLayoutTwo.setVisibility(View.VISIBLE);
            homeTabHScrollView.setVisibility(View.VISIBLE);
        }

    }
    /**
     * 设置刷新
     */
    private void setRefresh(){
    }

    @Override
    public void onResume() {
        super.onResume();
        if(statrTime<=0&& isRecom()){
            statrTime=System.currentTimeMillis();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopVideo();
    }

    public HomeModuleBean getmoduleBean() {
        return homeModuleBean;
    }

    public void setmoduleBean(HomeModuleBean mPlateData) {
        this.homeModuleBean = mPlateData;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    /**
     * 刷新
     */
    public void refresh() {
        Log.i(tag,"HomeFragment refresh:::"+homeModuleBean.getPosition());
        if (refreshLayout != null) {
            refreshLayout.autoRefresh();
        }
    }
    /**
     * 滚动到顶部
     */
    public void returnListTop() {
        if (mListview != null) {
            mListview.setSelection(0);
        }
    }
    /**
     * 设置二次参数数据
     * @param twoTitle
     * @param twoType
     */
    public void setTwoModuledata(String twoTitle,String twoType){
        if(TextUtils.isEmpty(twoTitle))homeModuleBean.setTwoTitle(twoTitle);
        if(TextUtils.isEmpty(twoType))homeModuleBean.setTwoType(twoType);
    }
    private void handlerTopDatas(ArrayList<Map<String,String>> listmaps){
        if(listmaps!=null&&listmaps.size()>0){
            int size= listmaps.size();
            for(int i=0;i<size;i++){
                Map<String,String> map = listmaps.get(i);
                //进行类型区分，判断数据
            }
        }
    }

    private VDPlayPauseHelper mVDPlayPauseHelper;

    /**
     * 处理view,video
     * @param parentView
     * @param position
     */
    private void setVideoLayout(final View parentView, final int position){
        if (parentView == null || position < 0 || position >= mListData.size())
            return;
        if(mListData.get(position).containsKey("video") && !TextUtils.isEmpty(mListData.get(position).get("video"))) {
            Map<String, String> videoData = StringManager.getFirstMap(mListData.get(position).get("video"));
            if(mVideoImageView==null)
                mVideoImageView = new VideoImageView(mActivity,false);
            mVideoImageView.setImageBg(mListData.get(position).get("img"));
            if (videoData != null) {
                String videoUrl = videoData.get("videoUrl");
                if (!TextUtils.isEmpty(videoUrl)) {
                    ArrayList<Map<String, String>> maps = StringManager.getListMapByJson(videoUrl);
                    if (maps != null && maps.size() > 0) {
                        String videoD = "";
                        int width = ToolsDevice.getWindowPx(getContext()).widthPixels;
                        for (Map<String, String> map : maps) {
                            if (map != null) {
                                if (width <= 480 && map.containsKey("D480p")) {
                                    videoD = map.get("D480p");
                                } else if (width > 720 && map.containsKey("D1080p")) {
                                    videoD = map.get("D1080p");
                                } else if (map.containsKey("D720p")) {
                                    videoD = map.get("D720p");
                                }
                            }
                        }
                        mVideoImageView.setVideoData(videoD);
                    }

                }

            }
            mVideoImageView.setVideoCycle(false);
            mVideoImageView.setVisibility(View.VISIBLE);
            if (mVideoLayout != null && mVideoLayout.getChildCount() > 0) {
                mVideoLayout.removeAllViews();
            }

            mVideoLayout = (RelativeLayout) parentView.findViewById(R.id.video_container);
            mVideoLayout.addView(mVideoImageView);
            mVideoImageView.onBegin();
            mPlayPosition = position;
            mPlayParentView = parentView;
            final View resumeView =  parentView.findViewById(R.id.resume_img);
            mVideoImageView.setVideoClickCallBack(new VideoImageView.VideoClickCallBack() {
                @Override
                public void setVideoClick() {
                    if (resumeView != null)
                        resumeView.setVisibility(isPlaying() ? View.VISIBLE : View.GONE);
                    playPause();
                }
            });
            mVideoImageView.setOnPlayingCompletionListener(new VideoImageView.OnPlayingCompletionListener() {
                @Override
                public void onPlayingCompletion() {
                    showReplayShareView();
                }
            });
        }
    }

    /**
     * 暂停播放
     */
    public void stopVideo(){
        if(mVideoImageView!=null){
            mPlayPosition = -1;
            if (mPlayParentView != null) {
                View resumeView = mPlayParentView.findViewById(R.id.resume_img);
                if (resumeView != null && resumeView.getVisibility() != View.GONE)
                    resumeView.setVisibility(View.GONE);
            }
            mPlayParentView = null;
            mVideoImageView.onVideoPause();
            if (mVideoLayout != null)
                mVideoLayout.removeAllViews();
        }
    }

    /**
     * 重播
     */
    private void restartVideo() {
        VDVideoViewController controller = VDVideoViewController.getInstance(mActivity);
        if (controller != null) {
            controller.resume();
            controller.start();
        }
    }

    /**
     * 播放/暂停
     */
    private void playPause() {
        if (mVDPlayPauseHelper == null)
            mVDPlayPauseHelper = new VDPlayPauseHelper(mActivity);
        mVDPlayPauseHelper.doClick();
    }

    /**
     * 是否正在播放
     * @return
     */
    private boolean isPlaying() {
        return mVideoImageView == null ? false : mVideoImageView.getIsPlaying();
    }

    /**
     * 显示重播、分享界面
     */
    private void showReplayShareView() {
        if (mVideoLayout == null)
            return;
        if (mReplayAndShareView == null)
            mReplayAndShareView = new ReplayAndShareView(mActivity);
        mReplayAndShareView.setOnReplayClickListener(new ReplayAndShareView.OnReplayClickListener() {
            @Override
            public void onReplayClick() {
                if(mVideoLayout != null)
                    mVideoLayout.removeView(mReplayAndShareView);
                restartVideo();
            }
        });
        mReplayAndShareView.setOnShareClickListener(new ReplayAndShareView.OnShareClickListener() {
            @Override
            public void onShareClick() {
                if (mPlayParentView == null || !(mPlayParentView instanceof HomeRecipeItem))
                    return;
                BarShare barShare = new BarShare(mActivity, "视频列表分享视频", "菜谱");
                String type = "", title = "", clickUrl = "", content = "", imgUrl = "",isVideo="";
                //是否是自己区分数据
                Map<String, String> dataMap = ((HomeRecipeItem)mPlayParentView).getData();
                if (dataMap == null)
                    return;
                title = "【香哈菜谱】看了" + dataMap.get("name") + "的教学视频，我已经学会了，味道超赞！";
                clickUrl = StringManager.wwwUrl + "caipu/" + dataMap.get("code") + ".html";
                content = "顶级大厨的做菜视频，讲的真是太详细啦！想吃就赶快进来免费学习吧~ ";
                imgUrl = dataMap.get("img");
                type = BarShare.IMG_TYPE_WEB;
                barShare.setShare(type, title, content, imgUrl, clickUrl);
                barShare.openSharePopup();
            }
        });
        if (mVideoLayout.indexOfChild(mReplayAndShareView) == -1){
            mVideoLayout.addView(mReplayAndShareView);
        }
        mReplayAndShareView.setVisibility(View.VISIBLE);
        mVideoLayout.requestLayout();
        mVideoLayout.invalidate();
    }

    private void hideReplayShareView() {
        if (mVideoLayout != null && mReplayAndShareView != null && mReplayAndShareView.isShowing()) {
            mVideoLayout.removeView(mReplayAndShareView);
        }
    }

    /**
     * 处理置顶数据
     */
    private void handlerTopData(Object object){
        linearLayoutThree.removeAllViews();
        ArrayList<Map<String,String>> listmaps= StringManager.getListMapByJson(object);
        if(listmaps!=null&&listmaps.size()>0){
            int size= listmaps.size();
            for(int i=0;i<size;i++){
                listmaps.get(i).put("isTop","2");
                HomeItem view= handlerTopView(listmaps.get(i));
                if(view!=null){
                    linearLayoutThree.addView(view);
                    linearLayoutThree.addView(LayoutInflater.from(mActivity).inflate(R.layout.view_home_show_line,null));
                }
            }
            linearLayoutThree.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理置顶数据View类型
     * @param map
     * @return
     */
    private HomeItem handlerTopView(Map<String,String> map){
        HomeItem viewTop=null;
        if(map.containsKey("type")&&!TextUtils.isEmpty(map.get("type"))){
            int type=Integer.parseInt(map.get("type"));
            switch (type){
                case 1:
                case 2:
                    viewTop= new HomeRecipeItem(mActivity);
                    break;
                case 3:
                    viewTop= new HomeTxtItem(mActivity);
                    break;
                case 4:
                    viewTop= new HomeAlbumItem(mActivity);
                    break;
                case 5:
                    viewTop= new HomePostItem(mActivity);
                    break;

            }
            viewTop.setData(map,0);
        }
        return viewTop;
    }
    public boolean isScrollData() {
        return isScrollData;
    }

    public void setScrollData(boolean scrollData) {
        isScrollData = scrollData;
    }

    public int getScrollDataIndex() {
        return scrollDataIndex;
    }

    public void setScrollDataIndex(int scrollDataIndex) {
        this.scrollDataIndex = scrollDataIndex;
    }

    public boolean isRecom() {
        return homeModuleBean.getType().equals("recom");
    }

    public long getStatrTime() {
        return statrTime;
    }

    public void setStatrTime(long statrTime) {
        this.statrTime = statrTime;
    }
    private void handlerHeaderView(int size){
        if(homeHeaderDataNum!=null&&size>0){
            show_num_tv.setText("有"+size+"条新内容");
            homeHeaderDataNum.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.translate_uptodown);
            animation.setStartOffset(500);
            homeHeaderDataNum.startAnimation(animation );
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    homeHeaderDataNum.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            homeHeaderDataNum.clearAnimation();
//                            ScaleAnimation scaleAnimation = new ScaleAnimation(1,0.5f,1,0.5f,
//                                    Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                            AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
                            //3秒完成动画
                            alphaAnimation.setDuration(1000);
//                            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.translate_downtoup);
                            homeHeaderDataNum.startAnimation(alphaAnimation);
                            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    homeHeaderDataNum.clearAnimation();
                                    homeHeaderDataNum.setVisibility(View.GONE);
                                }
                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        }
                    },1000*2);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    /**
     * 保存刷新数据
     */
    public void setStatisticShowNum(){
        if(scrollDataIndex>0&&isRecom()){
            XHClick.saveStatictisFile("home","recom","","",String.valueOf(scrollDataIndex),"list","","","","","");
            scrollDataIndex=-1;
        }
    }
}
