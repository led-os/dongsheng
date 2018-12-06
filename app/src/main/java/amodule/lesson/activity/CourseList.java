package amodule.lesson.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

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

    private SyllabusAdapter mSyllabusAdapter;
    private List<String> groupList = new ArrayList<>();
    private List<List<String>> childList = new ArrayList<>();
    private ExpandableListView mExList;

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
        loadManager.loading(mExList, true);
        CourseDataController.loadCourseListData("0", "1", new InternetCallback() {
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
        ArrayList<Map<String, String>> info = StringManager.getListMapByJson(mCourseListMap.get("info"));
//        ArrayList<Map<String, String>> info = new ArrayList<>();
//        info.add(info1.get(0));
        if (info.size() == 1) {//只有一章
            initOne(info);
        } else {//多章
            initMore(info);
        }
        mSyllabusAdapter.setChildList(childList);
        mSyllabusAdapter.setGroupList(groupList);
        mSyllabusAdapter.notifyDataSetChanged();
    }

    /**
     * @param info 只有一章
     */
    private void initOne(ArrayList<Map<String, String>> info) {
        ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(0).get("lessonList"));
        ArrayList<String> strings = new ArrayList<>();
        for (Map<String, String> map : lessonList) {
            groupList.add(map.get("title"));
            childList.add(strings);
        }

        //设置分组项的点击监听事件
        mExList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), groupList.get(i), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    /**
     * @param info 多章
     */
    private void initMore(ArrayList<Map<String, String>> info) {
        for (int i = 0; i < info.size(); i++) {
            groupList.add(info.get(i).get("title"));
            ArrayList<Map<String, String>> lessonList = StringManager.getListMapByJson(info.get(i).get("lessonList"));
            ArrayList<String> strings = new ArrayList<>();
            for (Map<String, String> map : lessonList) {
                strings.add(map.get("title"));
            }
            childList.add(strings);
        }

        //设置分组项的点击监听事件
        mExList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), groupList.get(i), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //设置子选项点击监听事件
        mExList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), childList.get(groupPosition).get(childPosition), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

}