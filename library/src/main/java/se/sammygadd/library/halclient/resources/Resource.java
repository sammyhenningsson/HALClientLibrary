package se.sammygadd.library.halclient.resources;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import se.sammygadd.library.halclient.Constants;

public class Resource {
    String mEtag;
    Date mExpireAt;

    JSONObject mJSON;
    HashMap<String,String> mAttributes;
    HashMap<String, Link> mLinks;
    HashMap<String, Curie> mCuries;
    HashMap<String, List<Resource>> mEmbedded;

    public Resource(JSONObject json) {
        mJSON = json;
    }

    public String getEtag() {
        return mEtag;
    }

    public void setEtag(String etag) {
        mEtag = etag;
    }

    public boolean isStale() {
        if (mExpireAt == null) {
            return true;
        }
        Date now = new Date();
        return mExpireAt.before(now);
    }

    public void setExpiration(Date expireAt) {
        mExpireAt = expireAt;
    }

    public HashMap<String, String> getAttributes() {
        if (mAttributes == null) {
            mAttributes = new HashMap<>();
            Iterator<String> keys = mJSON.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.equals("_links") || key.equals("_embedded")) {
                    continue;
                }
                // FIXME: How should we handle arrays or hash as attributes?
                if (mJSON.optJSONObject(key) != null || mJSON.optJSONArray(key) != null) {
                    continue;
                }
                mAttributes.put(key, mJSON.optString(key));
            }
        }
        return mAttributes;
    }

    public HashMap<String, Link> getLinks() {
        if (mLinks == null) {
            mLinks = new HashMap<>();
            try {
                JSONObject links = mJSON.optJSONObject("_links");
                if (links == null) {
                    return mLinks;
                }
                Iterator<String> keys = links.keys();
                while (keys.hasNext()) {
                    String rel = keys.next();
                    if (rel.equals("curies")) {
                        parseCuries(links.getJSONArray(rel));
                    } else {
                        Link link = new Link(rel, links.getJSONObject(rel));
                        mLinks.put(rel, link);
                    }
                }
            } catch (JSONException e) {
                Log.e(Constants.TAG, "Failed to parse links: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return mLinks;
    }

    private void parseCuries(JSONArray curies) {
        mCuries = new HashMap<>();
        for(int i = 0; i < curies.length(); ++i) {
            try {
                JSONObject json = curies.getJSONObject(i);
                Curie curie = new Curie(json);
                mCuries.put(curie.name(), curie);
            } catch (JSONException e) {
                System.out.println("Failed to parse curie: " + curies.toString());
            }
        }
    }

    public HashMap<String, Curie> getCuries() {
        if (mCuries == null) {
            getLinks();
        }
        return mCuries;
    }

    public HashMap<String, List<Resource>> getEmbedded() {
        if (mEmbedded == null) {
            mEmbedded = new HashMap<>();
            try {
                JSONObject embedded = mJSON.optJSONObject("_embedded");
                if (embedded == null) return mEmbedded;

                Iterator<String> keys = embedded.keys();
                while (keys.hasNext()) {
                    String rel = keys.next();
                    List<Resource> list = new ArrayList<>();
                    JSONObject jsonObject = embedded.optJSONObject(rel);
                    if (jsonObject != null) {
                        list.add(new Resource(jsonObject));
                        mEmbedded.put(rel, list);
                        continue;
                    }
                    JSONArray jsonArray = embedded.optJSONArray(rel);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            list.add(new Resource(json));
                        }
                        mEmbedded.put(rel, list);
                        continue;
                    }
                }
            } catch (JSONException e) {
                Log.e(Constants.TAG, "Failed to parse embedded resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return mEmbedded;
    }

    public String getAttribute(String name) {
        HashMap<String, String> attributes = getAttributes();
        return attributes.get(name);
    }

    public String getAttribute(String name, String defaultValue) {
        String value = getAttribute(name);
        if (value == null) { return defaultValue; }
        return value;
    }

    public Link getLink(String rel) {
        return getLinks().get(rel);
    }

    public Curie getCurie(String rel) {
        return getCuries().get(rel);
    }

    public List<Resource> getEmbedded(String name) {
        if (getEmbedded().containsKey(name)) {
            return mEmbedded.get(name);
        }
        return new ArrayList<Resource>();
    }

    public String toString() {
        try {
            int indentSpaces = 2;
            return mJSON.toString(indentSpaces);
        } catch (JSONException e) {
            return mJSON.toString();
        }
    }

    public Set<String> actions() {
        return getLinks().keySet();
    }
}
