package com.cj.android.touchpull_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cj.android.touchpull_master.CJ.CJActivity;


/**
 * Created by jian.cao on 2016/1/20.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        (findViewById(R.id.btn_listView)).setOnClickListener(this);
        (findViewById(R.id.btn_scrollView)).setOnClickListener(this);
        (findViewById(R.id.btn_gridView)).setOnClickListener(this);
        (findViewById(R.id.btn_webView)).setOnClickListener(this);
        (findViewById(R.id.btn_textView)).setOnClickListener(this);
        (findViewById(R.id.btn_editView)).setOnClickListener(this);
        (findViewById(R.id.btn_expandableListView)).setOnClickListener(this);
        (findViewById(R.id.btn_viewPager)).setOnClickListener(this);
        (findViewById(R.id.btn_CJ)).setOnClickListener(this);
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
            case R.id.btn_webView:
                startActivity(new Intent(this, WebViewActivity.class));
                break;
            case R.id.btn_textView:
                startActivity(new Intent(this, TextViewActivity.class));
                break;
            case R.id.btn_editView:
                startActivity(new Intent(this, EditTextActivity.class));
                break;
            case R.id.btn_expandableListView:
                startActivity(new Intent(this, ExpandableListViewActivity.class));
                break;
            case R.id.btn_viewPager:
                startActivity(new Intent(this, ViewPagerActivity.class));
                break;
            case R.id.btn_CJ:
                startActivity(new Intent(this, CJActivity.class));
                break;
        }
    }
}
