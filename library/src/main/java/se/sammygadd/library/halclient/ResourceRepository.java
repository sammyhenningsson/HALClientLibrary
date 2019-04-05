package se.sammygadd.library.halclient;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;

import org.json.JSONObject;

import java.util.HashMap;

public class ResourceRepository {
    private MutableLiveData<Form> mRegistrationForm;
    private HashMap<String, Resource> mStorage;

    private static ResourceRepository mRepository;

    public static ResourceRepository get() {
        if (mRepository == null) {
            mRepository = new ResourceRepository();
        }
        return mRepository;
    }

    public ResourceRepository() {
        mRegistrationForm = new MutableLiveData<>();
        mStorage = new HashMap<>();
    }

    private ApiService apiService() {
        return ApiService.get();
    }

    public LiveData<ResourceWrapper> getResource(String uri) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();
        apiService().get(uri, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(Constants.TAG, "status: " + statusCode + "\n" + response.toString());
                Resource resource = parseResource(headers, response);
                store(resource, headers);
                data.setValue(new ResourceWrapper(resource));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                ResourceWrapper wrapper = processFailure(statusCode, headers, response);
                data.setValue(wrapper);
            }
        });
        return data;
    }

    public LiveData<ResourceWrapper> submitForm(Form form) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();

        submitForm(form, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(Constants.TAG, "status: " + statusCode + "\n" + response.toString());
                Resource resource = parseResource(headers, response);
                store(resource, headers);
                data.setValue(new ResourceWrapper(resource));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                ResourceWrapper wrapper = processFailure(statusCode, headers, response);
                data.setValue(wrapper);
            }
        });

        return data;
    }

    private void submitForm(Form form, JsonHttpResponseHandler responseHandler) {
        String data = form.getData();
        Log.d(Constants.TAG, "Submiting form: " + data);
        StringEntity body = new StringEntity(data, HTTP.UTF_8);
        String url = form.getHref();
        String contentType = form.getType();

        switch (form.getMethod()) {
            case "POST":
                apiService().post(url, body, contentType, responseHandler);
                break;
            case "PUT":
                apiService().put(url, body, contentType, responseHandler);
                break;
            case "PATCH":
                apiService().patch(url, body, contentType, responseHandler);
                break;
        }
    }

    private Resource parseResource(Header[] headers, JSONObject response) {
        if (isForm(headers)) {
            return new Form(response);
        } else if (isError(headers)) {
            if (isValidationError(response)) {
                return new ValidationError(response);
            } else {
                return new Error(response);
            }
        } else {
            return new Resource(response);
        }
    }

    private boolean isForm(Header[] headers) {
        return contentType(headers).contains("profile=shaf-form");
    }

    private boolean isError(Header[] headers) {
        return contentType(headers).contains("profile=shaf-error");
    }

    private boolean isValidationError(JSONObject response) {
        return !response.isNull("fields");
    }

    private String contentType(Header[] headers) {
        for (int i = 0; i < headers.length; ++i) {
            Header header = headers[i];
            if (header.getName().equals("Content-Type")) {
                return header.getValue();
            }
        }
        return "";
    }

    private ResourceWrapper processFailure(int statusCode, Header[] headers, JSONObject response) {
        Log.e(Constants.TAG,Integer.toString(statusCode) + ": " + response.toString());
        Resource error = parseResource(headers, response);
        String msg = error.getAttribute("title", "Request failed with status: " + statusCode);
        return new ResourceWrapper(error, msg);
    }

    public void store(Resource resource, Header[] headers) {
        String href = resource.getLink("self").href();
        mStorage.put(href, resource);
    }
}
