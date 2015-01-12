package com.alimertozdemir.talkingdictionaryapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliMert on 12.11.2014.
 */
public class UIDictionary {

    List<DictionaryResult> tr = new ArrayList<DictionaryResult>();

    public List<DictionaryResult> getTr() {
        return tr;
    }

    public void setTr(List<DictionaryResult> tr) {
        this.tr = tr;
    }

    @Override
    public String toString() {
        return "UIDictionary{" +
                "tr=" + tr +
                '}';
    }

}
