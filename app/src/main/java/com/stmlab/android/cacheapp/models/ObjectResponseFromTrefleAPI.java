package com.stmlab.android.cacheapp.models;

import org.json.JSONObject;

import java.util.ArrayList;

public class ObjectResponseFromTrefleAPI {
    ArrayList<TrefleModel> data;
    JSONObject links;
    JSONObject meta;

    public ArrayList<TrefleModel> getData() {
        return data;
    }

    public void setData(ArrayList<TrefleModel> data) {
        this.data = data;
    }

    public JSONObject getLinks() {
        return links;
    }

    public void setLinks(JSONObject links) {
        this.links = links;
    }

    public JSONObject getMeta() {
        return meta;
    }

    public void setMeta(JSONObject meta) {
        this.meta = meta;
    }
}
