package com.cj.android.touchpull_master.CJ;

import android.content.Context;
import android.util.AttributeSet;

import com.cj.android.touchpull.APullImageView;
import com.cj.android.touchpull.TouchPullView;

/**
 * Created by jian.cao on 2016/1/29.
 */
public class MyTouchPullView extends TouchPullView {
    public MyTouchPullView(Context context) {
        super(context);
    }

    public MyTouchPullView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTouchPullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected APullImageView getPullView() {
        return new MyPullImageView(getContext());
    }
}
