package com.cj.android.touchpull;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 支持ListView,GridView,ExpandableListView,ScrollView...
 * pull方向分三步（1:根据子控件筛选出可能方向；2:根据触摸移动距离与SCROLL_PRECISION值比较得到手指滑动方向；3:结合1和2得到最终方向）
 * Created by jian.cao on 2016/1/20.
 */
public class TouchPullView extends RelativeLayout {
    private APullImageView pullImageView;//刷新或加载的动画view
    private List<View> conditionChildViews;//这里放置的控件，用来筛选出可能方向。（1:根据子控件筛选出可能方向）
    private Direction direction = new Direction();//pull方向
    private Direction directionEnable = new Direction(Direction.BOTH_PULL);//该TouchPullView控件支持的方向
    private static final float SCROLL_PRECISION = 5;//触摸距离达到该值时，得到手指滑动方向。(2:根据触摸移动距离与SCROLL_PRECISION值比较得到手指滑动方向)
    private float startY;//儿子控件到达刷新或加载条件时的Y坐标
    private boolean isGetFinalDirection = false;//是否得到了最终方向(3:结合1和2得到最终方向)
    private boolean refreshingOrLoadingOverScrollEnabled = true;//当刷新或加载时子控件能否滚动
    private static final int NONE = 0;//doMainType默认取值
    private static final int REFRESHING = 1;//刷新中
    private static final int LOADING = 2;//加载中
    private static final int COMPLETING = 3;//完成中(刷新加载完成，执行完成动画)
    private static final int CANCELING = 4;//取消中(MotionEvent.ACTION_CANCEL或MotionEvent.ACTION_UP没达到指定距离时执行取消动画)
    private static final int ROLLING = 5;//回滚中（拉拽过远，回滚到指定的位置）
    private int doMainType = NONE;//正在做的事情,可能的值（NONE，REFRESHING，LOADING，COMPLETING，CANCELING，ROLLING）
    private TouchPullListener touchPull;//回调接口对象
    private Timer simulationFingersTimer;//模拟手指触摸定时器
    private long simulationFingersTimeInterval;//前后两次触摸间隔
    private int simulationFingersAllNumber;//一次完整的模拟所需总共触摸次数
    private float simulationFingersStep;//步长
    private static final int SIMULATION_META_STATE = 1;//模拟手指触的MetaState(识别模拟还是真实触摸)
    private boolean simulationFingering = false;//正在模拟触摸中

    public TouchPullView(Context context) {
        super(context);
    }

    public TouchPullView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchPullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 使用该控件时，设置ITouchPull接口实现类对象，就可以处理刷新和加载操作了。
     *
     * @param mTouchPull
     */
    public void setTouchPullListener(TouchPullListener mTouchPull) {
        this.touchPull = mTouchPull;
    }

    /**
     * 设置支持的方向
     *
     * @param type
     */
    public void setDirectionEnable(@Direction.DirectionType int type) {
        this.directionEnable.type = type;
    }

    /**
     * 设置当刷新或加载时子控件能否滚动
     *
     * @param refreshingOrLoadingOverScrollEnabled
     */
    public void setRefreshingOrLoadingOverScrollEnabled(boolean refreshingOrLoadingOverScrollEnabled) {
        this.refreshingOrLoadingOverScrollEnabled = refreshingOrLoadingOverScrollEnabled;
    }

    /**
     * 置为刷新(模拟手指触摸)
     * 建议在activity生命周期onWindowFocusChanged中调用该方法
     */
    public void autoRefresh() {
        printLog("step MOVE_DISTANCE:"+pullImageView.getMoveDistance());
        printLog("step pullImageView.getHeight():"+pullImageView.getHeight());
        float step = (pullImageView.getMoveDistance() + pullImageView.getHeight())
                / (20 - 5f);
        printLog("step:"+step);
        simulationPullDown(0, 20, step, step * 20);
    }

