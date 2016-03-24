package com.cj.android.touchpull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/**
 * 刷新或加载的动画view
 * Created by jian.cao on 2016/1/28.
 */
public abstract class APullImageView extends ImageView {
    public TouchPullView touchPullView;//父控件

    public APullImageView(Context context) {
        super(context);
    }

    public APullImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public APullImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init() {
        setVisibility(INVISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //当pullImageView移动距离达到该值+this.getHeight()时，达到临界值。
    public int getMoveDistance() {
        return this.getHeight();
    }

    /**
     * 达到临界值了（就是放开可以刷新或加载了）
     */
    public abstract void toReachCriticalValue();

    /**
     * 没有达到临界值时
     */
    public abstract void disReachCriticalValue();

    /**
     * 开始刷新加载动画
     */
    public abstract void startRefreshOrLoadAnimation(Direction direction, AnimationListener animationListener);

    /**
     * 开始刷新加载完成动画
     */
    public abstract void startRefreshOrLoadCompleteAnimation(Direction direction, AnimationListener animationListener);

    /**
     * 开始取消拉拽动画
     */
    public abstract void startCancelPullAnimation(Direction direction, AnimationListener animationListener);

    /**
     * 开始回滚到刷新加载的位置动画
     */
    public abstract void startRollToFreshOrLoadLocation(Direction direction, AnimationListener animationListener);

    /**
     * 拉拽时，该控件随手指移动旋转
     *
     * @param currentY 当前触摸Y坐标
     * @param startY   到达刷新或加载条件时的触摸Y坐标
     */
    public abstract void setProgressRotation(float currentY, float startY);

    /**
     * 重置
     */
    public abstract void reset();

    /**
     * 设置位置
     *
     * @param left
     * @param top
     */
    public final void setLocation(int left, int top) {
        this.setFrame(left, top - touchPullView.getTop(), left + this.getWidth(), top + this.getHeight() - touchPullView.getTop());
    }

    /**
     * 该控件顶部相对于TouchPullView顶部的距离
     *
     * @return
     */
    public final int getTopRelativeParentOfParent() {
        return getTop() + touchPullView.getTop();
    }

    /**
     * 该控件底部相对于TouchPullView顶部的距离
     *
     * @return
     */
    public final int getBottomRelativeParentOfParent() {
        return getBottom() + touchPullView.getTop();
    }

    /**
     * 该控件顶部相对于TouchPullView顶部的距离
     *
     * @return
     */
    public final int getLeftRelativeParentOfParent() {
        return getLeft() + touchPullView.getLeft();
    }

    /**
     * 触摸距离转换为拉拽距离
     *
     * @param currentY 当前触摸Y坐标
     * @param startY   到达刷新或加载条件时的触摸Y坐标
     * @return
     */
    public int getPullDistance(float currentY, float startY) {
        return (int) (currentY - startY) / 3;
    }

    /**
     * 拉拽距离转换为触摸距离
     *
     * @param currentY 当前触摸Y坐标
     * @param startY   到达刷新或加载条件时的触摸Y坐标
     * @return
     */
    public int getDistancePull(float currentY, float startY) {
        return (int) (currentY - startY) * 3;
    }
}
