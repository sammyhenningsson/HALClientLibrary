package se.sammygadd.library.halclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;

import static org.junit.Assert.*;

public class TestHelper {
    public static String getString(Class clazz, String name) {
        InputStream is = clazz.getClassLoader().getResourceAsStream(name);
        assertNotNull("Failed to load test resource \"" + name + "\"", is);
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    public static JSONObject getJSONObject(Class clazz, String name) {
        String str = getString(clazz, name);
        JSONObject json = null;
        try {
            json = new JSONObject(str);
        } catch (JSONException e) {
            System.out.println("Caught json exception: " + e.getMessage());
        }
        assertNotNull("Could not parse test resource \"" + name + "\"", json);
        return json;
    }

    public static Resource getResource(Class clazz, String name) {
        JSONObject json = getJSONObject(clazz, name);
        Resource resource = new Resource(json);
        return resource;
    }

    public static Form getFormResource(Class clazz, String name) {
        JSONObject json = getJSONObject(clazz, name);
        Form form = new Form(json);
        return form;
    }

    public static Error getErrorResource(Class clazz, String name) {
        JSONObject json = getJSONObject(clazz, name);
        Error error  = new Error(json);
        return error;
    }

    public static ValidationError getValidationErrorResource(Class clazz, String name) {
        JSONObject json = getJSONObject(clazz, name);
        ValidationError error  = new ValidationError(json);
        return error;
    }
}
