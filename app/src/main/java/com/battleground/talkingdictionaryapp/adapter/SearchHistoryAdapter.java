
package com.battleground.talkingdictionaryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.battleground.talkingdictionaryapp.R;

import java.util.ArrayList;

/**
 * Created by AliMert on 31.10.2014.
 */
public class SearchHistoryAdapter extends BaseAdapter {

    Context context = null;
    ArrayList<String> historyListData = null;

    public SearchHistoryAdapter(Context context, ArrayList<String> rowItems) {
        this.context = context;
        this.historyListData = rowItems;
    }

    @Override
    public int getCount() {
        return historyListData.size();
    }

    @Override
    public Object getItem(int position) {
        return historyListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater
                    .inflate(R.layout.activity_search_history_adapter, parent, false);
        }

        TextView textView1 = (TextView) convertView.findViewById(R.id.tvRow);
        textView1.setText(historyListData.get(position));

        return convertView;
    }
}
