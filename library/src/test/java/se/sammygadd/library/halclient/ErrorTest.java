package se.sammygadd.library.halclient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ErrorTest {
    Error error;

    @Before
    public void setup() {
        String str = TestHelper.getResource(getClass(), "error.json");
        assertNotNull("Failed to load test resource \"error.json\"", str);
        try {
            error = new Error(new JSONObject(str));
        } catch (JSONException e) {
            System.out.println("Caught json exception: " + e.getMessage());
        }
        assertNotNull("Could not parse test resource \"error.json\"", error);
    }

    @Test
    public void error_contructor() throws JSONException {
        assertNotNull(error.toString());
    }

    @Test
    public void getTitle() {
        assertEquals("User not allowed", error.getTitle());
    }

    @Test
    public void getMessage() {
        assertEquals("This action is not permitted by the current user", error.getMessage());
    }

    @Test
    public void getCode() {
        assertEquals("FORBIDDEN", error.getCode());
    }
}
