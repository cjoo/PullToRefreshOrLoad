package com.cj.android.touchpull_master.CJ;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.android.touchpull.TouchPullListener;
import com.cj.android.touchpull.TouchPullView;
import com.cj.android.touchpull_master.R;

/**
 * Created by jian.cao on 2016/1/25.
 */
public class CJActivity extends Activity implements TouchPullListener {
    private ListView listView;
    private MyAdapter myAdapter;
    private TouchPullView touchPullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter=new MyAdapter());
        touchPullView = (TouchPullView) findViewById(R.id.touchPullView);
        touchPullView.setTouchPullListener(this);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        touchPullView.autoRefresh();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            touchPullView.complete();
            if (msg.what == 1) {
                myAdapter.count = 20;
            }else{
                myAdapter.count += 20;
            }
            myAdapter.notifyDataSetChanged();
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
                Message message = Message.obtain();
                message.what = 1;
                handler.sendMessage(message);
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
                Message message = Message.obtain();
                message.what = 2;
                handler.sendMessage(message);
            }
        }).start();
    }

    class MyAdapter extends BaseAdapter {
        public int count = 20;
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                convertView = textView = new TextView(CJActivity.this);
            } else {
                textView = (TextView) convertView;
            }
            textView.setText("listView item:" + position);
            return convertView;
        }
    }
}
