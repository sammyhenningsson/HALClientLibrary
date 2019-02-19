package se.sammygadd.library.halclient;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Form extends Resource {

    private final String DEFAULT_SUBMIT_TEXT = "save";

    public interface OnSubmitListener {
        void onSubmit();
    }

    class Field {
        public final static int STRING  = 1;
        public final static int INTEGER = 2;
        public final static int FLOAT   = 3;
        public final static int BOOLEAN = 4;

        private String mName;
        private Integer mType;
        private String mLabel;
        private String mValue;
        private Boolean mRequired;
        private String mSubmit;

        public Field(JSONObject json) {
            setType(json.optString("type"));
            mName = json.optString("name");
            mLabel = json.optString("label", mName);
            mValue = json.optString("value");
            mRequired = json.optBoolean("required", false);
        }

        private void setType(String type) {
            switch (type) {
                case "string":
                    mType = STRING;
                    break;
                case "integer":
                    mType = INTEGER;
                    break;
                case "float":
                    mType = FLOAT;
                    break;
                case "boolean":
                    mType = BOOLEAN;
                    break;
                default:
                    mType = STRING;
            }
        }

        public String getName() {
            return mName;
        }

        public String getLabel() {
            return mLabel;
        }

        public Integer getType() {
            return mType;
        }

        public String getValue() {
            return mValue;
        }

        public void setValue(String value) {
            mValue = value;
        }

        public boolean isRequired() {
            return mRequired;
        }
    }

    private OnSubmitListener mSubmitListener;

    private List<Field> mFields;

    public Form(JSONObject json) {
        super(json);
    }

    public Form(JSONObject json, OnSubmitListener listener) {
        this(json);
        setOnSubmitListener(listener);
    }

    public String getName() {
        return getAttribute("name");
    }

    public String getTitle() {
        return getAttribute("title");
    }

    public String getMethod() {
        String method = getAttribute("method", "POST");
        return method.toUpperCase();
    }

    public String getHref() {
        return getAttribute("href");
    }

    public String getType() {
        return getAttribute("type");
    }

    public String getSubmit() {
        return getAttribute("submit", DEFAULT_SUBMIT_TEXT);
    }

    public String getData() {
        JSONObject json = new JSONObject();

        for (Field field: getFields()) {
            String name = field.getName();
            String value = field.getValue();
            try {
                json.put(field.getName(), field.getValue());
            } catch (JSONException e) {
                Log.i(Constants.TAG, "Failed to add " + name + " => " + value + " to JSONObject");
            }
        }
        return json.toString();
    }

    public List<Field> getFields() {
        if (mFields == null) {
            mFields = new ArrayList<>();
            JSONArray list = mJSON.optJSONArray("fields");
            for (int i = 0; i < list.length(); i++) {
                try {
                    Field field = new Field(list.getJSONObject(i));
                    mFields.add(field);
                } catch (JSONException e) {
                    Log.e(Constants.TAG, "Failed to parse field: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return mFields;
    }

    public void setOnSubmitListener(OnSubmitListener listener) {
        mSubmitListener = listener;
    }

    public boolean submit() {
        if (mSubmitListener == null) {
            return false;
        }
        mSubmitListener.onSubmit();
        return true;
    }
}