    /**
     * 模拟下拉
     *
     * @param delay
     * @param timeInterval 前后两次触摸间隔
     * @param step         步长
     * @param distance     距离
     */
    public void simulationPullDown(long delay, long timeInterval, float step, float distance) {
        if (simulationFingering) {
            printLog("simulationPullDown return");
            return;
        }
        printLog("simulationPullDown");
        simulationFingering = true;
        if (simulationFingersTimer == null) {
            simulationFingersTimer = new Timer();
        } else {
            simulationFingersTimer.cancel();
            simulationFingersTimer = new Timer();
        }
        simulationFingersTimeInterval = timeInterval;
        simulationFingersStep = step;
        simulationFingersAllNumber = distance % step == 0 ? (int) (distance / step) : (int) (distance / step) + 1;
        simulationFingersTimer.schedule(new TimerTask() {
            private int i = 0;

            @Override
            public void run() {
                i++;
                autoFreshHandler.sendEmptyMessage(i);
            }
        }, delay, simulationFingersTimeInterval);
    }

    //完成模拟手指滑动
    private void completeSimulation() {
        printLog("completeSimulation");
        if (simulationFingersTimer != null) {
            simulationFingersTimer.cancel();
        }
        simulationFingering = false;
    }

    private Handler autoFreshHandler = new Handler() {
        private long downTime;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action;
            if (msg.what == 1) {
                action = MotionEvent.ACTION_DOWN;
                downTime = SystemClock.currentThreadTimeMillis();
            } else if (msg.what < simulationFingersAllNumber) {
                action = MotionEvent.ACTION_MOVE;
            } else {
                action = MotionEvent.ACTION_UP;
            }
            float dy = (msg.what - 1) *simulationFingersStep + getTop();
            float y = pullImageView.getDistancePull(dy, 0);
            MotionEvent motionEvent = MotionEvent.obtain(downTime,
                    SystemClock.currentThreadTimeMillis(),
                    action, getLeft(), y, SIMULATION_META_STATE);
            dispatchTouchEvent(motionEvent);
            if (msg.what >= simulationFingersAllNumber) {
                completeSimulation();
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //保存pullImageView的位置
        int left = pullImageView.getLeftRelativeParentOfParent();
        int top = pullImageView.getTopRelativeParentOfParent();

        super.onLayout(changed, l, t, r, b);

        //置为之前的位置
        pullImageView.setLocation(left, top);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        printLog("onFinishInflate");
        /**
         * WebView extends AbsoluteLayout extends ViewGroup（例如：大部分）
         * WebView extends MockView extends TextView(例如：api =19,22)
         */
        //筛选出子控件
        conditionChildViews = new ArrayList<View>();
        List<ViewGroup> viewGroups = new ArrayList<ViewGroup>();
        viewGroups.add(this);
        while (viewGroups.size() > 0) {
            for (int i = viewGroups.size() - 1; i >= 0; i--) {
                ViewGroup iView = viewGroups.remove(i);
                for (int j = 0; j < iView.getChildCount(); j++) {
                    View jView = iView.getChildAt(j);
                    printLog(jView.getClass().getSimpleName());
                    if (jView instanceof ViewGroup) {
                        if (jView instanceof AbsListView || jView instanceof ScrollView ||
                                jView instanceof WebView) {
                            conditionChildViews.add(jView);
                            continue;
                        }
                        viewGroups.add((ViewGroup) jView);
                        continue;
                    }
                    conditionChildViews.add(jView);
                }
            }
        }
        //添加pullImageView
        pullImageView = getPullView();
        pullImageView.touchPullView = this;
        this.addView(pullImageView, this.getChildCount());

    }

    //获取刷新和加载控件
    protected APullImageView getPullView() {
        return new DefaultPullImageView(getContext());
    }

    //打印日志
    private void printLog(String msg) {
        Log.i("TouchPullView", msg);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        printLog("requestDisallowInterceptTouchEvent:" + disallowIntercept);
        if (disallowIntercept) {//过滤disallowIntercept=true。
            return;
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //模拟触摸时不可以真实触摸
        if (simulationFingering && ev.getMetaState() != SIMULATION_META_STATE) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 子控件为ListView等时，ACTION_MOVE执行几次后this.disallowIntercept=true;
     * 造成的影响：用户体验不好！（如果从中间滑到顶部或底部是不会进入onInterceptTouchEvent方法的，拦截不到事件，不能执行刷新加载相关功能）
     * 解决办法：重写requestDisallowInterceptTouchEvent，过滤disallowIntercept=true。
     * 缺点：这样会导致子控件调用parent.requestDisallowInterceptTouchEvent(true)失效。
     * <p/>
     * ViewGroup类dispatchTouchEvent方法关键代码：
     * final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
     * if (!disallowIntercept) {
     * intercepted = onInterceptTouchEvent(ev);
     * }
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        printLog("parent onInterceptTouchEvent:" + ev.getAction());
        int action = ev.getAction();
        if (doMainType == NONE && action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (doMainType == REFRESHING || doMainType == LOADING) {
            printLog("parent onInterceptTouchEvent result:" + !refreshingOrLoadingOverScrollEnabled);
            return !refreshingOrLoadingOverScrollEnabled;
        }
        printLog("parent onInterceptTouchEvent result:" + isGetFinalDirection);
        if (isGetFinalDirection) {
//            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
        //得到可能的方向
        if (this.direction.type == Direction.NONE &&
                this.directionEnable.type != Direction.NONE) {
            this.startY = ev.getY();
            printLog("startY：" + startY);
            for (View view : conditionChildViews) {
                printLog(view.getClass().getSimpleName());
                if (view instanceof AbsListView) {
                    AbsListView absListView = (AbsListView) view;
                    Direction direction = new Direction();
                    View firstView = absListView.getChildAt(0);
                    View lastView = absListView.getChildAt(absListView.getChildCount() - 1);
                    if (absListView.getFirstVisiblePosition() == 0 && (firstView == null ||
                            firstView.getTop() >= view.getTop())) {
                        printLog("顶部");
                        direction.addDirection(Direction.DOWN_PULL);
                    }
                    if (absListView.getLastVisiblePosition() == (absListView.getCount() - 1) && (lastView == null ||
                            lastView.getBottom() <= view.getBottom())) {
                        printLog("底部");
                        direction.addDirection(Direction.UP_PULL);
                    }
                    if (direction.type == Direction.NONE) {
                        this.direction.type = Direction.NONE;
                        printLog("NONE");
                        break;
                    }
                    this.direction.configDirection(direction.type);
                } else if (view instanceof ScrollView) {
                    ScrollView scrollView = (ScrollView) view;
                    Direction direction = new Direction();
                    int scrollY = view.getScrollY();
                    int height = view.getHeight();
                    int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
                    if (scrollY == 0) {
                        printLog("顶部");
                        direction.addDirection(Direction.DOWN_PULL);
                    }
                    if ((scrollY + height) == scrollViewMeasuredHeight) {
                        printLog("底部");
                        direction.addDirection(Direction.UP_PULL);
                    }
                    if (direction.type == Direction.NONE) {
                        this.direction.type = Direction.NONE;
                        printLog("NONE");
                        break;
                    }
                    this.direction.configDirection(direction.type);
                } else {
                    if (view.getScrollY() == 0) {
                        printLog("顶部");
                        direction.addDirection(Direction.DOWN_PULL);
                    }
                    if (true) {//TODO 哈哈(待修改)
                        printLog("底部");
                        direction.addDirection(Direction.UP_PULL);
                    }
                    if (direction.type == Direction.NONE) {
                        this.direction.type = Direction.NONE;
                        printLog("NONE");
                        break;
                    }
                    this.direction.configDirection(direction.type);
                }
            }
            this.direction.takeIntersection(this.directionEnable);
        }
        //得到最终方向
        else if (this.direction.type != Direction.NONE) {
            findFinalDirection(ev);
        }

        return false;
    }

    //找到最终方向
    private void findFinalDirection(MotionEvent ev) {
        if (this.startY - ev.getY() >= SCROLL_PRECISION) {
            if (this.direction.containsDirection(Direction.UP_PULL)) {
                this.direction.type = Direction.UP_PULL;
                isGetFinalDirection = true;
            } else {
                this.direction.type = Direction.NONE;
            }
        } else if (ev.getY() - this.startY >= SCROLL_PRECISION) {
            if (this.direction.containsDirection(Direction.DOWN_PULL)) {
                this.direction.type = Direction.DOWN_PULL;
                isGetFinalDirection = true;
            } else {
                this.direction.type = Direction.NONE;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        printLog("onTouchEvent:" + event.getAction());
        int action = event.getAction();
        if (doMainType != NONE) {
            return false;
        }
        //支持不处理事件的子控件（如没有设置事件的TextView。。。）
        if (!isGetFinalDirection) {
            if (this.direction.type != Direction.NONE) {
                findFinalDirection(event);
            }
            return true;
        }

        //事件取消
        if (action == MotionEvent.ACTION_CANCEL) {
            startCancelPullAnimation();
            return true;
        }
        //处理多点触控
        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            this.startY -= event.getY(1) - event.getY(0);
        }

        //显示刷新加载视图
        if (pullImageView.getVisibility() != View.VISIBLE) {
            pullImageView.setVisibility(View.VISIBLE);
            pullImageView.setLocation(this.getWidth() / 2 - pullImageView.getWidth() / 2, 0);
        }

        //【pullImageView随手指移动并旋转】
        int locationY = pullImageView.getTopRelativeParentOfParent();
        if (direction.type == Direction.DOWN_PULL) {
            locationY = this.getTop() - pullImageView.getHeight() +
                    pullImageView.getPullDistance(event.getY(), this.startY);
        } else if (direction.type == Direction.UP_PULL) {
            locationY = this.getBottom() +
                    pullImageView.getPullDistance(event.getY(), this.startY);
        }
        printLog("locationY:" + locationY);
        pullImageView.setLocation(pullImageView.getLeft(), locationY);
        pullImageView.setProgressRotation(event.getY(), this.startY);

        //判断是否拉拽到一定距离
        if ((direction.type == Direction.DOWN_PULL && pullImageView.getTopRelativeParentOfParent() - this.getTop() >= pullImageView.getMoveDistance()) ||
                (direction.type == Direction.UP_PULL && this.getBottom() - pullImageView.getBottomRelativeParentOfParent() >= pullImageView.getMoveDistance())) {
            printLog("刷新或加载");
            pullImageView.toReachCriticalValue();//到达刷新加载条件
            if (action == MotionEvent.ACTION_UP) {
                //开始执行刷新加载操作
                startRollToFreshOrLoadLocation();
                return true;
            }
        }
        //没有达到刷新加载条件的拉拽距离
        else {
            pullImageView.disReachCriticalValue();//没有到达刷新加载条件
            if (action == MotionEvent.ACTION_UP) {
                startCancelPullAnimation();
                return true;
            }
        }

        //处理多点触控
        if (action == MotionEvent.ACTION_POINTER_UP) {
            this.startY += event.getY(1) - event.getY(0);
        }
        return true;
    }

    //重置
    private void reset() {
        pullImageView.reset();
        isGetFinalDirection = false;
        direction.type = Direction.NONE;
        doMainType = NONE;
    }

    /**
     * 刷新或加载完成
     */
    public void complete() {
        startRefreshOrLoadCompleteAnimation();
    }

    //开始取消拉拽动画
    private void startCancelPullAnimation() {
        doMainType = CANCELING;
        pullImageView.startCancelPullAnimation(this.direction, refreshOrLoadCompleteAnimationListener);
    }

    //开始刷新加载完成动画
    private void startRefreshOrLoadCompleteAnimation() {
        if (doMainType != REFRESHING && doMainType != LOADING) {
            return;
        }
        doMainType = COMPLETING;
        pullImageView.startRefreshOrLoadCompleteAnimation(this.direction, refreshOrLoadCompleteAnimationListener);
    }

    //监听刷新加载完成的对象
    private TouchPullView.RefreshOrLoadCompleteAnimationListener refreshOrLoadCompleteAnimationListener = new RefreshOrLoadCompleteAnimationListener();

    //监听刷新加载完成的类
    private class RefreshOrLoadCompleteAnimationListener extends MyAnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            reset();
        }
    }

    //开始回滚到刷新加载的位置
    private void startRollToFreshOrLoadLocation() {
        doMainType = ROLLING;
        pullImageView.startRollToFreshOrLoadLocation(this.direction, rollBackAnimationListener);
    }

    //监听回滚动画完成的对象
    private RollBackAnimationListener rollBackAnimationListener = new RollBackAnimationListener();

    //监听回滚动画完成的类
    private class RollBackAnimationListener extends MyAnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {
            //刷新或加载
            if (direction.type == Direction.DOWN_PULL) {
                doMainType = REFRESHING;
                if (touchPull == null) {
                    //调用刷新或加载完成
                    complete();
                } else {
                    pullImageView.startRefreshOrLoadAnimation(direction, null);
                    touchPull.refresh();
                }
            } else if (direction.type == Direction.UP_PULL) {
                doMainType = LOADING;
                if (touchPull == null) {
                    //调用刷新或加载完成
                    complete();
                } else {
                    pullImageView.startRefreshOrLoadAnimation(direction, null);
                    touchPull.load();
                }
            }
        }
    }

    private abstract class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
