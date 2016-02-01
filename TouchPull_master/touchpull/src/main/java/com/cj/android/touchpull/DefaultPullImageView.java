package com.cj.android.touchpull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by jian.cao on 2016/1/28.
 */
public class DefaultPullImageView extends APullImageView {
    public DefaultPullImageView(Context context) {
        super(context);
        init();
    }

    public DefaultPullImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultPullImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void toReachCriticalValue() {
        this.setAlpha(1f);
    }

    @Override
    public void disReachCriticalValue() {
        this.setAlpha(0.6f);
    }

    public void init() {
        setImageResource(R.drawable.ic_loading);
        this.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);// TODO setRotation在WebView下出现闪烁（api=19或22）
    }

    @Override
    public void startRefreshOrLoadAnimation(Direction direction, AnimationListener animationListener) {
        startAnimation(getDefaultRefreshingOrLoadingAnimation());
    }

    private Animation defaultRefreshingOrLoadingAnimation;//默认的刷新或加载时动画

    //返回默认的刷新或加载时动画
    private Animation getDefaultRefreshingOrLoadingAnimation() {
        if (defaultRefreshingOrLoadingAnimation == null) {
            defaultRefreshingOrLoadingAnimation = new RotateAnimation(0f, 360f, 0.5f * this.getWidth(), 0.5f * this.getHeight());
            defaultRefreshingOrLoadingAnimation.setDuration(600);
            defaultRefreshingOrLoadingAnimation.setFillAfter(true);
            defaultRefreshingOrLoadingAnimation.setRepeatCount(Animation.INFINITE);
        }
        return defaultRefreshingOrLoadingAnimation;
    }

    @Override
    public void startRefreshOrLoadCompleteAnimation(Direction direction, AnimationListener animationListener) {
        Animation animation = getDefaultRefreshOrLoadCompleteAnimation();
        animation.setAnimationListener(animationListener);
        startAnimation(animation);
    }

    private ScaleAnimation defaultRefreshOrLoadCompleteAnimation;//默认的刷新加载完成动画

    //获取默认的刷新加载完成动画
    private Animation getDefaultRefreshOrLoadCompleteAnimation() {
        if (defaultRefreshOrLoadCompleteAnimation == null) {
            defaultRefreshOrLoadCompleteAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    this.getWidth() / 2, this.getHeight() / 2);
            defaultRefreshOrLoadCompleteAnimation.setDuration(400);
        }
        return defaultRefreshOrLoadCompleteAnimation;
    }

    @Override
    public void startCancelPullAnimation(Direction direction, AnimationListener animationListener) {
        //计算出toYValue
        float toYValue = 0;
        if (direction.type == Direction.DOWN_PULL) {
            if (this.getBottomRelativeParentOfParent() <= touchPullView.getTop()) {
                animationListener.onAnimationEnd(null);
                return;
            }
            toYValue = -((float) this.getTopRelativeParentOfParent() - touchPullView.getTop()) / this.getHeight() - 1;
        } else if (direction.type == Direction.UP_PULL) {
            if (this.getTopRelativeParentOfParent() >= touchPullView.getBottom()) {
                animationListener.onAnimationEnd(null);
                return;
            }
            toYValue = ((float) touchPullView.getBottom() - this.getTopRelativeParentOfParent()) / this.getHeight();
        }
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toYValue);
        translate.setDuration(100);
        //pullImageView不在显示范围时，不会进入onAnimationEnd方法
        translate.setAnimationListener(animationListener);
        startAnimation(translate);
    }

    @Override
    public void startRollToFreshOrLoadLocation(Direction direction, AnimationListener animationListener) {
        RollToFreshOrLoadLocationAnimationListener rollBackAnimationListener = new RollToFreshOrLoadLocationAnimationListener();
        rollBackAnimationListener.animationListener = animationListener;
        //计算toYValue
        float toYValue = 0;
        if (direction.type == Direction.DOWN_PULL) {
            rollBackAnimationListener.pullViewAnimationEndLocationTop = (float) touchPullView.getTop() + MOVE_DISTANCE;
            toYValue = -((float) this.getTopRelativeParentOfParent() -  touchPullView.getTop() - MOVE_DISTANCE) /  this.getHeight();
        } else if (direction.type == Direction.UP_PULL) {
            rollBackAnimationListener.pullViewAnimationEndLocationTop = (float) touchPullView.getBottom() - MOVE_DISTANCE -  this.getHeight();
            toYValue = ((float) touchPullView.getBottom() -  this.getBottomRelativeParentOfParent() - MOVE_DISTANCE) /  this.getHeight();
        }

        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toYValue);
        translate.setDuration(100);
        translate.setFillAfter(true);
        translate.setAnimationListener(rollBackAnimationListener);

        this.startAnimation(translate);
    }

    @Override
    public void setProgressRotation(float currentY, float startY) {
        super.setRotation(currentY - startY);
    }

    class RollToFreshOrLoadLocationAnimationListener implements AnimationListener {
        public float pullViewAnimationEndLocationTop;
        public AnimationListener animationListener;

        @Override
        public void onAnimationStart(Animation animation) {
            animationListener.onAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setLocation(getLeft(), (int) pullViewAnimationEndLocationTop);
            animationListener.onAnimationEnd(animation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            animationListener.onAnimationRepeat(animation);
        }
    }

    @Override
    public void reset() {
        clearAnimation();
        this.setVisibility(View.INVISIBLE);
    }
}
