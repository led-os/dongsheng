package acore.tools;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xh.basic.tool.UtilString;

public class StringManager extends UtilString {
    public static boolean httpState=false;//false为https，true为http；
    //固定URL基础配置
    public final static String defaultDomain = ".xiangha.com";
    public static String defaultProtocol = "https://";
    public final static String apiTitle = "api";
    public final static String appWebTitle = "appweb";
    public final static String wwwTitle = "www";
    public final static String mmTitle = "mm";
    public final static String mTitle = "m";
    //当前域名
    public static String domain = defaultDomain;
    //当前协议
    public static String protocol = defaultProtocol;
    //API请求地址
    public static String apiUrl = defaultProtocol + apiTitle + defaultDomain + "/";
    //app网页地址
    public static String appWebUrl = defaultProtocol + appWebTitle + defaultDomain + "/";
    //PC主网页地址
    public static String wwwUrl = defaultProtocol + wwwTitle + defaultDomain + "/";
    //手机管理平台地址
    public static String mmUrl = defaultProtocol + mmTitle + defaultDomain + "/";

    public static String mUrl = defaultProtocol + mTitle + defaultDomain + "/";

    public static final Map<String, String> urlSection = new HashMap<String, String>();

    static {
        urlSection.put("caipu3", "main3/caipu/");
        urlSection.put("home5", "main5/home/");
        urlSection.put("caipu5", "main5/caipu/");
        urlSection.put("shicai5", "main5/shicai/");
        urlSection.put("zhishi5", "main5/zhishi/");
        urlSection.put("so5", "main5/so/");
        urlSection.put("group5", "main5/group/");
        urlSection.put("quan5", "main5/quan/");
        urlSection.put("quan6", "main6/quan/");
        urlSection.put("user6", "main6/user/");
        urlSection.put("main6", "main6/index/");
        urlSection.put("other6", "main6/other/");
        urlSection.put("caipu6", "main6/caipu/");
        urlSection.put("vip6", "main6/vip/");
        urlSection.put("auth6", "main6/userAuth/");
    }

    public final static String appID = "1";
    //第三方下载链接（应用宝）
    public final static String third_downLoadUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.xiangha";
    //崩溃
    public final static String api_uploadCrashLog = "https://crash.xiangha.com/report";
    //用户手机应用列表
    public final static String api_uploadFavorLog = "https://favor.xiangha.com/list";
    //用户发贴发菜谱用户日志
    public final static String api_uploadUserLog = "https://crash.xiangha.com/crash/report2";
    //获取广告信息
    public final static String api_adData_old = apiUrl + "main6/ads/getBasicList";
    public final static String api_adData = apiUrl + "main7/open/getAdConfig";
    public final static String api_appData = apiUrl + urlSection.get("home5") + "getAppData";
    public final static String api_activityList = apiUrl + urlSection.get("home5") + "getActivityList";
    public final static String api_statisticShare = apiUrl + urlSection.get("home5") + "addShareStatistic";

    public final static String api_getSurpriseActivity = apiUrl + urlSection.get("home5") + "getSurpriseActivity";
    public final static String api_getProductList = apiUrl + urlSection.get("home5") + "getProductList";

    public final static String api_versionInfo = apiUrl + urlSection.get("home5") + "getAppUpdate";
    public final static String api_getDownloadUrl = apiUrl + urlSection.get("home5") + "getDownloadUrl";
    public final static String api_getWebRule = apiUrl + urlSection.get("home5") + "getWebRule";

    public final static String api_scoreStore = appWebUrl + "app5/scoreStore";//积分商城
    public final static String api_dailyTask = appWebUrl + "app5/dailyTask";//每日任务
    public final static String api_scoreList = appWebUrl + "app5/scoreList";//积分乐园《是否带有Tab选择靠服务器的版本判断》
    public final static String api_integralInfo = appWebUrl + "app5/customerScore";//积份规则
    public final static String api_exchangeList = appWebUrl + "app5/orderList";//兑换记录
    public final static String api_commodityDetail = appWebUrl + "app5/itemInfo";//物品详情
    public final static String api_approveGourmet = appWebUrl + "approve/index";//申请认证
    public final static String api_artcle = appWebUrl + "article/index";//有奖征文
    public final static String api_joinHand = appWebUrl + "joinHand/index";//合作
    public final static String api_jifenDraw = appWebUrl + "app5/jifenDraw";//抽奖
    public final static String api_aboutus = apiUrl + urlSection.get("home5") + "aboutus";//关于我们
    //	/** 香哈协议 */
    public static final String api_agreementXiangha = appWebUrl + "deal/xiangha.html";
    /**
     * 原创协议
     */
    public static final String api_agreementOriginal = appWebUrl + "deal/original.html";
    /**
     * 举报说明
     */
    public static final String api_agreementReport = appWebUrl + "deal/report.html";

