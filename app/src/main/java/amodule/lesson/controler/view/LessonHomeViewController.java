package amodule.lesson.controler.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xiangha.R;

import java.util.List;
import java.util.Map;

import acore.logic.MessageTipController;
import acore.logic.XHClick;
import acore.widget.rvlistview.RvListView;
import amodule.home.view.HomePushIconView;
import amodule.main.StatisticData;
import amodule.main.delegate.ISetMessageTip;
import amodule.main.view.MessageTipIcon;
import cn.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Description :
 * PackageName : amodule.lesson.controler.view
 * Created by mrtrying on 2017/12/19 11:23:38.
 * e_mail : ztanzeyu@gmail.com
 */
public class LessonHomeViewController implements View.OnClickListener, ISetMessageTip {

    private Activity mActivity;

    private View mHeaderView;

    private PtrClassicFrameLayout mRefreshLayout;
    private RvListView mRvListView;

    private LessonHomeHeaderControler mHeaderControler;

    private MessageTipIcon mMessageTipIcon;
    private HomePushIconView mPushIconView;

    public LessonHomeViewController(Activity activity) {
        this.mActivity = activity;
        mHeaderView = LayoutInflater.from(mActivity).inflate(R.layout.a_lesson_header_layout, null, true);
    }

    public void onCreate() {
        initUI();
        setStatisticsData();
    }

    public void onResume() {
        mMessageTipIcon.setMessage(MessageTipController.newInstance().getMessageNum());
    }

    public void onPause() {

    }

    public void onDestroy() {

    }

    private void initUI() {
        TextView title = (TextView) mActivity.findViewById(R.id.title);
        title.setText("VIP会员");
        mMessageTipIcon = (MessageTipIcon) mActivity.findViewById(R.id.message_tip);
        mPushIconView = (HomePushIconView) mActivity.findViewById(R.id.favorite_pulish);
        mPushIconView.setOnClickListener(this);
        mActivity.findViewById(R.id.back).setVisibility(View.INVISIBLE);
        //创建headerData控制器
        mHeaderControler = new LessonHomeHeaderControler(mHeaderView);

        mRefreshLayout = (PtrClassicFrameLayout) mActivity.findViewById(R.id.refresh_list_view_frame);
        mRefreshLayout.disableWhenHorizontalMove(true);
        mRvListView = (RvListView) mActivity.findViewById(R.id.rvListview);
        mRvListView.addHeaderView(mHeaderView);
    }

    private void setStatisticsData() {
        StatisticData data = new StatisticData();
        data.setId("vip_homepage621");
        data.setContentTwo("顶部tapbar");
        data.setContentThree("消息");
        mMessageTipIcon.setStatisticsData(mMessageTipIcon.type_click, data);
    }

    public void setHeaderData(List<Map<String, String>> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mHeaderControler.setData(data);
    }

    //回到第一个位置
    public void returnListTop() {
        if (mRvListView != null) {
            mRvListView.scrollToPosition(0);
        }
    }

    public void refreshComplete() {
        if (null != mRefreshLayout)
            mRefreshLayout.refreshComplete();
    }

    public void autoRefresh() {
        if (null != mRefreshLayout)
            mRefreshLayout.autoRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_pulish:
                if (mPushIconView != null) {
                    mPushIconView.showPulishMenu();
                    XHClick.mapStat(mActivity, "vip_homepage621", "顶部tapbar", "发布");
                }
                break;
            default:
                break;
        }
    }

    public void saveStatisticData(String page) {
        if (mHeaderControler != null) {
            mHeaderControler.saveStatisticData(page);
        }
    }

    /*--------------------------------------------- Get&Set ---------------------------------------------*/

    public RvListView getRvListView() {
        return mRvListView;
    }

    public PtrClassicFrameLayout getRefreshLayout() {
        return mRefreshLayout;
    }


    @Override
    public void setMessageTip(int tipCournt) {
        if (mMessageTipIcon != null) {
            mMessageTipIcon.setMessage(tipCournt);
        }
    }
}
