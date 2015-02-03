
package com.battleground.talkingdictionaryapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.battleground.talkingdictionaryapp.model.TranslateResults;
import com.battleground.talkingdictionaryapp.model.UIDictionary;
import com.battleground.talkingdictionaryapp.utils.AppUtils;
import com.battleground.talkingdictionaryapp.utils.FloatingActionButton;
import com.battleground.talkingdictionaryapp.utils.SharedPreference;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DictionaryAppMainActivity extends ActionBarActivity implements OnClickListener, OnItemSelectedListener,
        View.OnTouchListener,
        HttpRequestCallback, TextToSpeech.OnInitListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    Map<String, String> hmLanguages;
    String translateURL = "https://translate.yandex.net/api/v1.5/tr.json/translate"; // ?key=trnsl.1.1.20140304T071909Z.b9e89fae4d489cf1.6486d3ae866bfe9c6a389b84cc61e5d860cf0112&lang=en-tr&text=dog";
    String detectURL = "https://translate.yandex.net/api/v1.5/tr.json/detect";
    String translateAPIKey = "trnsl.1.1.20140304T071909Z.b9e89fae4d489cf1.6486d3ae866bfe9c6a389b84cc61e5d860cf0112";
    String dictionaryURL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup";
    String dictionaryAPIKey = "dict.1.1.20141110T212048Z.1975cd38b6ef587c.5161dfd7fcf12b17b5e75c5c63ca90aa535b4947";
    String translatedText = "";
    HttpRequest httpRequest;
    Map<String, String> params = new HashMap<String, String>();
    private TranslateResults translate = new TranslateResults();
    private EditText etInput;
    private TextView tvOutput;
    private Button btnSend;
    private Gson gson;
    private Spinner spinnerFrom, spinnerTo;
    private String item = "English";
    private ImageButton ibMicWhite, ibMicRed;
    private FloatingActionButton fabDictionaryButton;
    private LinearLayout llMainLayout;
    private TextToSpeech tts;
    private AdView adView;
    SharedPreference mySearchHistoryPrefs = new SharedPreference();
    SharedPreference myTranslateHistoryPrefs = new SharedPreference();
    SharedPreference readTranslatedTextPref = new SharedPreference();
    ShareActionProvider myShareActionProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Tracker tracker = ((AnalyticsApplication)this.getApplication()).getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        tracker.enableAdvertisingIdCollection(true);
        tracker.setScreenName("DictionaryAppMainActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(AppUtils.getToolbarWithResizedLogo(this, toolbar));

        tts = new TextToSpeech(this, this);

        etInput = (EditText) findViewById(R.id.etInput);
        tvOutput = (TextView) findViewById(R.id.tvOutput);
        tvOutput.setMovementMethod(new ScrollingMovementMethod());
        btnSend = (Button) findViewById(R.id.btnTranslate);
        btnSend.setOnClickListener(this);
        httpRequest = new HttpRequest(DictionaryAppMainActivity.this);

        llMainLayout = (LinearLayout) findViewById(R.id.llMainLayout);
        llMainLayout.setOnClickListener(this);

        spinnerFrom = (Spinner) findViewById(R.id.languageFrom);
        spinnerTo = (Spinner) findViewById(R.id.languageTo);

        ibMicWhite = (ImageButton) findViewById(R.id.ibMicWhite);
        ibMicWhite.setOnClickListener(this);

        ibMicRed = (ImageButton) findViewById(R.id.ibMicRed);
        ibMicRed.setOnClickListener(this);

        adView = (AdView) this.findViewById(R.id.adMob);
        // request TEST ads to avoid being disabled for clicking your own ads
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for
                // emulators
                // test mode on DEVICE (this example code must be replaced with
                // your device uniquq ID)
                // .addTestDevice("2A10FF0C45ED8D55276EC0A8F57F8B9A") // Galaxy S2
                //.addTestDevice("AEF679F3204F75A99083554903D8B1E0") // Note 3
                .build();
        adView.loadAd(adRequest);

        final View activityRootView = findViewById(R.id.rlDictionaryActivity);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight()
                                - activityRootView.getHeight();
                        if (heightDiff > 300) { // if more than 100 (300) pixels, its
                            // probably a keyboard...
                            adView.setVisibility(View.GONE);
                            Log.d("View Tree Observer : ", "Klavye açıldı > " + heightDiff);
                        } else {
                            Log.d("View Tree Observer : ", "Klavye kapandı > " + heightDiff);
                            adView.setVisibility(View.VISIBLE);
                        }
                    }
                });

        List<String> languages = new ArrayList<String>();
        languages.addAll(Arrays.asList(getResources().getStringArray(R.array.languages)));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout,
                languages);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(dataAdapter);
        spinnerTo.setAdapter(dataAdapter);
        spinnerTo.setSelection(1);
        dataAdapter.notifyDataSetChanged();

        hmLanguages = new HashMap<String, String>();
        hmLanguages.put(languages.get(0), "null");
        hmLanguages.put(languages.get(1), "en");
        hmLanguages.put(languages.get(2), "ru");
        hmLanguages.put(languages.get(3), "pl");
        hmLanguages.put(languages.get(4), "de");
        hmLanguages.put(languages.get(5), "fr");
        hmLanguages.put(languages.get(6), "es");
        hmLanguages.put(languages.get(7), "it");
        hmLanguages.put(languages.get(8), "tr");

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);

        spinnerFrom.setOnTouchListener(this);
        spinnerTo.setOnTouchListener(this);

        String historyItem = (String) getIntent().getSerializableExtra("SearchedHistoryItem");
        etInput.setText(historyItem);

        fabDictionaryButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_microphone_white_24dp))
                .withButtonColor(getResources().getColor(R.color.pocket_color_1))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 22, 40)
                .create();

        fabDictionaryButton.setOnTouchListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTranslate:
                AppUtils.hideKeyboard(DictionaryAppMainActivity.this);
                if (!etInput.getText().toString().trim().equals("")) {
                    // Add searchItem to SharedPrefs
                    mySearchHistoryPrefs.addHistory("Search_History", DictionaryAppMainActivity.this,
                            etInput.getText().toString());
                    if (hmLanguages.get(spinnerFrom.getSelectedItem()).equals("null")
                            && !etInput.getText().toString().contains(" ")) {
                        AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.auto_detect_check));
                    } else if (hmLanguages.get(spinnerFrom.getSelectedItem()).equals("null")
                            && etInput.getText().toString().trim().contains(" ")) {
                        detectLanguageRequest(etInput.getText().toString());
                    } else if (!hmLanguages.get(spinnerFrom.getSelectedItem()).equals("null")
                            && !etInput.getText().toString().trim().contains(" ")) {
                        dictionaryRequest(hmLanguages.get(spinnerFrom.getSelectedItem()),
                                hmLanguages.get(spinnerTo.getSelectedItem()));
                    } else {
                        translateRequest(hmLanguages.get(spinnerFrom.getSelectedItem()),
                                hmLanguages.get(spinnerTo.getSelectedItem()));
                    }
                } else {
                    AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.type_inside));
                    etInput.requestFocus();
                }
                break;
            case R.id.llMainLayout:
                AppUtils.hideKeyboard(DictionaryAppMainActivity.this);
                break;
            case R.id.ibMicWhite:
                ibMicWhite.setVisibility(View.GONE);
                ibMicRed.setVisibility(View.VISIBLE);
                promptSpeechInput();
                break;
            case R.id.ibMicRed:
                promptSpeechInput();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.equals(spinnerFrom) || view.equals(spinnerTo)) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                AppUtils.hideKeyboard(DictionaryAppMainActivity.this);
            }
        } else if (view.equals(fabDictionaryButton)) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                fabDictionaryButton.setFloatingActionButtonDrawable(getResources().getDrawable(
                        R.drawable.ic_microphone_red_24dp));
                promptSpeechInput();
            }
        }
        return false;
    }

    public void detectLanguageRequest(String text) {
        String TAG = "DetectRequest";
        params.put("key", translateAPIKey);
        params.put("text", text);
        if (!AppUtils.checkNetworkConnection(DictionaryAppMainActivity.this)) {
            AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.check_network));
        } else {
            httpRequest.makeHttpPostWithVolley(detectURL, params, TAG, false);
        }
    }

    public void translateRequest(String langCodeFrom, String langCodeTo) {
        String TAG = "TranslateRequest";
        params.put("key", translateAPIKey);
        params.put("lang", langCodeFrom + "-" + langCodeTo);
        Log.d("Translate Request : FROM > TO >>>", langCodeFrom + ">" + langCodeTo);
        params.put("text", etInput.getText().toString());
        if (!AppUtils.checkNetworkConnection(DictionaryAppMainActivity.this)) {
            AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.check_network));
        } else {
            httpRequest.makeHttpPostWithVolley(translateURL, params, TAG, true);
        }
    }

    public void dictionaryRequest(String langCodeFrom, String langCodeTo) {
        String TAG = "DictionaryRequest";
        params.put("key", dictionaryAPIKey);
        params.put("lang", langCodeFrom + "-" + langCodeTo);
        Log.d("Translate Request : FROM > TO >>>", langCodeFrom + ">" + langCodeTo);
        params.put("text", etInput.getText().toString());
        if (!AppUtils.checkNetworkConnection(DictionaryAppMainActivity.this)) {
            AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.check_network));
        } else {
            httpRequest.makeHttpPostWithVolley(dictionaryURL, params, TAG, true);
        }
    }

    @Override
    public void callback(String jsonResponse, String operName) {
        gson = new Gson();
        Log.d("Response", jsonResponse.toString());
        if (operName.equals("TranslateRequest")) {
            translate = gson.fromJson(jsonResponse, TranslateResults.class);
            tvOutput.setText(translate.getText().get(0));
            // Add translatedText to Shared Preferences
            myTranslateHistoryPrefs.addHistory("Translate_History", DictionaryAppMainActivity.this,
                    translate.getText().get(0));
            translatedText = translate.getText().get(0);
            // Add translatedText to intent in order to send text to speech
            // input
            setShareIntent(translatedText);
            Locale selectedLocale = new Locale(hmLanguages.get(item));
            speakOut(translatedText, selectedLocale); // Text to Speech
        } else if (operName.equals("DictionaryRequest")) {
            Log.d("DictionaryRequest", jsonResponse.toString());
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONObject defObject = jsonObject.getJSONArray("def").getJSONObject(0);
                UIDictionary dictionary = gson.fromJson(defObject.toString(), UIDictionary.class);
                setDictionaryResponseInsideApp(dictionary);
                Log.d("DictionaryRequestCikti", defObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.text_not_found, etInput.getText().toString()));
                tvOutput.setText("");
            }
        } else if (operName.equals("DetectRequest")) {
            translate = gson.fromJson(jsonResponse, TranslateResults.class);
            String langCodeFrom = translate.getLang().trim();
            translateRequest(langCodeFrom, hmLanguages.get(spinnerTo.getSelectedItem()));
        }

    }

    public void setDictionaryResponseInsideApp(UIDictionary dictionary) {
        String outputText = "";
        String speachText = "";
        for (int i = 0; i < dictionary.getTr().size(); i++) {
            outputText = outputText + dictionary.getTr().get(i).toString() + "," + "<br>";
            speachText = speachText + dictionary.getTr().get(i).getSpeachString() + ", ";
        }

        outputText = outputText.substring(0, outputText.length() - 5);
        tvOutput.setText(Html.fromHtml("<b>" + getString(R.string.translation) +" :</b>" + "<br>" + outputText));

        Log.d("Okuma öncesi Cıktı : ", speachText);
        // Add translatedText to intent in order to send text to speech
        // input
        setShareIntent(speachText.substring(0, speachText.length() - 2));
        //setShareIntent(outputText.substring(0, outputText.indexOf(" ")).replaceAll("<br>"," "));
        Locale selectedLocale = new Locale(hmLanguages.get(item));
        speakOut(speachText, selectedLocale); // Text to Speech
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dictionary, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (!"".equals(translatedText))
            setShareIntent(translatedText);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.search_history) {
            if (myTranslateHistoryPrefs.getHistories("Translate_History", DictionaryAppMainActivity.this) == null) {
                AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.no_history));
            } else {
                AppUtils.gotoActivityWithResult(DictionaryAppMainActivity.this, SearchHistoryActivity.class, null, 2);
            }

            return true;
        }
        if (id == R.id.settings) {
            AppUtils.gotoActivity(DictionaryAppMainActivity.this, SettingsActivity.class, null, false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(String translatedText) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        Log.d("TRANSLATED TEXT > ", translatedText);
        intent.putExtra(Intent.EXTRA_TEXT, translatedText
                + "\n"
                + (Html.fromHtml("<b>" + getString(R.string.share_intent_text) +"" + "</b>")));

        myShareActionProvider.setShareIntent(intent);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        item = adapterView.getSelectedItem().toString();
        AppUtils.hideKeyboard(DictionaryAppMainActivity.this);
        Log.d("ITEM>>> ", adapterView.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // item = "null";
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                fabDictionaryButton.setFloatingActionButtonDrawable(getResources().getDrawable(
                        R.drawable.ic_microphone_white_24dp));
                // ibMicWhite.setVisibility(View.VISIBLE);
                // ibMicRed.setVisibility(View.GONE);
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etInput.setText(result.get(0));
                    if (!result.get(0).trim().contains(" ")) {
                        AppUtils.showToast(DictionaryAppMainActivity.this, getString(R.string.auto_detect_check));
                    }
                }
                break;
            }

            case 2:
                if (resultCode == RESULT_OK && null != data) {
                    String historyItem = data.getExtras().getString("SearchedHistoryItem");
                    etInput.setText(historyItem);
                }
        }
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc
        GoogleAnalytics.getInstance(this.getApplicationContext()).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this.getApplicationContext()).reportActivityStop(this);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            Locale selectedLocale = new Locale(hmLanguages.get(item));

            int result = tts.setLanguage(selectedLocale);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);
                speakOut("", selectedLocale);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String speachText, Locale selectedLocale) {

        if (!"".equals(speachText)) {
            if (readTranslatedTextPref.getSetting("isButtonChecked", DictionaryAppMainActivity.this) == null) {
                readTranslatedTextPref.saveSetting("isButtonChecked", DictionaryAppMainActivity.this, "true");
            }
            //String text = tvOutput.getText().toString();
            if ("true".equals(readTranslatedTextPref.getSetting("isButtonChecked", DictionaryAppMainActivity.this))) {
                tts.setLanguage(selectedLocale);
                tts.speak(speachText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

}