    public static final String api_nouseInfo = appWebUrl + "zhishi/nousInfo?code=";
    public final static String api_commonData = apiUrl + urlSection.get("home5") + "commonData";
    public final static String api_soList = apiUrl + urlSection.get("so5") + "getSoData";
    public final static String api_soIndex = apiUrl + urlSection.get("so5") + "getSoIndex";
    public final static String api_getTiezi = apiUrl + "main6/search/byQuan";

    public final static String api_getCaipu = apiUrl + "main8/search/byCaipu";
    public final static String api_getHotWords = apiUrl + "main8/search/getHotWords";
    public final static String api_matchWords = apiUrl + "main8/search/getAssociational";

    public final static String api_ingreInfo = apiUrl + urlSection.get("shicai5") + "getIngreInfo";
    public final static String api_getUserDishAll = apiUrl + urlSection.get("home5") + "getUserDishAll";
    public final static String api_soFavorite = apiUrl + urlSection.get("so5") + "customerSo?cate=fav&";

    public final static String api_getDishInfo = apiUrl + urlSection.get("caipu5") + "getDishInfo";
    public final static String api_getRecommendDish = apiUrl + urlSection.get("caipu5") + "getVideoRecommend";
    public final static String api_getVideoClassifyDish = apiUrl + urlSection.get("caipu5") + "getVideoDishTagInfo";
    public final static String api_getMenuData = apiUrl + urlSection.get("caipu5") + "getTopic";

    public final static String api_getDishList = apiUrl + urlSection.get("caipu5") + "getDishList";
    public final static String api_getIngreList = apiUrl + urlSection.get("shicai5") + "getIngreList";
    public final static String api_uploadImg = apiUrl + "upload/imgs";

    public final static String api_getUSerData = apiUrl + urlSection.get("home5") + "getUserData";//根据类型获取用户数据//ok
    public final static String api_getUserInfoByCode = apiUrl + urlSection.get("user6") + "getUserInfoByCode";//根据类型获取用户数据//ok
    public final static String api_getDishByCode = apiUrl + urlSection.get("user6") + "getDishByCode";//根据类型获取用户数据//ok
    public final static String api_getPostByCode = apiUrl + urlSection.get("user6") + "getPostByCode";//根据类型获取用户数据//ok
    public final static String api_getDailyTask = appWebUrl + "app5/dailyTask"; //每日任务
    public final static String api_getCustomerRank = appWebUrl + "app5/customerRank"; //用户等级
    public final static String api_inviteCustomer_new = appWebUrl + "app5/inviteCustomer";//邀请好友
    public final static String api_getTaskCount = apiUrl + "home5/getTaskCount"; //积分商城页面获取任务数量，带有用户积分

    public final static String api_getFriendList = apiUrl + urlSection.get("home5") + "getFriendList";
    public final static String api_phoneLogin = apiUrl + urlSection.get("user6") + "phoneLogin";
    public final static String api_phoneBind = apiUrl + urlSection.get("user6") + "phoneBind";
    public final static String api_setUserData = apiUrl + urlSection.get("home5") + "setUserData";

    public final static String api_uploadDish = apiUrl + urlSection.get("caipu3") + "uploadDish";
    public final static String api_deleteDish = apiUrl + urlSection.get("caipu5") + "deleteDish";
    public final static String api_quanTopUrl = apiUrl + urlSection.get("quan5") + "getUserTop";
    public final static String api_quanSetSubject = apiUrl + urlSection.get("quan5") + "setSubjectData";
    public final static String api_getCheckIngore = apiUrl + urlSection.get("home5") + "checkIngore";
    //----------生活圈新api------------
    public final static String api_uploadSubject = apiUrl + "main6/tie/uploadSub";
    public final static String api_uploadFloor = apiUrl + "main6/tie/uploadFloor";
    public final static String api_circleStaticData = apiUrl + urlSection.get("quan6") + "staticData";
    public final static String api_circleMyQuan = apiUrl + urlSection.get("quan6") + "myQuan";
    public final static String api_circleShare = wwwUrl + "quan";
    public final static String api_indexModules = apiUrl + urlSection.get("quan6") + "indexModules";
    public final static String api_recCustomer = apiUrl + "main6/tie/recCustomer";
    public final static String api_setClickList = apiUrl + "main6/tie/setClickList";

