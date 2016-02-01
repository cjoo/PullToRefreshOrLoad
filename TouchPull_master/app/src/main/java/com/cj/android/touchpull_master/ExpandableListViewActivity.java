package com.cj.android.touchpull_master;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian.cao on 2016/1/26.
 */
public class ExpandableListViewActivity extends Activity {
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandablelistview);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        add();
        expandableListView.setAdapter(new ExpandableAdapter(this));
    }

    private List<String> groupArray = new ArrayList<String>();
    private List<List<String>> childArray = new ArrayList<List<String>>();

    private void add() {
        for (int i = 0; i < 300; i++) {
            groupArray.add("第" + i + "行");
        }
        List<String> tempArray = new ArrayList<String>();
        tempArray.add("第一条");
        tempArray.add("第二条");
        tempArray.add("第三条");

        for (int index = 0; index < groupArray.size(); ++index) {
            childArray.add(tempArray);
        }
    }

    public class ExpandableAdapter extends BaseExpandableListAdapter {
        Activity activity;

        public ExpandableAdapter(Activity a) {
            activity = a;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childArray.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childArray.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            String string = childArray.get(groupPosition).get(childPosition);
            return getGenericView(string);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupArray.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groupArray.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String string = groupArray.get(groupPosition);
            return getGenericView(string);
        }

        // View stub to create Group/Children 's View
        public TextView getGenericView(String string) {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);
            TextView text = new TextView(activity);
            text.setLayoutParams(layoutParams);
            // Center the text vertically
            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            text.setPadding(36, 0, 0, 0);
            text.setText(string);
            return text;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
