package se.sammygadd.library.halclient.resources;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValidationError extends Error {

    public class Field {
        private String mName;
        private List<String> mMessages;

        public Field(String name, List<String> messages) {
            mName = name;
            mMessages = messages;
        }

        public String getName() {
            return mName;
        }

        public List<String> getMessages() {
            return mMessages;
        }

        public String getMessage() {
            if (mMessages == null || mMessages.isEmpty()) {
                return "";
            }
            StringBuilder str = new StringBuilder(mMessages.get(0));
            for (int i = 1; i < mMessages.size(); ++i) {
                str.append(", ");
                str.append(mMessages.get(i));
            }
            return str.toString();
        }
    }

    private List<Field> mFields;

    public ValidationError(JSONObject json) {
        super(json);
    }

    public List<Field> getFields() {
        if (mFields == null) {
            mFields = new ArrayList<>();
            JSONObject fields = mJSON.optJSONObject("fields");
            if (fields == null) {
                return mFields;
            }

            Iterator<String> keys = fields.keys();
            while (keys.hasNext()) {
                String name = keys.next();

                JSONArray list = fields.optJSONArray(name);
                if (list == null) {
                    continue;
                }

                List<String> messages = new ArrayList<>();

                for (int i = 0; i < list.length(); i++) {
                    String error = list.optString(i);
                    messages.add(error);
                }

                mFields.add(new Field(name, messages));
            }
        }
        return mFields;
    }

    public Field getField(String name) {
        List<Field> fields = getFields();
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public String getMessage(String name) {
        Field field = getField(name);
        if (field == null) {
            return null;
        } else {
            return field.getMessage();
        }
    }

    public List<String> getMessages(String name) {
        Field field = getField(name);
        if (field == null) {
            return new ArrayList<String>();
        } else {
            return field.getMessages();
        }
    }
}