    //发现圈子
    public final static String api_circleFind = apiUrl + urlSection.get("quan6") + "getList";
    //关注某个圈子
    public final static String api_circleGetInfo = apiUrl + urlSection.get("quan6") + "getInfo";
    //圈子成员列表
    public final static String api_circleCustomerList = apiUrl + urlSection.get("quan6") + "customerList";
    public final static String api_circleCustomerBlackList = apiUrl + urlSection.get("quan6") + "customerBlackList";
    /**
     * 圈子列表
     */
    public final static String api_circleSubjectList = apiUrl + "main6/tie/getList";
    public final static String api_circlegetInfo = apiUrl + "main6/tie/getInfo";
    public final static String api_circleSafaList = apiUrl + "main6/tie/safaList";
    public final static String api_topCustomer = apiUrl + "main6/tie/topCustomer";
    public final static String api_getFollowSubjectList = apiUrl + "main6/tie/getDishList"; //跟帖列表
    //成员操作
    public final static String api_circleCustomerPower = apiUrl + urlSection.get("quan6") + "customerPower";

    //管理员推荐
    public final static String api_setSubjectRecommend = apiUrl + urlSection.get("quan5") + "setSubjectRecommend";
    public final static String api_setAppUrl = apiUrl + urlSection.get("home5") + "setAppUrl";
    public final static String api_message = apiUrl + urlSection.get("home5") + "getNewsInfo";
    public final static String api_nousList = apiUrl + urlSection.get("zhishi5") + "getNousList";
    public final static String api_nousInfo = apiUrl + urlSection.get("zhishi5") + "nousInfo";
    public final static String api_setHealthTest = apiUrl + urlSection.get("shicai5") + "setHealthTest";
    public final static String api_getDialogInfo = apiUrl + urlSection.get("home5") + "getDialogInfo";
    public final static String api_sendDialog = apiUrl + urlSection.get("home5") + "sendDialog";

    //邀请好友验证码请求
    public final static String api_inviteCheck = apiUrl + urlSection.get("home5") + "inviteCheck";
    public final static String api_parseInvitationCode = apiUrl + urlSection.get("home5") + "parseInvitationCode";
    public final static String api_inviteCustomer = apiUrl + urlSection.get("home5") + "inviteCustomer";
    //美食圈模板
    public final static String api_changeSubjectClassify = apiUrl + urlSection.get("quan5") + "changeSubjectClassify";//修改贴子模块
    public final static String api_addJingHua = apiUrl + urlSection.get("quan5") + "addJingHua";//加精

    //乐视获取videoUrl的接口
    public static final String api_getVideoUrl = "http://api.letvcloud.com/getplayurl.php";
    public final static String api_homeTodayGood = apiUrl + "main6/index/todayGood";
    public final static String api_homeTodayGoodShare = wwwUrl + "caipu/youzhi";
    public final static String api_monitoring = "https://stat.xiangha.com/s4.gif";

    //钱包会员
    public final static String api_money = appWebUrl + "vip/walletV1?fullScreen=2"; //我的钱包

    public final static String api_isAuto = apiUrl + urlSection.get("vip6") + "isAuto"; //是否自动续费
    public final static String api_setIsAuto = apiUrl + urlSection.get("vip6") + "setAuto"; //设置是否自动续费

    //一元VIP
    public final static String api_yiyuan_bindstate = apiUrl + "main7/user/isBindingState";//一元VIP绑定状态
    public final static String api_yiyuan_binduser = apiUrl + "main7/user/bindingUser";//一元VIP绑定


