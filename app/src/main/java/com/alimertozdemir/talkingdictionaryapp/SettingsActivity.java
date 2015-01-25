package com.alimertozdemir.talkingdictionaryapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.alimertozdemir.talkingdictionaryapp.utils.SharedPreference;


public class SettingsActivity extends Activity {

    SharedPreference readTranslatedTextPref = new SharedPreference();
    private boolean isButtonChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch readSwitch = (Switch) findViewById(R.id.switch1);
        readSwitch.setTextOn("YES");
        readSwitch.setTextOff("NO");

        if (readTranslatedTextPref.getSetting("isButtonChecked", SettingsActivity.this) == null){
            readSwitch.setChecked(true);
            readTranslatedTextPref.saveSetting("isButtonChecked", SettingsActivity.this, "true");
        } else  if ("false".equals(readTranslatedTextPref.getSetting("isButtonChecked", SettingsActivity.this))){
            readSwitch.setChecked(false);
        } else  if ("true".equals(readTranslatedTextPref.getSetting("isButtonChecked", SettingsActivity.this))){
            readSwitch.setChecked(true);
        }

        if (readSwitch != null) {
            readSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isButtonChecked = isChecked;
                    readTranslatedTextPref.saveSetting("isButtonChecked", SettingsActivity.this, String.valueOf(isChecked));
                }
            });
        }


    }

    public boolean isButtonChecked() {
        return isButtonChecked;
    }

}
