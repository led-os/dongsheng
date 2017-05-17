package acore.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xiangha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import acore.dialogManager.VersionOp;
import acore.override.XHApplication;
import aplug.basic.InternetCallback;
import aplug.basic.ReqInternet;
import xh.basic.tool.UtilFile;
import xh.basic.tool.UtilLog;

public class ToolsDevice {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        int res = 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        if (dpValue < 0)
            res = -(int) (-dpValue * scale + 0.5f);
        else
            res = (int) (dpValue * scale + 0.5f);
        return res;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        int res = 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        if (pxValue < 0)
            res = -(int) (-pxValue / scale + 0.5f);
        else
            res = (int) (pxValue / scale + 0.5f);
        return res;
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(Context context, float spValue) {
        int res = 0;
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        if (spValue < 0)
            res = -(int) (-spValue * scale + 0.5f);
        else
            res = (int) (spValue * scale + 0.5f);
        return res;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        int res = 0;
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        if (pxValue < 0)
            res = -(int) (-pxValue / scale + 0.5f);
        else
            res = (int) (pxValue / scale + 0.5f);
        return res;
    }

    /**
     * 获取手机像素高宽
     */
    public static DisplayMetrics getWindowPx(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) manager.getDefaultDisplay().getMetrics(metric);
        return metric;
    }

    /**
     * 获取手机device
     */
    public static String getPhoneDevice(Context context) {
        // 系统#手机型号#系统版本#应用版本#宽#高#渠道号#
        String mtype = android.os.Build.MODEL; // 手机型号
        mtype = mtype.replace("#", "_");
        String mVersion = android.os.Build.VERSION.RELEASE; // android版本号
        DisplayMetrics metric = getWindowPx(context);
        String channalID = ChannelUtil.getChannel(context);
        String plantForm = context.getResources().getString(R.string.plantForm);
        return "and" + "#" + mtype + "#" + mVersion + "#" + VersionOp.getVerName(context) + "#" + metric.widthPixels + "#" + metric.heightPixels + "#" + channalID + "#";
    }

    /**
     * 获取总内存
     *
     * @return
     */
    public static String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            // for (String num : arrayOfString) {
            // Log.i(str2, num + "\t");
            // }

            // initial_memory = Integer.valueOf(arrayOfString[1]).intValue() *
            // 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            // 获得系统总内存，单位是KB，除以1024转换为M
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
            localBufferedReader.close();

        } catch (IOException e) {
            UtilLog.reportError("获取系统总内存", e);
        }
        // return Formatter.formatFileSize(mct,initial_memory);//
        // Byte转换为KB或者MB，内存大小规格化
        return String.valueOf(initial_memory);
    }

    /**
     * 获取android当前可用内存大小
     *
     * @param mct
     * @return
     */
    public static String getAvailMemory(Context mct) {
        if (mct != null) {
            ActivityManager am = (ActivityManager) mct.getSystemService(Context.ACTIVITY_SERVICE);
            MemoryInfo mi = new MemoryInfo();
            am.getMemoryInfo(mi);
            // mi.availMem; 当前系统的可用内存
            long availMem = mi.availMem / 1024 / 1024;
            return String.valueOf(availMem);
        } else
            return "0";
    }

    public static String getPackageName(Context context) {
        if (context != null) {
            return context.getPackageName();
        }
        return "";
    }

    /**
     * 获取手机网络状态类型
     */
    public static String getNetWorkType(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                switch (netInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        return "wifi";
                    case ConnectivityManager.TYPE_MOBILE:
                        switch (netInfo.getSubtype()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                                return "2G_GPRS";
                            case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                                return "2G_CDMA";
                            case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                                return "2G_EDGE";
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                                return "2G_1xRTT";
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                return "2G_IDEN";
                            case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                                return "3G_EVDO_A";
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                                return "3G_UMTS";
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                return "3G_EVDO_0";
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                                return "3G_HSDPA";
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                                return "3G_HSUPA";
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                                return "3G_HSPA";
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                return "3G_EVDO_B";
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                                return "3G_EHRPD";
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                return "3G_HSPAP";
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                return "4G_LTE";
                            default:
                                return "mobile";
                        }
                }
            }
        }
        return "null";
    }

    public static String getNetWorkSimpleType(Context context) {
        String networkType = getNetWorkType(context);
        if (!TextUtils.isEmpty(networkType)) {
            if (networkType.startsWith("wifi")) {
                return "wifi";
            } else if (networkType.startsWith("2G")) {
                return "2G";
            } else if (networkType.startsWith("3G")) {
                return "3G";
            } else if (networkType.startsWith("4G")) {
                return "4G";
            } else if (networkType.startsWith("mobile")) {
                return "mobile";
            }
        }
        return "null";
    }

    /**
     * @param context
     * @return 联网方式 0: 其它，1: WIFI，2:2G，3: 3G，4: 4G
     */
    public static int getNetWorkSimpleNum(Context context) {
        String networkType = getNetWorkType(context);
        if (!TextUtils.isEmpty(networkType)) {
            if (networkType.startsWith("wifi")) {
                return 1;
            } else if (networkType.startsWith("2G")) {
                return 2;
            } else if (networkType.startsWith("3G")) {
                return 3;
            } else if (networkType.startsWith("4G")) {
                return 4;
            } else if (networkType.startsWith("mobile")) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 手机网络是否连接;
     *
     * @param context
     * @return
     */
    public static boolean getNetActiveState(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return netInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 键盘控制
     *
     * @param ifShow 显示隐藏
     * @param con
     * @param view   键盘在哪个view上，如editText
     */
    public static void keyboardControl(boolean ifShow, Context con, View view) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (ifShow) {
            imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    /**
     * 获取用户后台App
     * @param context
     * @return
     */
    public static String getUserApp(Context context, String userCode) {
        String time = (String) UtilFile.loadShared(context, FileManager.xmlFile_appInfo, FileManager.xmlKey_upFavorTime);
        String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (time != "" && (Long.valueOf(currentTime) - Long.valueOf(time) < 7)) {
            return "";
        }
        // TODO 存储启动时间
        Map<String, String> map = new HashMap<String, String>();
        map.put(FileManager.xmlKey_upFavorTime, currentTime);
        UtilFile.saveShared(context, FileManager.xmlFile_appInfo, map);
        UtilLog.print("i", "oldTime:" + time + " currentTime:" + currentTime);

        JSONArray jsonArray = new JSONArray();
        JSONObject JsonObject = new JSONObject();
        try {
            PackageManager mPackageManager = context.getPackageManager();
            List<PackageInfo> mPackageInfoList = mPackageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
            // 设备码\tUSERCODE\t应用1;应用2;....;应用N
            String content = "";
            JsonObject.put("upFavorTime", currentTime);
            JsonObject.put("deviceInfo", ToolsDevice.getDeviceIdInfo(context));
            JsonObject.put("userCode", userCode + "");
            for (int i = 0; i < mPackageInfoList.size(); i++) {
                final PackageInfo packageInfo = mPackageInfoList.get(i);
                // 获取到非系统的app
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // 添加自己的代码即可
                    content = content + mPackageInfoList.get(i).packageName + ",";
                }
            }
            JsonObject.put("list", content);
        } catch (Exception e) {
            UtilLog.reportError("保存用户后台应用列表到jsonObject", e);
        }
        jsonArray.put(JsonObject);
        return jsonArray.toString();
    }

//    public static String getIp() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements();) {
//                    InetAddress inetAddress = ipAddr.nextElement();
//                    // ipv4地址
//                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (Exception ex) {
//        }
//        return "";
//    }

    //根据Wifi信息获取本地Mac
    public static String getMacAddressFromWifiInfo(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取手机IMEI
     * @param context
     * @return
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else
            return "111111111111111";
    }

    /**
     * 获取手机android_id
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (androidId != null) {
            return androidId;
        } else
            return "111111111111111";
    }

    // 上传应用错误信息和开应用时后台运行的app日志
    public static void sendCrashAndAppInfoToServer(Context context, String userCode) {
        if (!getNetActiveState(context)) {
            return;
        }
        String appList = getUserApp(context, userCode);
        if (appList != "") {
            uploadService(appList, StringManager.api_uploadFavorLog, new InternetCallback(XHApplication.in()) {
                @Override
                public void loaded(int flag, String url, Object returnObj) {
                    UtilLog.print("d", "uploadService:" + returnObj.toString());
                }
            });
        }
    }

    public static void uploadService(String jsonArray, String url, InternetCallback internetCallback) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("content", jsonArray);
        ReqInternet.in().doPost(url, map, internetCallback);
    }

    /**
     * 检测该包名在手机中的状态;
     * @param packageName 完整的包名;
     * @return 状态标志 0-未安装，1-已安装，2-运行在后台，3-当前运行
     */
    public static int isAppInPhone(Context context, String packageName) {
        int res = 0;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            return res;
        }
        res = 1;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 该包名是否在当前运行;isTop1就可以了;
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(100);
        for (RunningTaskInfo info : tasksInfo) {
            if (info != null && info.baseActivity != null && info.baseActivity.getPackageName().equals(packageName))
                res = 2;
            if (info != null && info.baseActivity != null && info.topActivity.getPackageName().equals(packageName)) {
                res = 3;
                break;
            }
        }
        return res;
    }

    /**
     * 为了去重,用此方法得到香哈经过处理过的imei,标示用户
     **/
    public static String getXhIMEI(Context context) {
        String deviceID = null;
        // 获取设备码
        if (context != null) {
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
             deviceID = "\"DEVICEID\":\"" + tManager.getDeviceId() + "\"-" + "\"ANDROID_ID\":\"" + context.getContentResolver()==null?"":Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) + "\"";
            deviceID = StringManager.stringToMD5(deviceID);
//			LogManager.print("d", "---------imei:" +deviceID);
        } else {
            deviceID = UtilFile.readFile(UtilFile.getDataDir() + FileManager.file_IMEI);
            deviceID = deviceID.replaceAll("\r\n", "");
            UtilLog.print("d", "------file---imei:" + deviceID);
        }
        if (deviceID == null || deviceID.equals(""))
            return "11111111111";

        // LogManager.print("d", "imei:" +deviceID);
        return deviceID;
    }

    /**
     * 确保获取到得到香哈经过处理过的imei,储存一次
     */
    public synchronized static void saveXhIMEI(final Context context) {
        if (UtilFile.ifFileModifyByCompletePath(UtilFile.getDataDir() + FileManager.file_IMEI, -1) == null) {
            String imei = getXhIMEI(context);
            if (!imei.equals("11111111111")) {
                new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        UtilFile.saveFileToCompletePath(UtilFile.getDataDir() + FileManager.file_IMEI, getXhIMEI(context), false);
                    }
                }.start();
            }
        }
    }

    /**
     * 每次传list<用户应用列表时>传,用户查看那个是可以作为用户的唯一标示
     **/
    public static JSONObject getDeviceIdInfo(Context context) {

        JSONObject jsonResult = new JSONObject();
        String deviceID = null;
        // 获取设备码
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceID = tManager.getDeviceId();
        try {
            jsonResult.put("DEVICEID", deviceID);
            jsonResult.put("SIMNO", tManager.getSimSerialNumber());
            // 对设备码进行加密
            jsonResult.put("IMSI", tManager.getSubscriberId());
            jsonResult.put("IMEI", getXhIMEI(context));
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                deviceID = (String) get.invoke(c, "ro.serialno");
                jsonResult.put("SERIAL", deviceID);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                deviceID = info.getMacAddress();
                jsonResult.put("MAC", deviceID);
            }
            // jsonResult.put("RANDOM",
            // SharePreferenceUtils.getSharePreferencesValue("random"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    // 获取当前版本
    public static String getVerName(Context context) {
        String verCode = "0.0.0";
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            UtilLog.reportError("版本号获取异常", e);
        }
        return verCode;
    }

    /**
     * 获取Device前半部分
     *
     * @param context
     */
    public static String getDevice(Context context) {
        if (context != null) {
            // 系统#手机型号#系统版本#应用版本#宽#高#渠道号#
            String device = UtilFile.loadShared(context, FileManager.xmlFile_appInfo, FileManager.xmlKey_device).toString();
            if (device.length() < 5) {
                return ToolsDevice.getPhoneDevice(XHApplication.in());
            }
            return device;
        }
        return "and#default#0#0#1080#1920#wifi#default#";
    }

    /**
     * @return 返回运营商对应的number
     */
    public static int getOperatorNum(Context con) {
        TelephonyManager telManager = (TelephonyManager)con.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {//中国移动
                return 1;
            } else if (operator.equals("46001")) {//中国联通
                return 2;
            } else if (operator.equals("46003")) {//中国电信
                return 3;
            }
        }
        return 0;
    }
    /** 判断是否支持闪光灯 */
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) //判断设备是否支持闪光灯
                        return true;
                }
            }
        }
        return false;
    }
    /**
     *设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param act
     * @return 1:MIUUI 2:Flyme
     */
    public static int modifyStateTextColor(Activity act) {
        int result=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(setMiuiStatusBarDarkMode(act, true)){
                result=1;
            }else if(FlymeSetStatusBarLightMode(act, true)){
                result=2;
            }/*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result=3;
            }*/
        }
        return result;
    }

    /**
     * 设置miui系统的状态栏字体颜色为深色
     *
     * @param activity
     * @param darkmode
     * @return
     */
    private static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param activity 需要设置的界面
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean FlymeSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                Window window = activity.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
}
