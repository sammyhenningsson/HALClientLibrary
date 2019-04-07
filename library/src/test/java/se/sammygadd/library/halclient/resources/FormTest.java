package se.sammygadd.library.halclient.resources;

import org.junit.Before;
import org.junit.Test;

import se.sammygadd.library.halclient.TestHelper;

import static org.junit.Assert.*;

public class FormTest {
    Form form;

    @Before
    public void setup() {
        form = TestHelper.getFormResource(getClass(), "form.json");
    }

    @Test
    public void form_toString() {
        assertNotNull(form.toString());
    }

    @Test
    public void getMethod() {
        assertEquals("POST", form.getMethod());
    }

}
