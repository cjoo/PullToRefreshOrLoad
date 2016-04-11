package com.cj.android.touchpull2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持ListView,GridView,ExpandableListView,ScrollView...
 * pull方向分三步（1:根据子控件筛选出可能方向；2:根据触摸移动距离与SCROLL_PRECISION值比较得到手指滑动方向；3:结合1和2得到最终方向）
 * Created by jian.cao on 2016/2/2.
 */
public class TouchPull2View extends RelativeLayout {
    private BasePullView pullView;//刷新加载动画控件
    private List<View> conditionChildViews;//这里放置的控件，用来筛选出可能方向。（1:根据子控件筛选出可能方向）
    private Direction direction = new Direction();//pull方向
    private Direction directionEnable = new Direction(Direction.BOTH_PULL);//该TouchPullView控件支持的方向
    private static final float SCROLL_PRECISION = 5;//触摸距离达到该值时，得到手指滑动方向。(2:根据触摸移动距离与SCROLL_PRECISION值比较得到手指滑动方向)
    private float startY;//儿子控件到达刷新或加载条件时的Y坐标
    private boolean isGetFinalDirection = false;//是否得到了最终方向(3:结合1和2得到最终方向)
    private boolean refreshingOrLoadingOverScrollEnabled = true;//当刷新或加载时子控件能否滚动
    private View downPullView, pullAbleView, upPullView;//在onLayout中对这三个控件重新layout
    public int pullY;//拉动距离（onLayout中使用）

    public TouchPull2View(Context context) {
        super(context);
        init();
    }

    public TouchPull2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPull2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TouchPull2View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

    }

    /**
     * 置为刷新状态
     */
    public void autoFresh() {
        pullView.autoFresh();
    }

    /**
     * 使用该控件时，设置ITouchPull接口实现类对象，就可以处理刷新和加载操作了。
     *
     * @param mTouchPull
     */
    public void setTouchPullListener(TouchPullListener mTouchPull) {
        pullView.setTouchPullListener(mTouchPull);
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

    public void complete() {
        pullView.complete();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        printLog("onFinishInflate");
        if (getChildCount() != 1) {
            throw new RuntimeException("直接子控件数量不等于1！");
        }
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

        pullView = getPullView();
        pullAbleView = getChildAt(0);
        //添加下拉view
        this.addView(downPullView = pullView.getDownPullView(), 0,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //添加上拉view
        this.addView(upPullView = pullView.getUpPullView(), this.getChildCount(),
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onDetachedFromWindow() {
        pullView.destroy();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //实现布局在动的效果
        downPullView.layout(0, pullY - downPullView.getMeasuredHeight(), downPullView.getMeasuredWidth(), pullY);
        pullAbleView.layout(0, pullY, pullAbleView.getMeasuredWidth(), pullY + pullAbleView.getMeasuredHeight());
        upPullView.layout(0, pullY + pullAbleView.getMeasuredHeight(), upPullView.getMeasuredWidth(), pullY + pullAbleView.getMeasuredHeight() + upPullView.getMeasuredHeight());
    }

    //获取IPullView
    protected BasePullView getPullView() {
        return new DefaultPullView(getContext(), this);
    }

    //打印日志
    private void printLog(String msg) {
        Log.i("TouchPull2View", msg);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        printLog("requestDisallowInterceptTouchEvent:" + disallowIntercept);
        if (disallowIntercept) {//过滤disallowIntercept=true。
            return;
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
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
        if (pullView.doMainType == pullView.NONE && action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (pullView.doMainType == pullView.REFRESHING || pullView.doMainType == pullView.LOADING) {
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
                    if (absListView.getFirstVisiblePosition() == 0 &&
                            firstView.getTop() >= view.getTop()) {
                        printLog("顶部");
                        direction.addDirection(Direction.DOWN_PULL);
                    }
                    if (absListView.getLastVisiblePosition() == (absListView.getCount() - 1) &&
                            lastView.getBottom() <= view.getBottom()) {
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
                pullView.start(this.direction, startY);
            } else {
                this.direction.type = Direction.NONE;
            }
        } else if (ev.getY() - this.startY >= SCROLL_PRECISION) {
            if (this.direction.containsDirection(Direction.DOWN_PULL)) {
                this.direction.type = Direction.DOWN_PULL;
                isGetFinalDirection = true;
                pullView.start(this.direction, startY);
            } else {
                this.direction.type = Direction.NONE;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        printLog("onTouchEvent:" + event.getAction());
        int action = event.getAction();
        if (pullView.doMainType != pullView.NONE) {
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
            pullView.cancel();
            return true;
        }
        //手指放开
        else if (action == MotionEvent.ACTION_UP) {
            pullView.motionUp();
            return true;
        }
        //处理多点触控
        else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            this.startY -= event.getY(1) - event.getY(0);
            pullView.setStartY(this.startY);
        }

        //【pullImageView随手指移动】
        pullView.pull(event.getY());

        //处理多点触控
        if (action == MotionEvent.ACTION_POINTER_UP) {
            this.startY += event.getY(1) - event.getY(0);
            pullView.setStartY(this.startY);
        }
        return true;
    }

    //重置
    private void reset() {
        pullView.reset();
        isGetFinalDirection = false;
        direction.type = Direction.NONE;
    }
}
