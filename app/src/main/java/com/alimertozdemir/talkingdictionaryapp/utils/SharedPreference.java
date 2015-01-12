
package com.alimertozdemir.talkingdictionaryapp.utils;

/**
 * Created by AliMert on 2.11.2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {

    public static final String PREFS_NAME = "DICTIONARY_APP";
    //public static final String HISTORIES = "Search_History";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining histories.
    public void saveHistories(String TAG, Context context, List<String> history) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonHistories = gson.toJson(history);

        editor.putString(TAG, jsonHistories);

        editor.commit();
    }

    public void addHistory(String TAG, Context context, String history) {
        List<String> histories = getHistories(TAG, context);
        if (histories == null)
            histories = new ArrayList<String>();
        histories.add(history);
        saveHistories(TAG, context, histories);
    }

    public void removeHistory(String TAG, Context context, String history) {
        ArrayList<String> histories = getHistories(TAG, context);
        if (histories != null) {
            histories.remove(history);
            saveHistories(TAG, context, histories);
        }
    }

    public void removeAllHistory(String TAG, Context context) {
        ArrayList<String> histories = getHistories(TAG, context);
        if (histories != null) {
            histories.removeAll(histories);
            saveHistories(TAG, context, histories);
        }
    }

    public ArrayList<String> getHistories(String TAG, Context context) {
        SharedPreferences settings;
        List<String> histories;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(TAG)) {
            String jsonHistories = settings.getString(TAG, null);
            Gson gson = new Gson();
            String[] historyItems = gson.fromJson(jsonHistories,
                    String[].class);

            histories = Arrays.asList(historyItems);
            histories = new ArrayList<String>(histories);
        } else
            return null;

        return (ArrayList<String>) histories;
    }


    //------------------------------------------------------

    public void saveSetting(String TAG, Context context, String setting) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(TAG, setting);

        editor.commit();
    }

    public String getSetting(String TAG, Context context) {
        SharedPreferences settings;
        String setting;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(TAG)) {
            setting = settings.getString(TAG, null);

        } else
            return null;

        return setting;
    }
}
