package com.battleground.talkingdictionaryapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.battleground.talkingdictionaryapp.utils.SharedPreference;


public class SettingsActivity extends ActionBarActivity {

    SharedPreference readTranslatedTextPref = new SharedPreference();
    private boolean isButtonChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_ab_up_compat);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Switch readSwitch = (Switch) findViewById(R.id.switch1);
        readSwitch.setTextOn(getString(R.string.yes));
        readSwitch.setTextOff(getString(R.string.no));

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
