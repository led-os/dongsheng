package acore.logic;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.Map;

import acore.override.XHApplication;
import acore.tools.FileManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import acore.tools.ToolsDevice;
import aplug.basic.ReqInternet;
import aplug.basic.XHInternetCallBack;

import static acore.logic.ConfigMannager.KEY_ADVIDEO;

/**
 * Description :
 * PackageName : acore.logic
 * Created by mrtrying on 2018/1/25 14:06:48.
 * e_mail : ztanzeyu@gmail.com
 */
public class AdVideoConfigTool {

    private final String configValue;

    private final Map<String,String> configMap;

    public AdVideoConfigTool(){
        this.configValue = ConfigMannager.getConfigByLocal(KEY_ADVIDEO);
        configMap = StringManager.getFirstMap(configValue);
    }

    public static AdVideoConfigTool of(){
        return new AdVideoConfigTool();
    }

    public void updateAdVideoData(String dataValue) {
         String videoUrl = getVideoUrl();
         String filePath = getVideoPath(videoUrl);
        if (!TextUtils.isEmpty(dataValue)) {
            Map<String, String> data = StringManager.getFirstMap(dataValue);
            data = StringManager.getFirstMap(data.get(KEY_ADVIDEO));
            videoUrl = data.get("videoUrl");
            filePath = getVideoPath(videoUrl);
        }

        final String originalFilePath = getVideoPath(getVideoUrl());
        if (TextUtils.equals(filePath, originalFilePath)) {
            if (!checkVideoExist(filePath)) {
                downloadVideo(videoUrl, filePath);
            }
            return;
        }
        //下载视频文件
        downloadVideo(videoUrl, filePath);
    }

    public Map<String,String> getConfigMap(){
        if(configMap.isEmpty()){
            configMap.putAll(StringManager.getFirstMap(configValue));
        }
        return configMap;
    }

    public boolean isOpen(){
        return !"1".equals(configMap.get("isOpen"));
    }

    public String getVideoUrlOrPath(){
        Map<String,String> configMap = getConfigMap();
        String url = configMap.get("videoUrl");
        if (TextUtils.isEmpty(url)){
            return "";
        }
        String path = getVideoPath(url);
        return checkVideoExist(path) ? path : url;
    }

    @NonNull
    private String getVideoPath(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)
                || !videoUrl.contains(".mp4")) {
            return "";
        }
        return FileManager.getSDDir() + Tools.getMD5(videoUrl) + ".mp4";
    }

    private String getVideoUrl() {
        Map<String, String> data = getConfigMap();
        return data.get("videoUrl");
    }

    private boolean checkVideoExist(String filePath) {
        return FileManager.ifFileModifyByCompletePath(filePath, -1) != null;
    }

    private void downloadVideo(String videoUrl, String filePath) {
        if(TextUtils.isEmpty(videoUrl)
                || TextUtils.isEmpty(filePath)){
            return;
        }
        if("wifi".equals(ToolsDevice.getNetWorkSimpleType(XHApplication.in()))){
            ReqInternet.in().getInputStream(videoUrl, new XHInternetCallBack() {
                @Override
                public void loaded(int i, String s, Object o) {
                    if (i >= ReqInternet.REQ_OK_IS
                            && o != null && o instanceof InputStream) {
                        new Thread(() -> FileManager.saveFileToCompletePath(filePath, (InputStream) o, false)).start();
                    }
                }
            });
        }
    }
}
