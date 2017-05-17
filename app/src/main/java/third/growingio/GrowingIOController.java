package third.growingio;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;

import java.util.Map;

import acore.override.XHApplication;
import acore.tools.ChannelUtil;
import acore.tools.FileManager;
import acore.tools.LogManager;
import acore.tools.Tools;

/**
 * PackageName : third.growingio
 * Created by MrTrying on 2017/5/3 17:15.
 * E_mail : ztanzeyu@gmail.com
 */

public class GrowingIOController {

    private volatile static int count = 0;

    public void init(Application context) {
        //GrowingIO初始化
        if (!isGrowingUser(context)) {
            return;
        }
        GrowingIO.startWithConfiguration(XHApplication.in(), new Configuration()
                .useID()
                .trackAllFragments()
                .setChannel(ChannelUtil.getChannel(context)));
    }

    /**
     * 设置用户属性
     * @param userInfo
     */
    public void setUserProperties(Context context,Map<String,String> userInfo){
        //是否是需要统计用户
        if(!isGrowingUser(context)){
            return;
        }
        GrowingIO growingIO = GrowingIO.getInstance();
        final String code = userInfo.get("code");
        if(TextUtils.isEmpty(code)){
            count = 0;
            //为空，说明是退出登录状态
            growingIO.setCS1("code",null);
        }else if(count == 0){
            LogManager.print("d","setUserProperties");
            count ++;
            //code，user唯一标识
            growingIO.setCS1("code",userInfo.get("code"));
            //昵称
            String nickname = userInfo.get("nickName");
            if(!TextUtils.isEmpty(nickname)){
                growingIO.setCS2("nickname",nickname);
            }
            //性别
            String sex = userInfo.get("sex");
            if(!TextUtils.isEmpty(sex)){
                int index = sex.indexOf("^");
                if(index >=0 && index + 1 < sex.length()){
                    sex = sex.substring(index + 1 , sex.length());
                }
                growingIO.setCS3("sex",sex);
            }
            //等级
            String lv = userInfo.get("lv");
            if(!TextUtils.isEmpty(lv)){
                growingIO.setCS4("lv",lv);
            }
            //关注数
            String followNum = userInfo.get("followNum");
            if(!TextUtils.isEmpty(followNum)){
                growingIO.setCS5("followNum",followNum);
            }
            //是否是美食家
            String isGourmet = userInfo.get("isGourmet");
            if(!TextUtils.isEmpty(isGourmet)){
                growingIO.setCS6("isGourmet",isGourmet);
            }
            //注册时间
            String regTime = userInfo.get("regTime");
            if(!TextUtils.isEmpty(regTime)){
                growingIO.setCS7("regTime",regTime);
            }
            //帖子数
            String subjectNum = userInfo.get("subjectNum");
            if(!TextUtils.isEmpty(subjectNum)){
                growingIO.setCS8("subjectNum",subjectNum);
            }
            //菜谱数
            String upNum = userInfo.get("upNum");
            if(!TextUtils.isEmpty(upNum)){
                growingIO.setCS9("upNum",upNum);
            }
            //收藏菜谱数
            String favNum = userInfo.get("favNum");
            if(!TextUtils.isEmpty(favNum)){
                growingIO.setCS10("favNum",favNum);
            }
        }
    }

    /**
     * 判断是否是GrowingIO需要统计的用户
     *
     * @param context
     *
     * @return
     */
    private boolean isGrowingUser(Context context) {
        final String key = "GrowingIO";
        String value = FileManager.loadShared(context, FileManager.xmlFile_appInfo, key).toString();
        boolean isInited = !TextUtils.isEmpty(value);
        //已经初始化过
        if (isInited) {
            if ("true".equals(value)) {
                return true;
            } else if ("false".equals(value)) {
                return false;
            }
            //未初始化过
        } else {
            int random = Tools.getRandom(0, 50);
            boolean isGrowingIOUser = random == 0;//1/50的机率
            FileManager.saveShared(context, FileManager.xmlFile_appInfo, key, String.valueOf(isGrowingIOUser));
            return isGrowingIOUser;
        }
        return false;
    }
}
