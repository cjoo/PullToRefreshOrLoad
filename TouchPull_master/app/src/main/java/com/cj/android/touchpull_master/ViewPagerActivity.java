package com.cj.android.touchpull_master;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.android.touchpull.TouchPullListener;
import com.cj.android.touchpull.TouchPullView;

/**
 * Created by maesinfo-024 on 2016/1/28.
 */
public class ViewPagerActivity extends FragmentActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(mFragmentPagerAdapter);
    }

    private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new F1();
                case 1:
                    return new F2();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    @SuppressLint("ValidFragment")
    private class F1 extends Fragment implements TouchPullListener {
        private ListView listView;
        private ListViewActivity.MyAdapter myAdapter;
        private TouchPullView touchPullView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_listview, null);
            listView = (ListView) view.findViewById(R.id.listView);
            listView.setAdapter(myAdapter = new ListViewActivity.MyAdapter(getActivity()));
            touchPullView = (TouchPullView) view.findViewById(R.id.touchPullView);
            touchPullView.setTouchPullListener(this);
            return view;
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
    }

    @SuppressLint("ValidFragment")
    private class F2 extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_scroll, null);
            ((TextView) view.findViewById(R.id.textView1)).setText("1、年底最后一搏了，我只要五分钟，五分钟后不出意外，就叫我张总！如果出意外，那就叫张某！不说了运钞来了…\n2、如果回" +
                    "家过年有人问你赚了多少钱？\n你可以这样回答：我炒股了！\n3、快要过年了，兜里不揣个7万八万的，真不好意思出门，“有钱" +
                    "”就是这么任性，想买啥都破不开。。。\n4、还有半个月就要过年了，按照中国人的传统，过年必须要回家，此时就会面临一个" +
                    "问题：“到底怎么才能衣锦还乡？”\n尤其是出入CBD高级写字楼的各位Tony，John，Mary，James又要翻山越岭，火车换驴车的回" +
                    "到县城，变回王大柱子，三铁棍子，春花妮子，嗑着朴实的瓜子，坐在炕头搓着麻将啦！\n5、很快就要过年了，谁借我5000元，" +
                    "让我买件像样的新衣服。分期还款，50年，每年一百，每月8块3元，每日2毛7，我天天给你发红包，上午1毛3，下午1毛4，每天都" +
                    "是一生一世，天天有惊喜。。。\n每天有联系，这样我们五十年不离不弃。\n6、眼看就要过年了，这手头有点紧呀！忍痛出售此" +
                    "坐驾：宝马血统，真皮座椅，座椅加热，零污染，360度全景天窗，定速巡航，四驱，两座，中央差速，全时四驱，全地形模式，" +
                    "非承载底盘，声控自动挡！控制方式：前进：得儿驾~~~~。刹车：吁~~~~。零油耗，百公里只需一捆草。。。有需要的速与我联系" +
                    "！");
            ((TextView) view.findViewById(R.id.textView2)).setText("1、年底最后一搏了，我只要五分钟，五分钟后不出意外，就叫我张总！如果出意外，那就叫张某！不说了运钞来了…\n2、如果回" +
                    "家过年有人问你赚了多少钱？\n你可以这样回答：我炒股了！\n3、快要过年了，兜里不揣个7万八万的，真不好意思出门，“有钱" +
                    "”就是这么任性，想买啥都破不开。。。\n4、还有半个月就要过年了，按照中国人的传统，过年必须要回家，此时就会面临一个" +
                    "问题：“到底怎么才能衣锦还乡？”\n尤其是出入CBD高级写字楼的各位Tony，John，Mary，James又要翻山越岭，火车换驴车的回" +
                    "到县城，变回王大柱子，三铁棍子，春花妮子，嗑着朴实的瓜子，坐在炕头搓着麻将啦！\n5、很快就要过年了，谁借我5000元，" +
                    "让我买件像样的新衣服。分期还款，50年，每年一百，每月8块3元，每日2毛7，我天天给你发红包，上午1毛3，下午1毛4，每天都" +
                    "是一生一世，天天有惊喜。。。\n每天有联系，这样我们五十年不离不弃。\n6、眼看就要过年了，这手头有点紧呀！忍痛出售此" +
                    "坐驾：宝马血统，真皮座椅，座椅加热，零污染，360度全景天窗，定速巡航，四驱，两座，中央差速，全时四驱，全地形模式，" +
                    "非承载底盘，声控自动挡！控制方式：前进：得儿驾~~~~。刹车：吁~~~~。零油耗，百公里只需一捆草。。。有需要的速与我联系" +
                    "！");
            return view;
        }
    }
}
