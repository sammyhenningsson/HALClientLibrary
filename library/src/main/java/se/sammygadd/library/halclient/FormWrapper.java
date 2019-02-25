package se.sammygadd.library.halclient;

public class FormWrapper extends ResourceWrapper {

    public static FormWrapper from(ResourceWrapper wrapper) {
        Form form = (Form) wrapper.getResource();
        String error = wrapper.getError();
        return new FormWrapper(form, error);
    }

    public FormWrapper(Form form, String error) {
        super(form, error);
    }

    public FormWrapper(Form form) {
        this(form, null);
    }

    public FormWrapper(String error) {
        this(null, error);
    }

    public Form getForm() {
        return (Form) getResource();
    }
}
