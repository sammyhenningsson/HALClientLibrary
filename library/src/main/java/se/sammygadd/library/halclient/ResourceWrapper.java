package se.sammygadd.library.halclient;

public class ResourceWrapper {
    private Resource mResource;
    private String mError;

    public ResourceWrapper(Resource resource, String error) {
        mResource = resource;
        mError = error;
    }

    public ResourceWrapper(Resource resource) {
        this(resource, null);
    }

    public ResourceWrapper(String error) {
        this(null, error);
    }

    public Resource getResource() {
        return mResource;
    }

    public Form getForm() {
        return isForm() ? (Form) mResource : null;
    }

    public ValidationError getValidationError() {
        return isValidationError() ? (ValidationError) mResource : null;
    }

    public String getError() {
        return mError;
    }

    public boolean isSuccessful() {
        return mError == null;
    }

    public boolean isFailure() {
        return !isSuccessful();
    }

    public boolean isForm() {
        return mResource instanceof Form;
    }

    public boolean isValidationError() {
        return mResource instanceof ValidationError;
    }
}
