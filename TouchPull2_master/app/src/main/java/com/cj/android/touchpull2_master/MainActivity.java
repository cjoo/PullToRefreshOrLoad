package com.cj.android.touchpull2_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by maesinfo-024 on 2016/2/2.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        (findViewById(R.id.btn_listView)).setOnClickListener(this);
        (findViewById(R.id.btn_scrollView)).setOnClickListener(this);
        (findViewById(R.id.btn_gridView)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_listView:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
            case R.id.btn_scrollView:
                startActivity(new Intent(this, ScrollViewActivity.class));
                break;
            case R.id.btn_gridView:
                startActivity(new Intent(this, GridViewActivity.class));
                break;
        }
    }
}
