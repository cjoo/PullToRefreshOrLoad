package com.cj.android.touchpull2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

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
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CANCELING:
                    touchPull2View.pullY -= (float) msg.obj;
                    if ((touchPull2View.pullY <= 0 && direction.type == Direction.DOWN_PULL)
                            || (touchPull2View.pullY >= 0 && direction.type == Direction.UP_PULL)) {
                        touchPull2View.pullY = 0;
                        reset();
                    }
                    touchPull2View.requestLayout();
                    break;
                case ROLLING:
                    touchPull2View.pullY -= (float) msg.obj;
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
            }
        }
    };
    private MyTimer myTimer = new MyTimer(handler);//和handler一起实现回滚和取消动画

    private RotateAnimation defaultRefreshingAnimation;

    private void startRefreshAnimation() {
        if (defaultRefreshingAnimation == null) {
            defaultRefreshingAnimation = new RotateAnimation(0f, 360f, 0.5f * downPullView.getWidth(), 0.5f * downPullView.getHeight());
            defaultRefreshingAnimation.setDuration(600);
            defaultRefreshingAnimation.setFillAfter(true);
            defaultRefreshingAnimation.setRepeatCount(Animation.INFINITE);
            LinearInterpolator lir = new LinearInterpolator();
            defaultRefreshingAnimation.setInterpolator(lir);
        } else {

        }
        downPullView.startAnimation(defaultRefreshingAnimation);
    }

    private RotateAnimation defaultLoadingAnimation;

    private void startLoadAnimation() {
        if (defaultLoadingAnimation == null) {
            defaultLoadingAnimation = new RotateAnimation(0f, 360f, 0.5f * upPullView.getWidth(), 0.5f * upPullView.getHeight());
            defaultLoadingAnimation.setDuration(600);
            defaultLoadingAnimation.setFillAfter(true);
            defaultLoadingAnimation.setRepeatCount(Animation.INFINITE);
            LinearInterpolator lir = new LinearInterpolator();
            defaultLoadingAnimation.setInterpolator(lir);
        } else {

        }
        upPullView.startAnimation(defaultLoadingAnimation);
    }

    @Override
    public void reset() {
        myTimer.cancel();
        doMainType = NONE;
        upPullView.clearAnimation();
        downPullView.clearAnimation();
        touchPull2View.pullY = 0;
        touchPull2View.requestLayout();
    }

    @Override
    public void start(Direction direction, float startY) {
        this.direction = direction;
        this.startY = startY;
    }

    @Override
    public void pull(float currentY) {
        touchPull2View.pullY = (int) (currentY - startY) / 3;
        if (touchPull2View.pullY >= downPullView.getMeasuredHeight()
                && direction.type == Direction.DOWN_PULL) {
            //TODO 达到下拉临界值
        } else if (touchPull2View.pullY <= -upPullView.getMeasuredHeight()
                && direction.type == Direction.UP_PULL) {
            //TODO 达到上拉临界值
        } else {
            //TODO 没达到临界值
        }
        if (direction.type == Direction.DOWN_PULL) {
            if (touchPull2View.pullY <= 0) {
                touchPull2View.pullY = 0;
            }
            downPullView.setRotation(currentY - startY);
        } else if (direction.type == Direction.UP_PULL) {
            if (touchPull2View.pullY >= 0) {
                touchPull2View.pullY = 0;
            }
            upPullView.setRotation(currentY - startY);
        }
        touchPull2View.requestLayout();
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

    @Override
    public void complete() {
        cancel();
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

}
