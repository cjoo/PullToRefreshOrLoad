package com.cj.android.touchpull_master;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cj.android.touchpull.TouchPullListener;
import com.cj.android.touchpull.TouchPullView;

/**
 * Created by jian.cao on 2016/1/22.
 */
public class GridViewActivity extends Activity implements TouchPullListener {
    private GridView gridView;
    private TouchPullView touchPullView;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);
        touchPullView = (TouchPullView) findViewById(R.id.touchPullView);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(myAdapter = new MyAdapter());
        touchPullView.setTouchPullListener(this);
//        touchPullView.setDirectionEnable(TouchPullView.Direction.DOWN_PULL);
    }
    public void simulationPullDown(View view) {
        touchPullView.autoRefresh();
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
            myAdapter.count = msg.what == 1 ? 20 : myAdapter.count + 20;
            myAdapter.notifyDataSetChanged();
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

    class MyAdapter extends BaseAdapter {
        public int count = 0;

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
                convertView = textView = new TextView(GridViewActivity.this);
            } else {
                textView = (TextView) convertView;
            }
            textView.setText("gridView item:" + position);
            return convertView;
        }
    }
}