    //广告统计
    public final static String api_clickAds = apiUrl + "main6/ads/clickAds"; //广告统计,用于加积分
    //菜谱详情页
    public final static String api_getDishInfoNew = apiUrl + urlSection.get("caipu6") + "info";
    public final static String api_getDishTopInfo = apiUrl + "main7/dish/topInfo";
    public final static String api_getDishTieInfo = apiUrl + "main7/dish/tieInfo";
    public final static String api_getDishstatusValue = apiUrl + "main7/dish/statusValue";
    public final static String api_getDishLikeNumStatus = apiUrl + "main7/dish/likeNumStatus";
    public final static String api_getDishLikeHate = apiUrl + "main7/dish/likeHate";
    public final static String api_addCollection = apiUrl + "main7/dish/addCollection";
    public final static String api_askButtonStatus = apiUrl + "main7/dish/askButtonStatus";
    public final static String api_getVideoList = apiUrl + "main7/school/getVideoList";
    public final static String api_adsNumber = apiUrl + "main7/open/adsNumber";

    public final static String api_getConf = apiUrl + urlSection.get("other6") + "getConf";
    public final static String api_subjectOverHead = apiUrl + urlSection.get("user6") + "subjectOverHeadLast";//用户美食贴置顶
    public final static String api_deleteSubject = apiUrl + urlSection.get("user6") + "deleteSubject";//用户删除美食贴
    //断点上传获取校验token
    public final static String api_getQiniuToken = apiUrl + urlSection.get("other6") + "getQiniuToken";

    //消息通知设置页面
    public static final String API_GETINFOSWITCHLIST = apiUrl + urlSection.get("home5") + "getInfoSwitchList";
    public static final String API_SETINFOSWITCH = apiUrl + urlSection.get("home5") + "setInfoSwitch";

    public final static String api_compareVerCode = apiUrl + urlSection.get("user6") + "compareVerCode";
    public final static String api_getActivityInfo = apiUrl + urlSection.get("other6") + "getActivityInfo"; //获取活动入口
    public final static String api_getUserPowers = apiUrl + urlSection.get("user6") + "permissions"; //获取用户权限按钮
    public final static String api_getUserInfo = apiUrl + urlSection.get("home5") + "getUser"; //用户登录
    public final static String api_smsReport = defaultProtocol + "crash.xiangha.com/sms/report"; //验证0码获取错误
    public final static String api_setSecret = apiUrl + urlSection.get("user6") + "modifyPassword"; //设置，修改密码
    public final static String api_getThirdBind = apiUrl + urlSection.get("user6") + "thirdPartyList"; //获取第三方账号绑定
    public final static String api_unbindThirdParty = apiUrl + urlSection.get("user6") + "unbindThirdParty"; //第三方账号解绑
    public final static String api_unbindEmail = apiUrl + urlSection.get("user6") + "unbindEmail"; //邮箱账号解绑
    public final static String api_checkAccount = apiUrl + urlSection.get("auth6") + "checkAccountByPhoneAndPassword"; //检验账号+密码是否是匹配的
    public final static String api_checkPhoneRegisterState = apiUrl + urlSection.get("auth6") + "checkPhoneIsRegistered"; //手机号是否注册
    public final static String api_checkEmailRegisterState = apiUrl + urlSection.get("auth6") + "checkEmailIsRegistered"; //邮箱是否注册
    public final static String api_modifyPhone = apiUrl + urlSection.get("user6") + "modifyPhone"; //注册手机号
    public final static String api_sendVoiceVerify = apiUrl + "main7/voiceVerify/send"; //获取语音验证码
    public final static String api_addTask = apiUrl + "main7/user/addTask";

    /***
     * 新首页接口
     */
    public final static String API_GET_LEVEL = apiUrl + "main7/recommend/getLevel"; //获取首页层级数据
    public final static String API_RECOMMEND = apiUrl + "main7/recommend/recommend"; //获取首页数据
    public final static String API_LOGIN_APP = apiUrl + "main7/public/loginApp"; //获取首页数据
    public final static String API_RECOMMEND_TOP = apiUrl + "main7/recommend/topv1"; //获取推荐置顶数据

    /***
     * 个人主页：（视频、文章、问答）接口
     */
    public final static String API_USERMAIN_LEVEL = apiUrl + "main7/article/getClassList";//获取个人主页导航数据

    /***
     * 推荐列表的统计
     */
    public final static String API_STATISTIC_S9 = "https://stat.xiangha.com/s9.gif"; //无埋点统计
    public final static String API_STATISTIC_S9_GXH = "https://stat.xiangha.com/s9_gxh.gif"; //无埋点统计

