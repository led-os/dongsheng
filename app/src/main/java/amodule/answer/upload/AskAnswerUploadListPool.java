package amodule.answer.upload;

import android.app.Activity;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import acore.override.XHApplication;
import acore.override.helper.XHActivityManager;
import acore.tools.StringManager;
import acore.tools.Tools;
import amodule.answer.db.AskAnswerSQLite;
import amodule.answer.model.AskAnswerModel;
import amodule.article.activity.ArticleUploadListActivity;
import amodule.main.Main;
import amodule.upload.UploadListPool;
import amodule.upload.bean.UploadItemData;
import amodule.upload.bean.UploadPoolData;
import amodule.upload.callback.UploadListUICallBack;

/**
 * Created by sll on 2017/7/20.
 */

public class AskAnswerUploadListPool extends UploadListPool {

    private AtomicBoolean mHasDoUploadLastInfo = new AtomicBoolean(false);
    private boolean mFlag;

    private AskAnswerSQLite mSQLite;

    @Override
    protected void initData(int draftId, String coverPath, String finalVideoPath, String timestamp, UploadListUICallBack callback) {
        super.initData(draftId, coverPath, finalVideoPath, timestamp, callback);
        mSQLite = new AskAnswerSQLite(XHApplication.in().getApplicationContext());
        uploadPoolData.setDraftId(draftId);
        AskAnswerModel model = modifyUploadListPoolData();
        uploadPoolData.setUploadAskAnswerData(model);
        uploadPoolData.setTitle(model.getmTitle());
        super.setUploadProcess();
    }

