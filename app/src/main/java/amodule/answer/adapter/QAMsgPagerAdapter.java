package amodule.answer.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xianghatest.R;

import java.util.ArrayList;

import acore.widget.PagerSlidingTabStrip;
import amodule.answer.fragment.QAMsgListFragment;
import amodule.answer.model.QAMsgModel;

/**
 * Created by sll on 2017/7/28.
 */

public class QAMsgPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    private ArrayList<View> mTabViews = new ArrayList<View>();
    private ArrayList<QAMsgModel> mDatas;
    private ArrayList<QAMsgListFragment> mFragments;

    public QAMsgPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(ArrayList<QAMsgModel> datas) {
        if (datas == null)
            return;
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDatas.get(position).getmTitle();
    }

    @Override
    public Fragment getItem(int position) {
        if (mDatas == null || mDatas.isEmpty())
            return null;
        if (mFragments == null)
            mFragments = new ArrayList<QAMsgListFragment>();
        if (mFragments.size() > position)
            return mFragments.get(position);
        else {
            QAMsgListFragment fragment = QAMsgListFragment.newInstance(mDatas.get(position));
            mFragments.add(fragment);
            return fragment;
        }
    }

    public QAMsgListFragment getCurrentFragment () {
        return (QAMsgListFragment) getItem(mCurrentSelectedHolder == null ? 0 : mCurrentSelectedHolder.mPosition);
    }

    @Override
    public int getCount() {
        return (mDatas == null || mDatas.isEmpty()) ? 0 : mDatas.size();
    }

    @Override
    public void onRemoveAllTabView() {
        mTabViews.clear();
    }

    @Override
    public View getCustomTabView(ViewGroup parent, int position) {
        TabHolder tabHolder = new TabHolder(parent.getContext());
        tabHolder.setData(position);
        if (tabHolder.mIsSelected)
            mCurrentSelectedHolder = tabHolder;
        mTabViews.add(tabHolder.mTabView);
        return tabHolder.mTabView;
    }

    @Override
    public void tabSelected(View tab) {

    }

    @Override
    public void tabUnselected(View tab) {

    }

    private TabHolder mCurrentSelectedHolder;

    public void onPageSelected(int position) {
        if (mTabViews.isEmpty() || mTabViews.size() <= position || position < 0)
            return;
        TabHolder holder = (TabHolder) mTabViews.get(position).getTag();
        if (mCurrentSelectedHolder != null && mCurrentSelectedHolder.mPosition != position) {
            mCurrentSelectedHolder.mNumTextView.setVisibility(View.INVISIBLE);
        }
        mCurrentSelectedHolder = holder;
        if (holder.mNumTextView.getVisibility() == View.VISIBLE)
            holder.mNumTextView.setVisibility(View.INVISIBLE);
    }

    private class TabHolder {
        public View mTabView;
        public TextView mNumTextView;
        public int mPosition;
        public boolean mIsSelected;

        public TabHolder(Context context) {
            this.mTabView = LayoutInflater.from(context).inflate(R.layout.tab_strip_numlayout, null, false);
            mNumTextView = (TextView) this.mTabView.findViewById(R.id.num);
            this.mTabView.setTag(this);
        }

        public void setData(int position) {
            mPosition = position;
            if (mDatas != null && mDatas.size() > position) {
                QAMsgModel model = mDatas.get(position);
                String numStr = model.getmMsgNum();
                try {
                    int num = Integer.parseInt(numStr);
                    if (num > 99) {
                        mNumTextView.setText(numStr + "+");
                    } else if (num > 0) {
                        mNumTextView.setText(numStr);
                        mNumTextView.setVisibility(View.VISIBLE);
                    }
                    mIsSelected = model.ismIsSelect();
                } catch (Exception e) {
                    e.printStackTrace();
                    mNumTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
