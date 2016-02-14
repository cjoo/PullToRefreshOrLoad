package com.cj.android.touchpull2;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 循环发送Message消息
 * Created by jian.cao on 2016/2/14.
 */
public class MyTimer {
    private Handler handler;
    private Timer timer;
    private MyTask mTask;


    public MyTimer(Handler handler) {
        this.handler = handler;
        timer = new Timer();
    }

    public void schedule(int what, Object obj) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTask(what, obj, handler);
        timer.schedule(mTask, 0, 15);
    }

    public void schedule(int what, Object obj, long period) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTask(what, obj, handler);
        timer.schedule(mTask, 0, period);
    }

    public void cancel() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    private class MyTask extends TimerTask {
        private Handler handler;
        private int what;
        private Object obj;

        public MyTask(int what, Object obj, Handler handler) {
            this.what = what;
            this.obj = obj;
            this.handler = handler;
        }

        @Override
        public void run() {
            Message message = Message.obtain();
            message.what = what;
            message.obj = obj;
            handler.sendMessage(message);
        }

    }
}
