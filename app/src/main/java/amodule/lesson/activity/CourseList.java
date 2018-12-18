package amodule.lesson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ExpandableListView;

import com.xiangha.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import acore.override.activity.base.BaseAppCompatActivity;
import acore.tools.StringManager;
import amodule.lesson.adapter.SyllabusAdapter;
import amodule.lesson.controler.data.CourseDataController;
import aplug.basic.InternetCallback;
import aplug.basic.ReqInternet;

/**
 * Description :
 * PackageName : amodule.lesson.activity
 * Created by mrtrying on 2018/12/3 15:28.
 * e_mail : ztanzeyu@gmail.com
 */
public class CourseList extends BaseAppCompatActivity {
    public static final String EXTRA_GROUP = "group";
    public static final String EXTRA_CHILD = "child";
    public static final String EXTRA_FROM_STUDY = "from";
    public static final String EXTRA_CODE = "code";
    public static final String EXTRA_TYPE = "type";
    private SyllabusAdapter mSyllabusAdapter;
    private List<String> mGroupList = new ArrayList<>();
    private List<List<String>> mChildList = new ArrayList<>();
    private ExpandableListView mExList;
    private int mGroupSelectIndex = 2;
    private int mChildSelectIndex = 5;
    private boolean isFromStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity("课程表", 2, 0, R.layout.c_view_bar_title, R.layout.a_course_list);
        initView();
        initData();
    }

    private void initView() {
        mExList = findViewById(R.id.expand_list);
        mSyllabusAdapter = new SyllabusAdapter(this);
        mExList.setAdapter(mSyllabusAdapter);
    }

    private void initData() {
//        String mCode = getIntent().getStringExtra(EXTRA_CODE);
//        String mType = getIntent().getStringExtra(EXTRA_TYPE);
        isFromStudy = getIntent().getBooleanExtra(EXTRA_FROM_STUDY, false);
        loadManager.loading(mExList, true);
        CourseDataController.loadCourseListData("88", "1", new InternetCallback() {
            @Override
            public void loaded(int i, String s, Object o) {
                loadManager.loaded(mExList);
                if (i >= ReqInternet.REQ_OK_STRING) {
                    Map<String, String> mCourseListMap = StringManager.getFirstMap(o);
                    initCourseListData(mCourseListMap);
                }
            }
        });
    }

    private void initCourseListData(Map<String, String> mCourseListMap) {
        String chapterNum = mCourseListMap.get("chapterNum");
        Map<String, String> playHistory = StringManager.getFirstMap("playHistory");
        String chapterCode = playHistory.get("chapterCode");
        String lessonCode = playHistory.get("lessonCode");
        ArrayList<Map<String, String>> info = StringManager.getListMapByJson(mCourseListMap.get("chapterList"));


        if (TextUtils.equals("1", chapterNum)) {//只有一章
            mGroupSelectIndex = 0;
            ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(0).get("lessonList"));
            for (int j = 0; j < lessonList.size(); j++) {
                if (TextUtils.equals(lessonCode, lessonList.get(j).get("code"))) {
                    mChildSelectIndex = j;
                    break;
                }
            }
            initOne(info);
        } else {//多章
            for (int i = 0; i < info.size(); i++) {
                if (TextUtils.equals(chapterCode, info.get(i).get("code"))) {
                    mGroupSelectIndex = i;
                    ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(i).get("lessonList"));
                    for (int j = 0; j < lessonList.size(); j++) {
                        if (TextUtils.equals(lessonCode, lessonList.get(j).get("code"))) {
                            mChildSelectIndex = j;
                            break;
                        }
                    }
                    break;
                }
            }
            initMore(info);
        }
        mSyllabusAdapter.setChildList(mChildList);
        mSyllabusAdapter.setGroupList(mGroupList);
        mSyllabusAdapter.notifyDataSetChanged();
        mSyllabusAdapter.setSelectIndex(mGroupSelectIndex, mChildSelectIndex);

        mExList.expandGroup(mGroupSelectIndex);
    }

    /**
     * @param info 只有一章
     */
    private void initOne(ArrayList<Map<String, String>> info) {
        ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(0).get("lessonList"));
        ArrayList<String> child = new ArrayList<>();
        for (Map<String, String> map : lessonList) {
            mGroupList.add(map.get("title"));
            mChildList.add(child);
        }

        //设置分组项的点击监听事件
        mExList.setOnGroupClickListener((expandableListView, view, i, l) -> {
            mChildSelectIndex = -1;
            clickItem(lessonList.get(i).get("code"));
            return false;
        });

    }

    /**
     * @param info 多章
     */
    private void initMore(ArrayList<Map<String, String>> info) {
        for (int i = 0; i < info.size(); i++) {
            mGroupList.add(info.get(i).get("title"));
            ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(i).get("lessonList"));
            ArrayList<String> strings = new ArrayList<>();
            for (Map<String, String> map : lessonList) {
                strings.add(map.get("title"));
            }
            mChildList.add(strings);
        }

        //设置子选项点击监听事件
        mExList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(groupPosition).get("lessonList"));
            mGroupSelectIndex = groupPosition;
            mChildSelectIndex = childPosition;
            clickItem(lessonList.get(childPosition).get("code"));
            return true;
        });
    }


    private void clickItem(String code) {
        mSyllabusAdapter.setSelectIndex(mGroupSelectIndex, mChildSelectIndex);
        mSyllabusAdapter.notifyDataSetChanged();
        if (isFromStudy) {
            Intent intent = new Intent();
            intent.putExtra("code", code);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent it = new Intent(this, CourseDetail.class);
            it.putExtra("code", code);
            startActivity(it);
        }
    }
}
