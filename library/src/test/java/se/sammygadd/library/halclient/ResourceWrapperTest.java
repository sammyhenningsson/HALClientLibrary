package se.sammygadd.library.halclient;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.ValidationError;

import static org.junit.Assert.*;

public class ResourceWrapperTest {
    Resource resource;
    Form form;
    Error error;
    ValidationError validationError;

    @Test
    public void wrapped_Resource() {
        resource = TestHelper.getResource(getClass(), "post.json");
        ResourceWrapper wrapper = new ResourceWrapper(resource);

        assertFalse(wrapper.isForm());
        assertFalse(wrapper.isArray());
        assertFalse(wrapper.isError());
        assertFalse(wrapper.isValidationError());
    }

    @Test
    public void wrapped_Form() {
        form = TestHelper.getFormResource(getClass(), "form.json");
        ResourceWrapper wrapper = new ResourceWrapper(form);

        assertTrue(wrapper.isForm());
        assertFalse(wrapper.isArray());
        assertFalse(wrapper.isError());
        assertFalse(wrapper.isValidationError());
    }

    @Test
    public void wrapped_Error() {
        error = TestHelper.getErrorResource(getClass(), "error.json");
        ResourceWrapper wrapper = new ResourceWrapper(error);

        assertTrue(wrapper.isError());
        assertFalse(wrapper.isForm());
        assertFalse(wrapper.isArray());
        assertFalse(wrapper.isValidationError());
    }

    @Test
    public void wrapped_ValidationError() {
        validationError = TestHelper.getValidationErrorResource(getClass(), "validation_error.json");
        ResourceWrapper wrapper = new ResourceWrapper(validationError);

        assertTrue(wrapper.isValidationError());
        assertTrue(wrapper.isError());
        assertFalse(wrapper.isForm());
        assertFalse(wrapper.isArray());
    }
}
