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

    public String getError() {
        return mError;
    }

    public boolean isSuccessful() {
        return mError == null;
    }

    public boolean isForm() {
        return mResource instanceof Form;
    }
}