    /**
     * 修改上传池数据 从草稿箱数据库读取数据，修改后，添加到上传池
     */
    public AskAnswerModel modifyUploadListPoolData() {
        AskAnswerModel model = mSQLite.queryData(uploadPoolData.getDraftId());
        Log.e("SLL","修改上传池数据 draftId:" + uploadPoolData.getDraftId());
        if (model != null) {

            ArrayList<UploadItemData> bodyItemDatas = new ArrayList<>();
            int bodyIndex = 0;
            String imgsDataStr = model.getmImgs();
            Log.e("SLL","修改上传池数据 imgsDataStr:" + imgsDataStr);
            if (!TextUtils.isEmpty(imgsDataStr)) {
                UploadItemData bodyItemData;
                ArrayList<Map<String, String>> makesList = StringManager.getListMapByJson(imgsDataStr);
                if (makesList != null && makesList.size() > 0) {
                    for (int i = 0; i < makesList.size(); i++) {
                        Map<String, String> map = makesList.get(i);
                        String imgPath = map.get("img");
                        String imgUrl = map.get("url");

                        Log.e("SLL", "文章上传 imgPath: " + imgPath + ",imgUrl:" + imgUrl);
                        Log.e("SLL", "文章上传 imgPath.indexOf(\"http\"): " + imgPath.indexOf("http"));
                        if (imgPath.indexOf("http") != 0 && !Tools.isFileExists(imgPath)) {
                            Toast.makeText(Main.allMain, "获取不到文章图片路径 " + i, Toast.LENGTH_SHORT).show();
                            return null;
                        }

                        bodyItemData = new UploadItemData();
                        bodyItemData.setPath(imgPath);
                        bodyItemData.setRecMsg(imgUrl);
                        bodyItemData.setIndex(bodyIndex++);
                        bodyItemData.setMakeStep("图片" + (i+1));
                        bodyItemData.setType(UploadItemData.TYPE_BREAKPOINT_IMG);
                        bodyItemData.setPos(UploadItemData.POS_BODY);
                        bodyItemDatas.add(bodyItemData);
                    }
                }
            }

            ArrayList<UploadItemData> headItemDatas = new ArrayList<>();
            int headIndex = 0;
            String videosDataStr = model.getmVideos();
            if (!TextUtils.isEmpty(videosDataStr)) {
                UploadItemData captureVideoData;
                ArrayList<Map<String, String>> videosList = StringManager.getListMapByJson(videosDataStr);
                if (videosList != null && videosList.size() > 0) {
                    for (int i = 0; i < videosList.size(); i++) {
                        Map<String, String> map = videosList.get(i);
                        String videoPath = map.get("video");
                        String videoUrl = map.get("videoUrl");
                        String videoImage = map.get("thumImg");
                        String imageUrl = map.get("imageUrl");

                        Log.e("articleUpload", "文章上传 videoPath: " + videoPath + "  videoUrl:" + videoUrl);
                        Log.e("articleUpload", "文章上传 videoImage: " + videoImage + "  imageUrl:" + imageUrl);
                        if (videoPath.indexOf("http") != 0 && !Tools.isFileExists(videoPath)) {
                            Toast.makeText(Main.allMain, "获取不到文章视频路径 " + i, Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        if (videoImage.indexOf("http") != 0 && !Tools.isFileExists(videoImage)) {
                            Toast.makeText(Main.allMain, "获取不到文章视频图片路径", Toast.LENGTH_SHORT).show();
                            return null;
                        }

                        UploadItemData videoImgItemData = new UploadItemData();
                        videoImgItemData.setPath(videoImage);
                        videoImgItemData.setRecMsg(imageUrl);
                        videoImgItemData.setIndex(headIndex++);
                        videoImgItemData.setMakeStep("视频封面" + (i+1));
                        videoImgItemData.setType(UploadItemData.TYPE_BREAKPOINT_IMG);
                        videoImgItemData.setPos(UploadItemData.POS_HEAD);
                        headItemDatas.add(videoImgItemData);

                        captureVideoData = new UploadItemData();
                        captureVideoData.setPath(videoPath);
                        captureVideoData.setVideoImage(videoImage);
                        captureVideoData.setRecMsg(videoUrl);
                        captureVideoData.setIndex(bodyIndex++);
                        captureVideoData.setMakeStep("视频" + (i+1));
                        captureVideoData.setPos(UploadItemData.POS_BODY);
                        captureVideoData.setType(UploadItemData.TYPE_VIDEO);
                        bodyItemDatas.add(captureVideoData);
                    }
                }
            }
            uploadPoolData.setHeadDataList(headItemDatas);
            uploadPoolData.setBodyDataList(bodyItemDatas);


            ArrayList<UploadItemData> tailItemDatas = new ArrayList<>();
            UploadItemData tailItemData = new UploadItemData();
            tailItemData.setType(UploadItemData.TYPE_LAST_TEXT);
            tailItemData.setMakeStep("文字等信息");
            tailItemData.setPos(UploadItemData.POS_TAIL);
            tailItemData.setIndex(0);
            tailItemDatas.add(tailItemData);
            uploadPoolData.setTailDataList(tailItemDatas);
        } else {
            Toast.makeText(XHApplication.in(), "uploadArticleData 从数据库中没有获取到数据", Toast.LENGTH_SHORT).show();
        }

        return model;
    }

    @Override
    public void allStartOrStop(int operation) {
        super.allStartOrStop(operation);
        if (!isCancel) {
            AskAnswerModel model = modifyUploadData(false);
            saveDataToSqlit(model);
        }
    }

    private AskAnswerModel modifyUploadData(final boolean isReset) {
        final AskAnswerModel model = uploadPoolData.getUploadAskAnswerData();
        if (model == null){
            return null;
        }

        //视频首图
        List<UploadItemData> headDataList = uploadPoolData.getHeadDataList();
        if(headDataList != null && headDataList.size() > 0){
            final ArrayList<Map<String, String>> makesList = StringManager.getListMapByJson(model.getmVideos());
            uploadPoolData.loopPoolData(headDataList, new UploadPoolData.LoopCallback() {
                @Override
                public boolean onLoop(UploadItemData itemData) {
                    if (itemData.getType() == UploadItemData.TYPE_BREAKPOINT_IMG) {
                        for (int i = 0; i < makesList.size(); i++) {
                            Map<String, String> map = makesList.get(i);
                            Log.e("SLL","视频图片信息 map.path:" + map.get("image") + "  url:" + itemData.getRecMsg());
                            if (map.get("thumImg").equals(itemData.getPath())) {
                                map.put("imageUrl",isReset ? "" : itemData.getRecMsg());
                                break;
                            }
                        }
                    }
                    return false;
                }
            });
            model.setmVideos(makesList);
        }
        final List<UploadItemData> bodyItemDatas = uploadPoolData.getBodyDataList();
        //视频信息
        if(bodyItemDatas != null && bodyItemDatas.size() > 0) {
            final ArrayList<Map<String, String>> makesList = StringManager.getListMapByJson(model.getmVideos());
            uploadPoolData.loopPoolData(bodyItemDatas, new UploadPoolData.LoopCallback() {
                @Override
                public boolean onLoop(UploadItemData itemData) {
                    if (itemData.getType() == UploadItemData.TYPE_VIDEO) {
                        for (int i = 0; i < makesList.size(); i++) {
                            Map<String, String> map = makesList.get(i);
                            Log.e("SLL","视频信息 map.path:" + map.get("video") + "  url:" + itemData.getRecMsg());
                            if (map.get("video").equals(itemData.getPath())) {
                                map.put("videoUrl",isReset ? "" : itemData.getRecMsg());
                                break;
                            }
                        }
                    }
                    return false;
                }
            });
            model.setmVideos(makesList);
        }
        //图片信息
        if(bodyItemDatas != null && bodyItemDatas.size() > 0) {
            final ArrayList<Map<String, String>> makesList = StringManager.getListMapByJson(model.getmImgs());
            uploadPoolData.loopPoolData(bodyItemDatas, new UploadPoolData.LoopCallback() {
                @Override
                public boolean onLoop(UploadItemData itemData) {
                    if (itemData.getType() == UploadItemData.TYPE_BREAKPOINT_IMG) {
                        Log.e("SLL","图片信息 path:" + itemData.getPath() + "  type:" + itemData.getType() + "  size:" + makesList.size());
                        for (int i = 0; i < makesList.size(); i++) {
                            Map<String, String> map = makesList.get(i);
                            Log.e("SLL","图片信息 map.path:" + map.get("path") + "  url:" + itemData.getRecMsg());
                            if (map.get("img").equals(itemData.getPath())) {
                                map.put("url",isReset ? "" : itemData.getRecMsg());
                                break;
                            }
                        }
                    }
                    return false;
                }
            });
            model.setmImgs(makesList);
        }
        return model;
    }

    /**
     * 将上传池中的线上路径存入草稿数据库
     */
    private void saveDataToSqlit(AskAnswerModel model) {
        mSQLite.updateData((int) model.getmId(),model);
    }

    @Override
    public void uploadOver(final boolean flag, final String response) {
        Log.i("articleUpload","uploadOver flag:" + flag + "   response:" + response);
        if (isPause)
            return;
        if (mUploadOverListener != null) {
            mUploadOverListener.onUploadOver(flag, response);
            return;
        }
        uploadPoolData.loopPoolData(uploadPoolData.getTailDataList(),
                new UploadPoolData.LoopCallback() {
                    @Override
                    public boolean onLoop(UploadItemData itemData) {
                        if (itemData.getType() == UploadItemData.TYPE_LAST_TEXT) {
                            itemData.setRecMsg(response);
                            if (flag) {
                                itemData.setState(UploadItemData.STATE_SUCCESS);
                                deleteData();
                                uploadPoolData.setUploadAskAnswerData(null);
                            } else {
                                mHasDoUploadLastInfo.set(false);
                                itemData.setState(UploadItemData.STATE_FAILD);
                                AskAnswerModel model = modifyUploadData(false);
                                saveDataToSqlit(model);
                            }
                            return true;
                        }
                        return false;
                    }
                });

        super.uploadOver(flag, response);
        Activity act = XHActivityManager.getInstance().getCurrentActivity();
        if (Tools.isForward(XHApplication.in())) {
            //TODO 跳转到问答详情页
        }
    }

    public interface UploadOverListener {
        void onUploadOver(boolean flag, String response);
    }

    private UploadOverListener mUploadOverListener;
    public void setUploadOverListener(UploadOverListener overListener) {
        mUploadOverListener = overListener;
    }

    public UploadOverListener getUploadOverListener() {
        return mUploadOverListener;
    }

    /**
     * 删除草稿箱数据库，上传成功后调用
     */
    private void deleteData() {
        mSQLite.deleteData(uploadPoolData.getDraftId());
    }

    @Override
    public void uploadLast() {
        Log.i("articleUpload","uploadLast()");

        //排除物料上传成功后，重复回调物料上传成功，触发上传最后一步
        if(mHasDoUploadLastInfo.get())
            return;
        Log.e("SLL","uploadLast() checkStuffUploadOver:" + checkStuffUploadOver());
        if (checkStuffUploadOver()) {
            mHasDoUploadLastInfo.set(true);
            final LinkedHashMap<String, String> params = combineParameter();
            List<UploadItemData> tailItemDatas = uploadPoolData.getTailDataList();
            uploadPoolData.loopPoolData(tailItemDatas, new UploadPoolData.LoopCallback() {
                @Override
                public boolean onLoop(UploadItemData itemData) {
                    if (itemData.getType() == UploadItemData.TYPE_LAST_TEXT) {
                        itemData.setUploadUrl(StringManager.API_QA_QAADD);
                        itemData.setUploadMsg(params);
                        return true;
                    }
                    return false;
                }
            });
            super.uploadLast();

        }
    }

    private void combineContent(LinkedHashMap<String, String> uploadData, AskAnswerModel model) {
        if (model == null)
            return;
        String text = model.getmText();
        uploadData.put("content[content][0][text]", text);
        ArrayList<Map<String, String>> imgsMap = StringManager.getListMapByJson(model.getmImgs());
        if (imgsMap == null || imgsMap.isEmpty())
            return;
        for (int i = 0; i < imgsMap.size(); i ++) {
            uploadData.put("content[content][0][imgs][" + i + "]", imgsMap.get(i).get("url"));
        }
    }

    public LinkedHashMap<String, String> combineParameter() {
        AskAnswerModel model = uploadPoolData.getUploadAskAnswerData();
        LinkedHashMap<String, String> uploadTextData = new LinkedHashMap<>();
        if (model == null) {
            return uploadTextData;
        }
        combineContent(uploadTextData, model);
        uploadTextData.put("code", model.getmDishCode());
        ArrayList<Map<String, String>> videos = StringManager.getListMapByJson(model.getmVideos());
        uploadTextData.put("video", videos.size() > 0 ? videos.get(0).get("videoUrl") : "");
        uploadTextData.put("imageurl", videos.size() > 0 ? videos.get(0).get("imageUrl") : "");
        uploadTextData.put("isAno", model.getmAnonymity());
        uploadTextData.put("qaType", model.getmType());
        uploadTextData.put("qaCode", model.getmQACode());
        uploadTextData.put("ansCode", model.getmAnswerCode());
        uploadTextData.put("authorCode", model.getmAuthorCode());
        uploadTextData.put("price", model.getmPrice());

        Log.e("SLL", "combineParaeter uploadTextData = " + uploadTextData.toString());

        return uploadTextData;
    }

    private String replaceUrl(String content,ArrayList<Map<String,String>> arrayList,String pathKey,String urlKey){
        for(int i = 0; i < arrayList.size(); i++){
            Map<String,String> map = arrayList.get(i);
            String path = map.get(pathKey);
            String url = map.get(urlKey);
            String newPath = path.replace("/","\\/");
            String newUrl = url.replace("/","\\/");
            Log.i("articleUpload","combineParameter() path:" + path + "   url:" + url);
            content = content.replace(newPath,newUrl);
            Log.i("articleUpload","combineParameter() newPath:" + newPath + "   newUrl:" + newUrl);
        }
        return content;
    }

    /**
     * 检查物料是否上传完毕
     *
     * @return allTingUploadSucess true 是，false 否
     */
    private boolean checkStuffUploadOver() {
        mFlag = true;
        uploadPoolData.loopPoolData(uploadPoolData.getHeadDataList(), new UploadPoolData.LoopCallback() {
            @Override
            public boolean onLoop(UploadItemData itemData) {
                Log.i("articleUpload","checkStuffUploadOver() ---HeadData--- itemData.getRecMsg():" + itemData.getRecMsg());
                if (TextUtils.isEmpty(itemData.getRecMsg())) {
                    mFlag = false;
                    return true;
                }
                return false;
            }
        });

        if (!mFlag) {
            return false;
        }
        uploadPoolData.loopPoolData(uploadPoolData.getBodyDataList(), new UploadPoolData.LoopCallback() {
            @Override
            public boolean onLoop(UploadItemData itemData) {
                Log.i("articleUpload","checkStuffUploadOver()   ---BodyData--- itemData.getRecMsg():" + itemData.getRecMsg());
                if (TextUtils.isEmpty(itemData.getRecMsg())) {
                    mFlag = false;
                    return true;
                }
                return false;
            }
        });
        return mFlag;
    }

    /**
     * 上传物料结束
     *
     * @param flag        成功  false 失败
     * @param uniquId     唯一标示
     * @param responseStr
     * @param jsonObject
     */
    @Override
    protected void uploadThingOver(final boolean flag, final String uniquId, final String responseStr, final JSONObject jsonObject) {
        Log.i("articleUpload","uploadThingOver() isPause:" + isPause);
        if (isPause)
            return;
        uploadPoolData.loopPoolData(uploadPoolData.getTotalDataList(),
                new UploadPoolData.LoopCallback() {
                    @Override
                    public boolean onLoop(UploadItemData itemData) {
                        if (itemData.getUniqueId().equals(uniquId)) {
                            if (flag) {
                                if (jsonObject != null)
                                    Log.e("itemData ", responseStr+","+itemData.getType() + "," + jsonObject.optString("hash"));

                                if (UploadItemData.TYPE_VIDEO == itemData.getType()){
                                    //处理url,has值丢失
                                    if(TextUtils.isEmpty(responseStr)||(TextUtils.isEmpty(jsonObject.optString("hash")))){
                                        Toast.makeText(XHApplication.in(),"hash为空",Toast.LENGTH_SHORT).show();
                                        itemData.setHashCode("");
                                        itemData.setRecMsg("");
                                        itemData.setState(UploadItemData.STATE_FAILD);
                                        return true;
                                    }else{
                                        itemData.setHashCode(jsonObject.optString("hash"));
                                        itemData.setRecMsg(responseStr);
                                        itemData.setState(UploadItemData.STATE_SUCCESS);
                                    }
                                }else{
                                    itemData.setState(UploadItemData.STATE_SUCCESS);
                                    itemData.setRecMsg(responseStr);
                                }
                                AskAnswerModel model = modifyUploadData(false);
                                if(model != null) {
                                    saveDataToSqlit(model);
                                }
                            } else {
                                itemData.setState(UploadItemData.STATE_FAILD);
                            }
                            return true;
                        }
                        return false;
                    }
                });

        uploadLast();
        super.uploadThingOver(flag, uniquId, responseStr, jsonObject);
    }
}
