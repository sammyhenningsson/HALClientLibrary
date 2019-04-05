package se.sammygadd.library.halclient;

import org.json.JSONObject;

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
