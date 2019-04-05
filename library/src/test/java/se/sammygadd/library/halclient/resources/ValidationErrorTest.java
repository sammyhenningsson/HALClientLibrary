package se.sammygadd.library.halclient.resources;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import se.sammygadd.library.halclient.TestHelper;
import se.sammygadd.library.halclient.resources.ValidationError;

import static org.junit.Assert.*;

public class ValidationErrorTest {
    ValidationError validationError;

    @Before
    public void setup() {
        String str = TestHelper.getResource(getClass(), "validation_error.json");
        assertNotNull("Failed to load test resource \"validation_error.json\"", str);
        try {
            validationError = new ValidationError(new JSONObject(str));
        } catch (JSONException e) {
            System.out.println("Caught json exception: " + e.getMessage());
        }
        assertNotNull("Could not parse test resource \"validation_error.json\"", validationError);
    }

    @Test
    public void validationError_contructor() throws JSONException {
        assertNotNull(validationError.toString());
    }

    @Test
    public void getFields() {
        List<ValidationError.Field> fields = validationError.getFields();
        assertEquals(2, fields.size());

        ValidationError.Field emailErrors = fields.get(0);
        assertEquals("email", emailErrors.getName());
        assertEquals("cannot be empty", emailErrors.getMessages().get(0));
        assertEquals("cannot be empty", emailErrors.getMessage());

        ValidationError.Field startDateErrors = fields.get(1);
        assertEquals("start_date", startDateErrors.getName());
        assertEquals("must have format YYYY-MM-DD", startDateErrors.getMessages().get(0));
        assertEquals("must be in the future", startDateErrors.getMessages().get(1));
        assertEquals("must have format YYYY-MM-DD, must be in the future", startDateErrors.getMessage());
    }
}
