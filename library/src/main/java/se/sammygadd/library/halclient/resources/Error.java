package se.sammygadd.library.halclient.resources;

import org.json.JSONObject;

import se.sammygadd.library.halclient.resources.Resource;

public class Error extends Resource {
    public Error(JSONObject json) {
        super(json);
    }

    public String getTitle() {
        return getAttribute("title");
    }

    public String getMessage() {
        return getAttribute("message");
    }

    public String getCode() {
        return getAttribute("code");
    }
}
