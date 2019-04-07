package se.sammygadd.library.halclient.resources;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import se.sammygadd.library.halclient.TestHelper;

import static org.junit.Assert.*;

public class ErrorTest {
    Error error;

    @Before
    public void setup() {
        error = TestHelper.getErrorResource(getClass(), "error.json");
    }

    @Test
    public void error_toString() throws JSONException {
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
