package se.sammygadd.library.halclient.resources;

import org.json.JSONObject;

public class Curie extends Link {
    public Curie(JSONObject json) {
        super("", json);
    }

    public String resolve(String param) {
        return href().replaceAll("\\{.*\\}", param);
    }
}
