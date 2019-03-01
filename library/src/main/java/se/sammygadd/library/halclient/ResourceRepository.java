package se.sammygadd.library.halclient;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;
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
                Log.i(Constants.TAG, response.toString());
                boolean isForm = false;
                for (int i = 0; i < headers.length; ++i) {
                    Header header = headers[i];
                    if (header.getName().equals("Content-Type")) {
                        Log.i(Constants.TAG, "Content-Type: " + header.getValue());
                        isForm = header.getValue().contains("profile=shaf-form");
                        break;
                    }
                }
                Resource resource;
                if (isForm) {
                    resource = new Form(response);
                } else {
                    resource = new Resource(response);
                }
                store(resource, headers);
                ResourceWrapper wrapper = new ResourceWrapper(resource);
                data.setValue(wrapper);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                ResourceWrapper wrapper = processFailure(statusCode, headers, response);
                data.setValue(wrapper);
            }
        });
        return data;
    }

    public LiveData<FormWrapper> getForm(String uri) {
        MutableLiveData<FormWrapper> data = new MutableLiveData<>();
        apiService().get(uri, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(Constants.TAG, response.toString());
                Form form = new Form(response);

                FormWrapper wrapper = new FormWrapper(form);
                data.setValue(wrapper);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                ResourceWrapper wrapper = processFailure(statusCode, headers, response);
                data.setValue(FormWrapper.from(wrapper));
            }
        });
        return data;
    }

    public LiveData<ResourceWrapper> submitForm(Form form) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();

        submitForm(form, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(Constants.TAG, Integer.toString(statusCode) + ": " + response.toString());
                Resource resource = new Resource(response);
                store(resource, headers);
                ResourceWrapper wrapper = new ResourceWrapper(resource);
                data.setValue(wrapper);
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
        Log.i(Constants.TAG, "Submiting form: " + data);
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

    private ResourceWrapper processFailure(int statusCode, Header[] headers, JSONObject response) {
        Log.e(Constants.TAG,Integer.toString(statusCode) + ": " + response.toString());
        Resource error = new Resource(response);
        String msg = error.getAttribute("title", "Request failed with status: " + statusCode);
        return new ResourceWrapper(error, msg);
    }

    public void store(Resource resource, Header[] headers) {
        String href = resource.getLink("self").href();
        mStorage.put(href, resource);
    }
}
