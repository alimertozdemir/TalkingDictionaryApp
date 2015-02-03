package com.battleground.talkingdictionaryapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliMert on 11.11.2014.
 */
public class DictionaryResult {

    private String text = "";
    private String pos = "";
    private List<DictionaryResult> syn = new ArrayList<DictionaryResult>();
    private List<DictionaryResult> mean = new ArrayList<DictionaryResult>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<DictionaryResult> getSyn() {
        return syn;
    }

    public void setSyn(List<DictionaryResult> syn) {
        this.syn = syn;
    }

    public List<DictionaryResult> getMean() {
        return mean;
    }

    public void setMean(List<DictionaryResult> mean) {
        this.mean = mean;
    }

    @Override
    public String toString() {
        return  text + " (" + pos + ")";
                //"Synonyms= " + syn + "<br>" +
                //"Means= " + mean;
    }

    public String getSpeachString() {
        return  text;
    }
}
