package third.mall.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.xiangha.R;

import java.util.Timer;
import java.util.TimerTask;

import acore.tools.Tools;
import acore.tools.ToolsDevice;

/**
 * 包含两个ScrollView的容器
 */
public class ScrollViewContainer extends RelativeLayout {

    /**
     * 自动上滑
     */
    public static final int AUTO_UP = 0;
    /**
     * 自动下滑
     */
    public static final int AUTO_DOWN = 1;
    /**
     * 动画完成
     */
    public static final int DONE = 2;
    /**
     * 动画速度
     */
    public static final float SPEED = 6.5f;

    private boolean isMeasured = false;

    /**
     * 用于计算手滑动的速度
     */
    private VelocityTracker vt;

    private int mViewHeight;
    private int mViewWidth;

    private View topView;
    private View bottomView;

    private boolean canPullDown;
    private boolean canPullUp;
    private int state = DONE;
    private boolean state_two = false;

    /**
     * 记录当前展示的是哪个view，0是topView，1是bottomView
     */
    private int mCurrentViewIndex = 0;
    /**
     * 手滑动距离，这个是控制布局的主要变量
     */
    private float mMoveLen;
    private MyTimer mTimer;
    private float mLastY;
    /**
     * 用于控制是否变动布局的另一个条件，mEvents==0时布局可以拖拽了，mEvents==-1时可以舍弃将要到来的第一个move事件，
     * 这点是去除多点拖动剧变的关键
     */
    private int mEvents;
    public ScrollviewContaninerInter scrollviewinterface;
    private Context context;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mMoveLen != 0) {
                if (state == AUTO_UP) {
                    mMoveLen -= SPEED;
                    if (mMoveLen <= -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        scrollviewinterface.setState(state);
                        state = DONE;
                        mCurrentViewIndex = 1;
                    }
                } else if (state == AUTO_DOWN) {
                    mMoveLen += SPEED;
                    if (mMoveLen >= 0) {
                        mMoveLen = 0;
                        scrollviewinterface.setState(state);
                        state = DONE;
                        mCurrentViewIndex = 0;
                    }
                } else {
                    mTimer.cancel();
                }
            }else{
                mTimer.cancel();
            }
            requestLayout();
        }
    };

    public void setInterface(ScrollviewContaninerInter scrollviewinterface) {
        this.scrollviewinterface = scrollviewinterface;
    }

    public ScrollViewContainer(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        mTimer = new MyTimer(handler);
    }

    private float xDistance, yDistance, yLast, xLast;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();


                if (vt == null)
                    vt = VelocityTracker.obtain();
                else
                    vt.clear();
                mLastY = ev.getY();
                vt.addMovement(ev);
                mEvents = 0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 多一只手指按下或抬起时舍弃将要到来的第一个事件move，防止多点拖拽的bug
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:

                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                /**
                 * 横向滑动时，不作其他操作，直接由系统处理滑动事件
                 */
                if (xDistance > yDistance) {
                    return super.dispatchTouchEvent(ev);
                }

                vt.addMovement(ev);
                if (canPullUp && mCurrentViewIndex == 0 && mEvents == 0) {
                    mMoveLen += (ev.getY() - mLastY);
                    scrollviewinterface.changeTitleState(true);
                    // 防止上下越界
                    if (mMoveLen > 0) {
                        mMoveLen = 0;
                        mCurrentViewIndex = 0;
                    } else if (mMoveLen < -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        mCurrentViewIndex = 1;

                    }
                    if (mMoveLen < -8) {
                        // 防止事件冲突
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (canPullDown && mCurrentViewIndex == 1 && mEvents == 0) {
                    mMoveLen += (ev.getY() - mLastY);
                    // 防止上下越界
                    if (mMoveLen < -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        mCurrentViewIndex = 1;
                    } else if (mMoveLen > 0) {
                        mMoveLen = 0;
                        mCurrentViewIndex = 0;
                    }
                    if (mMoveLen > 8 - mViewHeight) {
                        // 防止事件冲突
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else
                    mEvents++;
                mLastY = ev.getY();
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                mLastY = ev.getY();
                vt.addMovement(ev);
                vt.computeCurrentVelocity(700);
                // 获取Y方向的速度
                float mYV = vt.getYVelocity();
                if (mMoveLen == 0 || mMoveLen == -mViewHeight)
                    break;
                if (Math.abs(mYV) < 500) {
                    // 速度小于一定值的时候当作静止释放，这时候两个View往哪移动取决于滑动的距离
                    if (mMoveLen <= -mViewHeight / 2) {
                        state = AUTO_UP;
                    } else if (mMoveLen > -mViewHeight / 2) {
                        state = AUTO_DOWN;
                    }
                } else {
                    // 抬起手指时速度方向决定两个View往哪移动
                    if (mYV < 0)
                        state = AUTO_UP;
                    else
                        state = AUTO_DOWN;
                }
                mTimer.schedule(2);
                try {
                    vt.recycle();
                    vt = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            topView.layout(0, (int) mMoveLen, mViewWidth,
                    topView.getMeasuredHeight() + (int) mMoveLen);
            Log.i("zyj", "onLayout: mMoveLen="+mMoveLen);
            bottomView.layout(0, topView.getMeasuredHeight() + (int) mMoveLen,
                    mViewWidth,
                    topView.getMeasuredHeight() + (int) mMoveLen + bottomView.getMeasuredHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            isMeasured = true;

            mViewWidth = getMeasuredWidth();

            topView = getChildAt(0);
            bottomView = getChildAt(1);

            bottomView.setOnTouchListener(bottomViewTouchListener);
            topView.setOnTouchListener(topViewTouchListener);
        }
        mViewHeight = getMeasuredHeight();
    }

    private OnTouchListener topViewTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (state_two) {
                return false;
            }
            ScrollView sv = (ScrollView) v;
            canPullUp = sv.getScrollY() == (sv.getChildAt(0).getMeasuredHeight() - sv
                    .getMeasuredHeight()) && mCurrentViewIndex == 0;
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (sv.getChildAt(0)  != null && sv.getChildAt(0) .getMeasuredHeight() <= sv.getScrollY() + sv.getHeight()) {
                        Log.i("wyl","--到底部了");
                    } else if (sv.getScrollY() == 0) {
                        Log.i("wyl","-到顶部了");
                    }
                    break;
            }
            return false;
        }
    };
    private OnTouchListener bottomViewTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ScrollView sv = (ScrollView) v;
            canPullDown = sv.getScrollY() == 0 && mCurrentViewIndex == 1;
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (sv.getChildAt(0)  != null && sv.getChildAt(0) .getMeasuredHeight() <= sv.getScrollY() + sv.getHeight()) {
                        Log.i("wyl","--到底部了");
                    } else if (sv.getScrollY() == 0) {
                        Log.i("wyl","-到顶部了");
                    }
                    break;
            }
            return false;
        }
    };

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    public interface ScrollviewContaninerInter {
        public abstract void setState(int state);

        public abstract void changeTitleState(boolean state);
    }

    public boolean isState_two() {
        return state_two;
    }

    public void setState_two(boolean state_two) {
        this.state_two = state_two;
    }

    /**
     * 设置展示位置--自动滑动到该位置
     * @param viewIndex
     */
    public void setViewIndex(String viewIndex){
        switch (viewIndex){
            case "1"://滚动到第一视图
                if(0!=mCurrentViewIndex) {
                    if (vt == null) vt = VelocityTracker.obtain();
                    mMoveLen = -ToolsDevice.getWindowPx(context).heightPixels + Tools.getDimen(context,R.dimen.dp_49);
                    state = AUTO_DOWN;
                    mTimer.schedule(1);
                }
                break;
            case "2"://滚动到第二视图
                if(1!=mCurrentViewIndex) {
                    if (vt == null) vt = VelocityTracker.obtain();
                    if(OneViewheight>0){
                        mMoveLen=-OneViewheight-Tools.getDimen(context,R.dimen.dp_49);
                    }else mMoveLen = -1;
                    state = AUTO_UP;
                    mTimer.schedule(1);
                }
                break;
        }
    }

    private int OneViewheight;

    /**
     * 设置当前view的高度
     * @param height
     */
    public void setOneViewHeight(int height){
        this.OneViewheight= height;
    }
}
