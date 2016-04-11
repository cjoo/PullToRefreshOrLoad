package com.cj.android.touchpull2_master;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.android.touchpull2.TouchPull2View;
import com.cj.android.touchpull2.TouchPullListener;

/**
 * Created by maesinfo-024 on 2016/2/2.
 */
public class ListViewActivity extends Activity implements TouchPullListener {
    private ListView listView;
    private MyAdapter myAdapter;
    private TouchPull2View touchPull2View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter = new MyAdapter(this));

        touchPull2View = (TouchPull2View) findViewById(R.id.touchPull2View);
        touchPull2View.setTouchPullListener(this);
        touchPull2View.autoFresh();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            touchPull2View.complete();
            if (msg.what == 1) {
                myAdapter.count = 20;
            } else {
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
                handler.sendEmptyMessage(1);
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
                handler.sendEmptyMessage(2);
            }
        }).start();
    }


    public static class MyAdapter extends BaseAdapter {
        private Context context;
        public int count = 20;

        public MyAdapter(Context context) {
            this.context = context;
        }

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
                convertView = textView = new TextView(context);
            } else {
                textView = (TextView) convertView;
            }
            textView.setText("listView item:" + position);
            return convertView;
        }
    }
}
