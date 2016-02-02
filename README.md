![](http://a4.qpic.cn/psb?/V10dK3v20JUav3/MJ09fEulJQ6q.SAnaNFctFCQSjXbOGIXBQDxDkMTjPo!/b/dG8BAAAAAAAA&ek=1&kp=1&pt=0&bo=4AFWA.ABVgMCCCw!&sce=0-12-12&rf=0-18)
#xml布局文件(activity_listview)：
  <com.cj.android.touchpull.TouchPullView xmlns:android="http://schemas.android.com/apk/res/android"
  
    android:id="@+id/touchPullView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <ListView
        android:id="@+id/listView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
\</com.cj.android.touchpull.TouchPullView>



#activity代码：
public class ListViewActivity extends Activity implements TouchPullListener {

    private TouchPullView touchPullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        
        touchPullView = (TouchPullView) findViewById(R.id.touchPullView);
        touchPullView.setTouchPullListener(this);
    }

    @Override
    public void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
               // TODO do something
            }
        }).start();
    }

    @Override
    public void load() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO do something
            }
        }).start();
    }
  
}
