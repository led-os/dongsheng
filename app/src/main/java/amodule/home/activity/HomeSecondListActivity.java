package amodule.home.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.xiangha.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import acore.override.activity.base.BaseAppCompatActivity;
import acore.tools.StringManager;
import acore.tools.ToolsDevice;
import acore.widget.PagerSlidingTabStrip;
import amodule.home.HomeModuleControler;
import amodule.home.adapter.HomeSecondListPagerAdapter;
import amodule.home.fragment.HomeSecondListFragment;
import amodule.home.module.HomeSecondModule;
import amodule.main.bean.HomeModuleBean;

/**
 * 首页的二级页面（视频、每日三餐）
 * Created by sll on 2017/11/13.
 */

public class HomeSecondListActivity extends BaseAppCompatActivity {

    public static final String TAG = "type";

    private String mType;//视频、三餐

    private PagerSlidingTabStrip mHomeTabStrip;
    private ViewPager mViewPager;
    private HomeSecondListPagerAdapter mPagerAdapter;
    private HomeModuleBean mModuleBean;
    private ArrayList<HomeSecondModule> mSecondModules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initActivity("", 2, 0, R.layout.back_title_bar, R.layout.home_second_list_layout);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        mType = bundle.getString("type1");
        if (TextUtils.isEmpty(mType)) {
            finish();
            return;
        }
        mModuleBean = new HomeModuleControler().getHomeModuleByType(this,mType);
        mSecondModules = new ArrayList<HomeSecondModule>();
        ArrayList<Map<String, String>> levels = StringManager.getListMapByJson(mModuleBean.getTwoData());
        for (Map<String, String> level : levels) {
            if (level != null) {
                HomeSecondModule secondModule = new HomeSecondModule();
                secondModule.setTitle(level.get("title"));
                secondModule.setType(level.get("two_type"));
                mSecondModules.add(secondModule);
            }
        }
        if (mSecondModules.isEmpty()) {
            this.finish();
            return;
        }
        //初始化UI
        initView();
    }

    private void initView() {
        mHomeTabStrip = (acore.widget.PagerSlidingTabStrip) findViewById(R.id.home_tab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        TextView titleV = (TextView) findViewById(R.id.title);
        titleV.setMaxWidth(ToolsDevice.getWindowPx(this).widthPixels - ToolsDevice.dp2px(this, 45 + 40));
        if(mModuleBean != null)
            titleV.setText(mModuleBean.getTitle());
        mPagerAdapter = new HomeSecondListPagerAdapter(getSupportFragmentManager(), mSecondModules, mModuleBean);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mHomeTabStrip.select(mHomeTabStrip.getmTabsContainer().getChildAt(i));
                mHomeTabStrip.notifyDataSetChanged();
                refreshAdData(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        int tabBgResId = 0;
        switch (mType) {
            case "video":
                tabBgResId = R.drawable.selector_hometabscroll_item_bg;
                break;
            case "day":

                break;
        }
        if (tabBgResId != 0)
            mHomeTabStrip.setTabInnerBackground(tabBgResId);
        mHomeTabStrip.setTabInnerTextPadding(getPxByDp(R.dimen.dp_13), getPxByDp(R.dimen.dp_5), getPxByDp(R.dimen.dp_13), getPxByDp(R.dimen.dp_5));
        mHomeTabStrip.setTextColorStateListResource(R.color.selector_hometabscroll_item_textcolor);
        mHomeTabStrip.setViewPager(mViewPager);
        mHomeTabStrip.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                refreshFragment(position);
            }
        });
    }

    private int getPxByDp(int resDp) {
        return getResources().getDimensionPixelSize(resDp);
    }

    private void refreshFragment(int position) {
        HomeSecondListFragment fragment = getFragmentByPosition(position);
        if (fragment == null)
            return;
        fragment.refresh();
    }

    private HomeSecondListFragment getFragmentByPosition(int position) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment instanceof HomeSecondListFragment) {
                    HomeSecondListFragment listFragment = (HomeSecondListFragment) fragment;
                    if (position == listFragment.getPosition())
                        return listFragment;
                }
                continue;
            }
        }
        return null;
    }

    private void refreshAdData(int position) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > position && fragments.get(position) instanceof HomeSecondListFragment) {
            ((HomeSecondListFragment) fragments.get(position)).isNeedRefresh(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