    /*文章*/
    public final static String api_getArticleClass = apiUrl + "main7/article/getArticleClass"; //文章分类
    public final static String api_getArticleRelated = apiUrl + "main7/article/getRelated"; //文章详情中的相关推荐
    public final static String api_getArticleInfo = apiUrl + "main7/article/articleInfo"; //文章详情
    public final static String api_articleAdd = apiUrl + "main7/article/articleAdd"; //发布文章
    public final static String api_likeArticle = apiUrl + "main7/article/likeArticle"; //文章点赞
    public final static String api_articleDel = apiUrl + "main7/article/articleDel"; //删除文章

    /*视频*/
    public final static String getVideoClass = apiUrl + "main7/video/getVideoClass"; //视频分类
    public final static String api_getVideoRelated = apiUrl + "main7/video/getRelated"; //视频详情
    public final static String api_likeVideo = apiUrl + "main7/video/likeVideo"; //视频详情
    public final static String api_videoAdd = apiUrl + "main7/video/videoAdd"; //发布视频
    public final static String api_videoDel = apiUrl + "main7/video/videoDel"; //发布视频

    /*评论*/
    public final static String api_forumList = apiUrl + "main7/forum/forumList"; //发布文章
    public final static String api_likeForum = apiUrl + "main7/forum/likeForum"; //评论点赞
    public final static String api_replayList = apiUrl + "main7/forum/replayList"; //评论点赞
    public final static String api_addForum = apiUrl + "main7/forum/addForum"; //添加评论
    public final static String api_delForum = apiUrl + "main7/forum/delForum"; //删除评论
    public final static String api_addReplay = apiUrl + "main7/forum/addReplay"; //添加回复
    public final static String api_delReplay = apiUrl + "main7/forum/delReplay"; //删除回复

    /*评论举报列表*/
    public final static String API_COMMENTS_REPORT = apiUrl + "main7/forum/reportList";
    public final static String API_COMMIT_REPORT = apiUrl + "main7/forum/addReport";

    /*个人主页列表*/
    public final static String API_USERHOME_ARTICLE = apiUrl + "main7/article/articleList";//文章列表
    public final static String API_USERHOME_VIDEO = apiUrl + "main8/shortVideo/shortVideoList";//视频列表
    public final static String API_USERHOME_ANSWER = apiUrl + "main7/qa/qaList";//问答列表
    public final static String api_article = mUrl + "articleVideo/getArticleSource/";//文章详情页m

    /*付费问答*/
    public final static String API_QA_ISTIP = apiUrl + "main7/qa/getIsTip";//获取是否弹窗提示
    public final static String API_QA_GETREPORT = apiUrl + "main7/qa/getReport";//获取问答问答举报信息
    public final static String API_QA_COMMITREPORT = apiUrl + "main7/qa/addReport";//提交问答举报
    public final static String API_QA_GETPRICE = apiUrl + "main7/qa/getQaPrice";//获取提问金额
    public final static String API_QA_QAADD = apiUrl + "main7/qa/qaAdd";//添加问答
    public final static String API_QA_QAMSGLIST = appWebUrl + "qa/myQa";//获取问答消息列表顶部tab信息
    public final static String API_QA_QASTATEMENT = appWebUrl + "qa/agreement?fullScreen=2";//问答细则及责任声明
    public final static String API_QA_QASETTING = appWebUrl + "qa/ansSet";//问答设置
    public final static String API_QA_QAARBITRATION = appWebUrl + "qa/arbitrateList?fullScreen=2";//问答仲裁
    public final static String API_TEMPLATE_AUTOLOADTEMPLATE = apiUrl + "main7/template/autoloadTemplate";//多模版控制
    public final static String API_TEMPLATE_GETTEMPLATENAME = apiUrl + "main7/template/getTemplateName";//获取单个模版
    //main8
    public final static String API_COLLECTIONLIST = apiUrl + "main8/recommend/favoritesList";//收藏列表+搜索收藏
    public final static String API_GET_FAVORITE_STATUS = apiUrl + "main8/favorites/getFavoritesStatus";//获取收藏状态
    public final static String API_SET_FAVORITE_STATUS = apiUrl + "main8/favorites/setFavorites";//设置收藏状态

    //菜谱详情页面
    public final static String API_GETDISHBURDENBYCODE = apiUrl + "main8/dish/getDishBurdenByCode";//用料信息

