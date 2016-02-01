package com.cj.android.touchpull_master;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cj.android.touchpull.TouchPullListener;
import com.cj.android.touchpull.TouchPullView;

/**
 * Created by jian.cao on 2016/1/25.
 */
public class WebViewActivity extends Activity implements TouchPullListener {
    private WebView webView;
    private TouchPullView touchPullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        touchPullView = (TouchPullView) findViewById(R.id.touchPullView);
        webView = (WebView) findViewById(R.id.webView);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        touchPullView.setTouchPullListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            touchPullView.complete();
            webView.loadUrl("https://www.baidu.com");
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
