package com.cj.android.touchpull2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by jian.cao on 2016/2/2.
 */
public class DefaultPullView extends BasePullView {
    private View upPullView, downPullView;
    private Direction direction;

    public DefaultPullView(Context context, TouchPull2View touchPull2View) {
        super(context, touchPull2View);

    }

    //和myTimer一起实现回滚和取消动画
    private Handler handler = new Handler() {
        private float lastCurrentY;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CANCELING:
                    touchPull2View.pullY -= (float) msg.obj * (1 + Math.abs(touchPull2View.pullY) / 180f);
                    if ((touchPull2View.pullY <= 0 && direction.type == Direction.DOWN_PULL)
                            || (touchPull2View.pullY >= 0 && direction.type == Direction.UP_PULL)) {
                        touchPull2View.pullY = 0;
                        reset();
                    }
                    touchPull2View.requestLayout();
                    break;
                case ROLLING:
                    touchPull2View.pullY -= (float) msg.obj * (1 + Math.abs(touchPull2View.pullY) / 180f);
                    if (touchPull2View.pullY <= downPullView.getMeasuredHeight() && direction.type == Direction.DOWN_PULL) {
                        myTimer.cancel();
                        touchPull2View.pullY = downPullView.getMeasuredHeight();
                        doMainType = REFRESHING;
                        startRefreshAnimation();
                        touchPull.refresh();
                    } else if (touchPull2View.pullY >= -upPullView.getMeasuredHeight() && direction.type == Direction.UP_PULL) {
                        myTimer.cancel();
                        touchPull2View.pullY = -upPullView.getMeasuredHeight();
                        doMainType = LOADING;
                        startLoadAnimation();
                        touchPull.load();
                    }
                    touchPull2View.requestLayout();
                    break;
                case AUTO_FRESH:
                    int height = downPullView.getMeasuredHeight();
                    if (height > 0) {
                        if (touchPull2View.pullY >= height) {
                            myTimer.cancel();
                            touchPull2View.pullY = height;
                            motionUp();
                        } else {
                            pull(lastCurrentY += height / 10f);
                        }
                        touchPull2View.requestLayout();
                    }
                    break;
            }
        }
    };
    private MyTimer myTimer = new MyTimer(handler);//和handler一起实现回滚和取消动画

    private ObjectAnimator defaultRefreshAnimation;

    private void startRefreshAnimation() {
        if (defaultRefreshAnimation == null) {
            defaultRefreshAnimation = ObjectAnimator.ofFloat(downPullView, "rotation", 1f + downPullView.getRotation(), 360f + downPullView.getRotation());
            defaultRefreshAnimation.setRepeatCount(ValueAnimator.INFINITE);
            defaultRefreshAnimation.setInterpolator(new LinearInterpolator());
            defaultRefreshAnimation.setDuration(600);
            defaultRefreshAnimation.start();
        }
    }

    private void stopRefreshAnimation() {
        if (defaultRefreshAnimation != null) {
            defaultRefreshAnimation.setRepeatCount(0);
            defaultRefreshAnimation = null;
        }
    }

    private ObjectAnimator defaultLoadingAnimation;

    private void startLoadAnimation() {
        if (defaultLoadingAnimation == null) {
            defaultLoadingAnimation = ObjectAnimator.ofFloat(upPullView, "rotation", 1f + upPullView.getRotation(), 360f + upPullView.getRotation());
            defaultLoadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
            defaultLoadingAnimation.setInterpolator(new LinearInterpolator());
            defaultLoadingAnimation.setDuration(600);
            defaultLoadingAnimation.start();
        }
    }

    private void stopLoadAnimation() {
        if (defaultLoadingAnimation != null) {
            defaultLoadingAnimation.setRepeatCount(0);
            defaultLoadingAnimation = null;
        }
    }

    @Override
    public void reset() {
        myTimer.cancel();
        doMainType = NONE;
        stopRefreshAnimation();
        stopLoadAnimation();
        resetScale(upPullView);
        resetScale(downPullView);
        touchPull2View.pullY = 0;
        touchPull2View.requestLayout();
    }

    private ObjectAnimator resetScaleXAnimator, resetScaleYAnimator;
    private AnimatorSet resetAnimatorSet;

    private void resetScale(View view) {
        if (resetScaleXAnimator == null) {
            resetScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        } else {
            resetScaleXAnimator.setTarget(view);
        }
        if (resetScaleYAnimator == null) {
            resetScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        } else {
            resetScaleYAnimator.setTarget(view);
        }
        if (resetAnimatorSet == null) {
            resetAnimatorSet = new AnimatorSet();
            resetAnimatorSet.playTogether(resetScaleXAnimator, resetScaleYAnimator);
            resetAnimatorSet.setDuration(0);
        }
        resetAnimatorSet.start();
    }

    @Override
    public void start(Direction direction, float startY) {
        this.direction = direction;
        this.startY = startY;
    }

    @Override
    public void pull(float currentY) {
        boolean rotation = false;
        touchPull2View.pullY = (int) (currentY - startY) / 3;
        if (touchPull2View.pullY >= downPullView.getMeasuredHeight()
                && direction.type == Direction.DOWN_PULL) {
            startRefreshAnimation();
        } else if (touchPull2View.pullY <= -upPullView.getMeasuredHeight()
                && direction.type == Direction.UP_PULL) {
            startLoadAnimation();
        } else {
            stopRefreshAnimation();
            stopLoadAnimation();
            rotation = true;
        }
        if (direction.type == Direction.DOWN_PULL) {
            if (touchPull2View.pullY <= 0) {
                touchPull2View.pullY = 0;
            }
            if (rotation) {
                downPullView.setRotation(currentY - startY);
            }
        } else if (direction.type == Direction.UP_PULL) {
            if (touchPull2View.pullY >= 0) {
                touchPull2View.pullY = 0;
            }
            if (rotation) {
                upPullView.setRotation(currentY - startY);
            }
        }
        touchPull2View.requestLayout();
    }

    @Override
    public void autoFresh() {
        doMainType = AUTO_FRESH;
        start(new Direction(Direction.DOWN_PULL), 0);
        myTimer.schedule(AUTO_FRESH, 0);//开始循环发送消息给handler
    }

    @Override
    public void cancel() {
        doMainType = CANCELING;
        myTimer.schedule(CANCELING, touchPull2View.pullY / 10f);//开始循环发送消息给handler
    }

    @Override
    public void motionUp() {
        if ((touchPull2View.pullY >= downPullView.getMeasuredHeight() && direction.type == Direction.DOWN_PULL) ||
                (touchPull2View.pullY <= -upPullView.getMeasuredHeight() && direction.type == Direction.UP_PULL)) {
            if (touchPull == null) {
                cancel();
            } else {
                Object obj = null;
                if (direction.type == Direction.DOWN_PULL) {
                    obj = (touchPull2View.pullY - downPullView.getMeasuredHeight()) / 10f;
                } else if (direction.type == Direction.UP_PULL) {
                    obj = (touchPull2View.pullY + upPullView.getMeasuredHeight()) / 10f;
                }
                doMainType = ROLLING;
                myTimer.schedule(ROLLING, obj);//开始循环发送消息给handler
            }
        } else {
            cancel();
        }
    }

    private ObjectAnimator completeScaleXAnimator, completeScaleYAnimator;
    private AnimatorSet completeAnimatorSet;

    @Override
    public void complete() {
        doMainType = COMPLETING;
        View view = upPullView;
        if (direction.type == Direction.DOWN_PULL) {
            view = downPullView;
        }
        if (completeScaleXAnimator == null) {
            completeScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0f);
        } else {
            completeScaleXAnimator.setTarget(view);
        }
        if (completeScaleYAnimator == null) {
            completeScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0f);
        } else {
            completeScaleYAnimator.setTarget(view);
        }
        if (completeAnimatorSet == null) {
            completeAnimatorSet = new AnimatorSet();
            completeAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cancel();
                }
            });
            completeAnimatorSet.setInterpolator(new LinearInterpolator());
            completeAnimatorSet.playTogether(completeScaleXAnimator, completeScaleYAnimator);
            completeAnimatorSet.setDuration(400);
        }
        completeAnimatorSet.start();
    }

    @Override
    public View getDownPullView() {
        downPullView = LayoutInflater.from(context).inflate(R.layout.view_downpull, null);
        return downPullView;
    }

    @Override
    public View getUpPullView() {
        upPullView = LayoutInflater.from(context).inflate(R.layout.view_uppull, null);
        return upPullView;
    }

    @Override
    public void destroy() {
        myTimer.destroy();
        stopRefreshAnimation();
        stopLoadAnimation();
        doMainType = NONE;
    }

}
