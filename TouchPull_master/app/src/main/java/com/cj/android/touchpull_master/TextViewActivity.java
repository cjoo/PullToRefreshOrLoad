package com.cj.android.touchpull_master;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cj.android.touchpull.TouchPullListener;
import com.cj.android.touchpull.TouchPullView;

/**
 * Created by jian.cao on 2016/1/22.
 */
public class TextViewActivity extends Activity implements TouchPullListener {
    private TouchPullView touchPullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);
        touchPullView = (TouchPullView) findViewById(R.id.touchPullView);
        touchPullView.setTouchPullListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            touchPullView.complete();
        }
    };

    @Override
    public void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(Message.obtain());
            }
        }).start();
    }

    @Override
    public void load() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(Message.obtain());
            }
        }).start();
    }
}