    public final static String API_MAIN8_SHARE_CONF = apiUrl + "main8/dish/getDishShareByCode";//获取菜谱分享配置
    public final static String API_MAIN8_TOPINFP = apiUrl  + "main8/dish/topInfo";//菜谱详情页顶部
    public final static String API_MAIN8_DISHMAKE = apiUrl  + "main8/dish/getDishMakeByCode";//菜谱详情页做法
    public final static String API_MAIN8_QAINFO = apiUrl + "main8/dish/getQaInfo";//菜谱详情页做法
    public final static String API_MAIN8_TIEINFO = apiUrl + "main8/dish/tieInfo";//菜谱详情页做法
    public final static String API_MAIN8_RELATIONBYCODE = apiUrl + "main8/dish/getDishRelationByCode";//菜谱详情页公共
    public final static String API_MAIN8_ANTIC = apiUrl + "main8/dish/getDishAntic";//菜谱详情页小技巧
    public final static String API_HOMEPAGE_6_0 = apiUrl + "main8/recommend/recommend";//首页
    public final static String API_GETDISHTYPE = apiUrl + "main8/dish/getDishType";//首页
    public final static String API_PAYVIP = apiUrl + "main7/vip/payVip";//支付vip处理

    public final static String API_SCHOOL_HOME = apiUrl + "main8/school/home";//会员首页
    public final static String API_SCHOOL_COURSELIST = apiUrl + "main8/school/courseList";//会员首页列表
    public final static String API_SCHOOL_CHAPTERLIST = apiUrl + "main8/school/chapterList";//会员首页列表
    public final static String API_SCHOOL_VIPBUTTON = apiUrl + "main8/school/getVipButtonStatus";//获取会员的button状态
    public final static String API_SCHOOL_TOPINFO = apiUrl + "main8/school/getTopInfo";
    public final static String API_SCHOOL_COURSEINTRODUCEINFO = apiUrl + "main8/school/getCourseIntroduceInfo";
    public final static String API_SCHOOL_COMMENTINFO = apiUrl + "main8/school/getCommentInfo";
    public final static String API_SCHOOL_COMMENTCHAPTERS = apiUrl + "main8/school/getCourseChapters";
    public final static String API_SCHOOL_LIKELIST = apiUrl + "main8/school/getLikeList";
    public final static String API_SCHOOL_INFO_VIPBUTTON = apiUrl + "main8/school/getVipButton";

    public final static String API_AD_GETADDATA = apiUrl + "main7/open/getAdData";//获取广告数据

    public final static String API_DISH_RECOMMEND = apiUrl + "main8/dish/getDishRecomByCode";//菜谱详情页的相关推荐

    public final static String API_SEARCH_COURSE_DISH = apiUrl + "main8/search/byCourseDishV1";//搜索名厨课菜谱
    public final static String API_SEARCH_RECOM_LABEL = apiUrl + "main8/search/recomLabel";//搜索名厨课菜谱
    //短视频详情页
    public final static String API_SHORT_VIDEOCODES = apiUrl + "main7/video/videoCodeList";
    public final static String api_getVideoInfo = apiUrl + "main8/shortVideo/shortVideoInfo"; //视频详情

    //话题详情页
    public final static String API_TOPIC_INFO = apiUrl + "main8/shortVideo/topicInfo";//简介
    public final static String API_TOPIC_INFOV1 = apiUrl + "main8/shortVideo/topicInfoV1";//简介
    public final static String API_TOPIC_LIST = apiUrl + "main8/shortVideo/topicShortVideoList";//列表

    //话题搜索页
    public final static String API_TOPIC_RECOM = apiUrl + "main8/shortVideo/topicRecom";//热门话题推荐
    public final static String API_TOPIC_SEARCH = apiUrl + "main8/shortVideo/topicSearch";//话题搜索

    //短视频有效浏览统计
    public final static String API_SHORT_VIDEO_VIEW_VALIDATE = apiUrl + "main8/shortVideo/shortVideoEffecStat";
    //短视频访问统计
    public final static String API_SHORT_VIDEO_ACCESS = apiUrl + "main8/shortVideo/shortVideoStat";
    public final static String API_FORUM_GETCOMMENTMODENUMBYDATE = apiUrl + "main7/forum/getNumByComMode";//灌水接口
    public final static String API_SHORTVIDEO_MUSICLIST = apiUrl + "main8/shortVideo/musicList";//音乐接口
    public final static String API_SHORTVIDEO_TOPICRECOM = apiUrl + "main8/shortVideo/topicRecom";//话题列表
    public final static String API_SHORTVIDEO_RELEASE = apiUrl + "main8/shortVideo/release";//视频发布
    public final static String API_SHORTVIDEO_TOPICCHECK = apiUrl + "main8/shortVideo/topicCheck";//视频发布

