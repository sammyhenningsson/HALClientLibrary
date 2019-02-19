package se.sammygadd.library.halclient;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Resource {
    JSONObject mJSON;
    HashMap<String,String> mAttributes;
    HashMap<String,String> mLinks;
    // HashMap<String,Resource> mEmbedded;

    public Resource(JSONObject json) {
        mJSON = json;
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
                if (mJSON.optJSONObject(key) != null || mJSON.optJSONArray(key) != null) {
                    continue;
                }
                mAttributes.put(key, mJSON.optString(key));
            }
        }
        return mAttributes;
    }

    public HashMap<String, String> getLinks() {
        if (mLinks == null) {
            mLinks = new HashMap<>();
            try {
                JSONObject links = mJSON.getJSONObject("_links");
                Iterator<String> keys = links.keys();
                while (keys.hasNext()) {
                    String rel = keys.next();
                    JSONObject link = links.getJSONObject(rel);
                    String href = link.getString(rel);
                    mLinks.put(rel, href);
                }
            } catch (JSONException e) {
                Log.e(Constants.TAG, "Failed to parse links: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return mLinks;
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

    public String getLinkHref(String rel) {
        HashMap<String, String> links = getLinks();
        return links.get(rel);
    }

    public String toString() {
        try {
            return mJSON.toString(2);
        } catch (JSONException e) {
            return mJSON.toString();
        }
    }

    public Set<String> actions() {
        return getLinks().keySet();
    }
}
