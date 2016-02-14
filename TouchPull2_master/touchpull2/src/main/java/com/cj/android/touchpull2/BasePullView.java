package com.cj.android.touchpull2;

import android.content.Context;
import android.view.View;

/**
 * Created by jian.cao on 2016/2/2.
 */
public abstract class BasePullView {
    protected float startY;//开始处理事件的触摸坐标
    protected Context context;
    protected TouchPull2View touchPull2View;
    protected TouchPullListener touchPull;//回调接口对象
    public static final int NONE = 0;//doMainType默认取值
    public static final int REFRESHING = 1;//刷新中
    public static final int LOADING = 2;//加载中
    public static final int COMPLETING = 3;//完成中(刷新加载完成，执行完成动画)
    public static final int CANCELING = 4;//取消中(MotionEvent.ACTION_CANCEL或MotionEvent.ACTION_UP没达到指定距离时执行取消动画)
    public static final int ROLLING = 5;//回滚中（拉拽过远，回滚到指定的位置）
    public int doMainType = NONE;//正在做的事情,可能的值（NONE，REFRESHING，LOADING，COMPLETING，CANCELING，ROLLING）

    public BasePullView(Context context, TouchPull2View touchPull2View) {
        this.context = context;
        this.touchPull2View = touchPull2View;
    }

    public void setTouchPullListener(TouchPullListener mTouchPull) {
        this.touchPull = mTouchPull;
    }

    /**
     * 重置
     */
    abstract void reset();

    public void setStartY(float startY) {
        this.startY = startY;
    }

    /**
     * 开始了哦
     *
     * @param direction
     * @param startY
     */
    abstract void start(Direction direction, float startY);

    /**
     * 拉拽
     *
     * @param currentY
     */
    abstract void pull(float currentY);

    /**
     * 取消
     */
    abstract void cancel();

    /**
     * 手指放开
     */
    abstract void motionUp();

    /**
     * 刷新或加载完成
     */
    abstract void complete();

    /**
     * 获取下拉view
     *
     * @return
     */
    abstract View getDownPullView();

    /**
     * 获取上拉view
     *
     * @return
     */
    abstract View getUpPullView();
}
