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
        validationError = TestHelper.getValidationErrorResource(getClass(), "validation_error.json");
    }

    @Test
    public void error_toString() {
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
