package se.sammygadd.library.halclient;

import java.util.ArrayList;
import java.util.List;

import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;

public class ResourceWrapper {
    private Resource mResource;
    private List<Resource> mResources;

    public ResourceWrapper(Resource resource) {
        mResource = resource;
        mResources = new ArrayList<>();
    }

    public ResourceWrapper(List<Resource> resources) {
        mResources = resources;
    }

    public Resource getResource() {
        return mResource;
    }

    public List<Resource> getResources() {
        return mResources;
    }

    public Form getForm() {
        return isForm() ? (Form) mResource : null;
    }

    public Error getError() {
        return isError() ? (Error) mResource : null;
    }

    public ValidationError getValidationError() {
        return isValidationError() ? (ValidationError) mResource : null;
    }

    public boolean isResource() {
        if (mResource == null) {
            return false;
        }
        return !isArray() && !isError();
    }

    public boolean isArray() {
        return !mResources.isEmpty();
    }

    public boolean isForm() {
        return mResource instanceof Form;
    }

    public boolean isError() {
        return mResource instanceof Error;
    }

    public boolean isValidationError() {
        return mResource instanceof ValidationError;
    }
}
