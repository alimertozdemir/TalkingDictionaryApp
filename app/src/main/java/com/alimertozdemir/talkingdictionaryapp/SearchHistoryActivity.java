
package com.alimertozdemir.talkingdictionaryapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alimertozdemir.talkingdictionaryapp.adapter.SearchHistoryAdapter;
import com.alimertozdemir.talkingdictionaryapp.utils.AppUtils;
import com.alimertozdemir.talkingdictionaryapp.utils.SharedPreference;

import java.io.Serializable;
import java.util.HashMap;

public class SearchHistoryActivity extends Activity {
    HashMap<String, Serializable> postIntentData = new HashMap<String, Serializable>();
    SharedPreference myPrefs = new SharedPreference();
    ListView listView;
    SearchHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.lvSearchHistory);
        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                postIntentData.put("SearchedHistoryItem",
                        myPrefs.getHistories("Search_History", SearchHistoryActivity.this).get(i));
                Log.d("HISTORY ITEM SELECTED : ", myPrefs.getHistories("Search_History", SearchHistoryActivity.this)
                        .get(i));
                AppUtils.gotoActivity(SearchHistoryActivity.this,
                        DictionaryTestApp.class, postIntentData, true);
            }
        });

        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

    }

    public void populateListView() {
        adapter = new SearchHistoryAdapter(getApplicationContext(),
                myPrefs.getHistories("Search_History", SearchHistoryActivity.this));
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clean_history) {
            myPrefs.removeAllHistory("Search_History", SearchHistoryActivity.this);
            populateListView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
