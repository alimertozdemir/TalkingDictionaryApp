package com.battleground.talkingdictionaryapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliMert on 7.9.2014.
 */
public class TranslateResults {

    private int code;
    private String lang;
    private List<String> text = new ArrayList<String>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "code='" + code + '\'' +
                ", lang='" + lang + '\'' +
                ", text=" + text +
                '}';
    }
}
