package se.sammygadd.library.halclient;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import cz.msebera.android.httpclient.Header;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;

public class HalResponseHandler extends JsonHttpResponseHandler {
    public interface SuccessCallback {
        void onSuccess(ResourceWrapper result, Header[] headers);
    }

    private MutableLiveData<ResourceWrapper>  mData;
    private SuccessCallback mCallback;

    public HalResponseHandler(MutableLiveData<ResourceWrapper> data, SuccessCallback callback) {
        super();
        mData = data;
        mCallback = callback;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        ResourceWrapper result = processResponse(statusCode, headers, response);
        mCallback.onSuccess(result, headers);
        mData.setValue(result);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        ResourceWrapper result = processResponse(statusCode, headers, response);
        mCallback.onSuccess(result, headers);
        mData.setValue(result);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        Log.w(Constants.TAG, statusCode + ": Unsupported response (" + contentType(headers) + ")");
        Log.w(Constants.TAG, "Response: " + responseString);
        mData.setValue(null);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
        processFailure(statusCode, headers, response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray response) {
        Log.w(Constants.TAG, statusCode + ": Unsupported response (" + contentType(headers) + ")");
        Log.w(Constants.TAG, "Response: " + response.toString());
        mData.setValue(null);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.w(Constants.TAG, statusCode + ": Unsupported response (" + contentType(headers) + ")");
        Log.w(Constants.TAG, "Response: " + responseString);
        mData.setValue(null);
    }

    private ResourceWrapper processResponse(int statusCode, Header[] headers, JSONObject response) {
        Log.i(Constants.TAG, "status: " + statusCode);
        Resource resource = parseResource(headers, response);
        return new ResourceWrapper(resource);
    }

    private ResourceWrapper processResponse(int statusCode, Header[] headers, JSONArray response) {
        Log.i(Constants.TAG, "status: " + statusCode);
        List<Resource> resources = parseResources(headers, response);
        return new ResourceWrapper(resources);

    }

    private void processFailure(int statusCode, Header[] headers, JSONObject response) {
        Log.w(Constants.TAG, "request failed with status: " + statusCode);
        Resource error = parseResource(headers, response);
        mData.setValue(new ResourceWrapper(error));
    }

    private Resource parseResource(Header[] headers, JSONObject response) {
        if (response == null || response.length() == 0) {
            Log.w(Constants.TAG, "No response body!");
            return null;
        }

        Resource resource;

        if (isForm(headers)) {
            resource = new Form(response);
        } else if (isError(headers)) {
            if (isValidationError(response)) {
                resource = new ValidationError(response);
            } else {
                resource = new Error(response);
            }
        } else if (isJSON(headers)) {
            resource = new Resource(response);
        } else {
            Log.w(Constants.TAG, "Unsupported response (" + contentType(headers) + ")");
            return null;
        }

        Log.d(Constants.TAG, "response: " + resource.toString());
        return resource;
    }

    private List<Resource> parseResources(Header[] headers, JSONArray response) {
        List<Resource> resources = new ArrayList<>();

        for (int i = 0; i < response.length(); ++i) {
            JSONObject json = response.optJSONObject(i);
            if (json == null) continue;
            Resource resource = parseResource(headers, json);
            resources.add(resource);
        }

        return resources;
    }

    private boolean isForm(Header[] headers) {
        return contentType(headers).contains("profile=shaf-form");
    }

    private boolean isError(Header[] headers) {
        return contentType(headers).contains("profile=shaf-error");
    }

    private boolean isJSON(Header[] headers) {
        return contentType(headers).matches("application/(.*)json");
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
}
