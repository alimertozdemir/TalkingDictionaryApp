package com.battleground.talkingdictionaryapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.battleground.talkingdictionaryapp.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by AliMert on 28.9.2014.
 */
public class AppUtils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && activity.getWindow().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void showToast(Context activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static boolean checkNetworkConnection(Context activity) {
        ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public static void gotoActivityWithResult(Activity activity, Class nextActivityClass, HashMap<String, Serializable> postIntentData, int requestCode){
        Intent intent = new Intent(activity, nextActivityClass);

        if (postIntentData != null){
            Iterator<Map.Entry<String, Serializable>> it = postIntentData.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, Serializable> entry = it.next();
                String key = entry.getKey();
                Serializable value = entry.getValue();
                intent.putExtra(key, value);
            }
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void gotoActivity(Activity activity, Class nextActivityClass, HashMap<String, Serializable> postIntentData, boolean closeCurrentActivity){
        Intent intent = new Intent(activity, nextActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (postIntentData != null){
            Iterator<Map.Entry<String, Serializable>> it = postIntentData.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, Serializable> entry = it.next();
                String key = entry.getKey();
                Serializable value = entry.getValue();
                intent.putExtra(key, value);
            }
        }
        activity.startActivity(intent);

        if (closeCurrentActivity){
            activity.finish();
        }

    }

    public static Toolbar getToolbarWithResizedLogo(Activity activity, Toolbar toolbar) {
        Drawable logo = activity.getResources().getDrawable(R.drawable.ic_launcher);
        toolbar.setLogo(logo);

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null)
                if (child.getClass() == ImageView.class) {
                    ImageView iv2 = (ImageView) child;
                    if (iv2.getDrawable() == logo) {
                        iv2.setAdjustViewBounds(true);
                        int padding = (int) (6 * activity.getResources().getDisplayMetrics().density);
                        iv2.setPadding(-6, padding, padding, padding);
                    }
                }
        }

        return toolbar;
    }

}