    //名厨菜更新时间
    public static final String API_COURSE_UPDATE = apiUrl + "main8/school/getRotationData";
    public final static String API_COURSE_CHAPTERDESC = apiUrl + "/main9/school/chapterDesc";

    //单课付费
    public static final String API_COURSE_TOP = apiUrl + "main9/school/courseTop";
    public static final String API_COURSE_DESC = apiUrl + "main9/school/courseDesc";
    public static final String API_LESSON_INFO = apiUrl + "main9/school/lessonInfo";
    public static final String API_LESSON_POINT = apiUrl + "main9/school/lessonPoint";
    public static final String API_SYLLABUS = apiUrl + "main9/school/syllabus";

    //替换url
    public final static String replaceUrl(String url) {
        if (defaultDomain != domain || defaultProtocol != protocol) {
            String[] find = {apiTitle, appWebTitle, wwwTitle, mmTitle, mTitle};
            String[] replace = {apiUrl, appWebUrl, wwwUrl, mmUrl, mUrl};
            for (int i = 0; i < find.length; i++) {
                String findStr = defaultProtocol + find[i] + defaultDomain + "/";
                if (url != null && url.indexOf(findStr) == 0) {
                    return url.replace(findStr, replace[i]);
                }
            }
        }
        return url;
    }

    //更换url
    public final static void changeUrl(String newProtocol, String newDomain) {
        if (!TextUtils.isEmpty(newDomain))
            domain = new String(newDomain);
        if (!TextUtils.isEmpty(newProtocol))
            protocol = new String(newProtocol);
        apiUrl = protocol + apiTitle + domain + "/";
        appWebUrl = protocol + appWebTitle + domain + "/";
        wwwUrl = protocol + wwwTitle + domain + "/";
        mmUrl = protocol + mmTitle + domain + "/";
        mUrl = protocol + mTitle + domain + "/";
    }

    /**
     * 用户编辑一些信息后，经过一些检验
     *
     * @param charSequence ： 判断的内容
     * @return 如果是全空格，则返回"",否则原样返回，不直接去掉用户故意加的前后空格
     */
    public final static String getUploadString(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence))
            return "";
        String content = charSequence.toString();
        if (content.trim().length() > 0)
            return content;
        else
            return "";
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static Map<String, String> getFirstMap(Object obj) {
        Map<String, String> map = new HashMap<>();
        ArrayList<Map<String, String>> returnList = StringManager.getListMapByJson(obj);
        if (returnList.size() > 0) {
            map = returnList.get(0);
        }
        return map;
    }

    /**
     * @param map
     * @param key
     * @return
     */
    public static boolean getBooleanByEqualsValue(Map<String, String> map, String key) {
        if (map == null || TextUtils.isEmpty(key) || !map.containsKey(key)) {
            return false;
        }
        return "2".equals(map.get(key));
    }

    public static JSONArray getJsonByArrayList(ArrayList<Map<String, String>> arrayList) {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject;
            for (Map<String, String> map : arrayList) {
                jsonObject = new JSONObject();
                for (String key : map.keySet()) {
                    jsonObject.put(key, map.get(key));
                }
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static JSONArray getJsonByMap(Map<String, String> map) {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            for (String key : map.keySet()) {
                jsonObject.put(key, map.get(key));
            }
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * 查看字符是否有正常字符，除空格和换行
     * @param content
     * @return
     */
    public static boolean isHasChar(String content){
        if(TextUtils.isEmpty(content))
            return false;
        content = content.replace(" ","");
        if(content.length() == 0)
            return false;
        content = content.replace("\n","");
        if(content.length() == 0)
            return false;
        return true;
    }

    /**
     * 获取vip的url
     * @param hasOpen 是否已经开通了会员
     * @return
     */
    public static String getVipUrl(boolean hasOpen) {
        return appWebUrl + "vip/myvip?fullScreen=2" + (hasOpen ? "&payset=2" : ""); //会员中心
    }

    public static String subOverString(String str,int length){
        if(TextUtils.isEmpty(str) || length <= 0){
            return "";
        }
        return str.length() > length ? str.substring(0,length) + "..." : str;
    }
}
