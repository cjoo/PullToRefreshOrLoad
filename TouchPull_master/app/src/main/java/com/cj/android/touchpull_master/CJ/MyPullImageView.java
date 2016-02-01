package com.cj.android.touchpull_master.CJ;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.cj.android.touchpull.DefaultPullImageView;
import com.cj.android.touchpull.Direction;

/**
 * Created by jian.cao on 2016/1/21.
 */
public class MyPullImageView extends DefaultPullImageView {
    private static final String TAG = "MoveImageView";
    private MaterialProgressDrawable mProgress;

    public MyPullImageView(Context context) {
        super(context);
    }

    public MyPullImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPullImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        this.setVisibility(View.INVISIBLE);
        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(0xFFFAFAFA);
        mProgress.setAlpha(255);
        mProgress.setArrowScale(1f);
        this.setBackgroundColor(0xFFFAFAFA);
        this.setImageDrawable(mProgress);
        initCircle(0xFFFAFAFA, 20);
    }

    @Override
    public void setProgressRotation(float currentY, float startY) {
//        super.setProgressRotation(currentY, startY);
        Log.i(TAG, "currentY:" + currentY + " startY:" + startY);
        mProgress.showArrow(true);
        float endAngle = (float) Math.abs(getPullDistance(currentY, startY)) / (MOVE_DISTANCE + this.getHeight());
        mProgress.setStartEndTrim(0, Math.min(endAngle, 0.8f));
        if (endAngle >= 0.8f) {
            mProgress.setProgressRotation((endAngle - 0.8f) * 2);
        }
    }

    @Override
    public void startRefreshOrLoadAnimation(Direction direction, Animation.AnimationListener animationListener) {
        mProgress.showArrow(false);
        mProgress.setStartEndTrim(0, 0);
        mProgress.start();
    }

    @Override
    public void reset() {
        mProgress.stop();
        this.setVisibility(View.INVISIBLE);
    }

    //******************************Circle*********************************/
    private void initCircle(int color, final float radius) {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int diameter = (int) (radius * density * 2);
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            OvalShape oval = new OvalShadow(mShadowRadius, diameter);
            circle = new ShapeDrawable(oval);
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(color);
        setBackgroundDrawable(circle);
    }

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;

    private int mShadowRadius;

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                    + mShadowRadius * 2);
        }
        MOVE_DISTANCE = 50;
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(getContext().getResources().getColor(colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;
        private int mCircleDiameter;

        public OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            mCircleDiameter = circleDiameter;
            mRadialGradient = new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2,
                    mShadowRadius, new int[]{
                    FILL_SHADOW_COLOR, Color.TRANSPARENT
            }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = MyPullImageView.this.getWidth();
            final int viewHeight = MyPullImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                    mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }
}
