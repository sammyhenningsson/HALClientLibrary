package se.sammygadd.library.halclient.resources;

import org.json.JSONObject;

public class Link {
    private String mRel;
    private String mHref;
    private String mTitle;
    private String mName;
    private boolean mTemplated;

    public Link(String rel, JSONObject json) {
        mRel = rel;
        parse(json);
    }

    public void parse(JSONObject json) {
        mHref = json.optString("href");
        mTitle = json.optString("title", mRel);
        mName = json.optString("name");
        mTemplated = json.optBoolean("templated");
    }

    public String rel() {
        return mRel;
    }

    public String href() {
        return mHref;
    }

    public String title() {
        return mTitle;
    }

    public String name() {
        return mName;
    }

    public boolean isTemplated() {
        return mTemplated;
    }
}
