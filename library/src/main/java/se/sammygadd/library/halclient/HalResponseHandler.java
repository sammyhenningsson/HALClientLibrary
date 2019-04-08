package se.sammygadd.library.halclient;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.lifecycle.MutableLiveData;
import cz.msebera.android.httpclient.Header;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;

public class HalResponseHandler extends JsonHttpResponseHandler {
    private MutableLiveData<ResourceWrapper>  mData;
    private Storage mStorage;
    private Pattern mMaxAgePattern;

    public HalResponseHandler(MutableLiveData<ResourceWrapper> data, Storage storage) {
        super();
        mData = data;
        mStorage = storage;
        mMaxAgePattern = Pattern.compile(".*max-age=(\\d+).*");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        ResourceWrapper result = processResponse(statusCode, headers, response);
        mData.setValue(result);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        ResourceWrapper result = processResponse(statusCode, headers, response);
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
        ResourceWrapper result = processFailure(statusCode, headers, response);
        mData.setValue(result);
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
        ResourceWrapper result = new ResourceWrapper(resource);

        addCacheInfo(resource, headers);
        store(result, headers);

        return result;
    }

    private ResourceWrapper processResponse(int statusCode, Header[] headers, JSONArray response) {
        Log.i(Constants.TAG, "status: " + statusCode);
        List<Resource> resources = parseResources(headers, response);
        return new ResourceWrapper(resources);
    }

    private ResourceWrapper processFailure(int statusCode, Header[] headers, JSONObject response) {
        if (statusCode == 304) {
            Log.d(Constants.TAG, "Resource has not been modifed. Use cached version!");
            Resource resource = getCached(headers);
            ResourceWrapper result = new ResourceWrapper(resource);

            addCacheInfo(resource, headers);
            store(result, headers);

            return result;
        } else {
            Log.w(Constants.TAG, "Request failed with status: " + statusCode);
            Resource error = parseResource(headers, response);
            return new ResourceWrapper(error);
        }
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

        Log.d(Constants.TAG, "Response: " + resource.toString());
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

    private void addCacheInfo(Resource resource, Header[] headers) {
        if (resource != null && !isError(headers)) {
            String etag = getEtag(headers);
            resource.setEtag(etag);

            Date expireAt = getExpireAt(headers);
            resource.setExpiration(expireAt);
        } else if (resource == null) {
            Log.d(Constants.TAG, "Resource is null!");
        } else {
            Log.d(Constants.TAG, "Resource is error!");
        }
    }

    private String contentType(Header[] headers) {
        return getHeader(headers, "Content-Type", "");
    }

    private String getEtag(Header[] headers) {
        return getHeader(headers, "ETag");
    }

    private Date getExpireAt(Header[] headers) {
        String cacheControl = getHeader(headers, "Cache-Control", "");
        Matcher matcher = mMaxAgePattern.matcher(cacheControl);
        if (!matcher.matches()) {
            return null;
        }
        String maxAge = matcher.group(1);
        Long seconds = Long.parseLong(maxAge);
        Date now = new Date();

        return new Date(now.getTime() + seconds * 1000);
    }

    private String getHeader(Header[] headers, String name) {
        return getHeader(headers, name, null);
    }

    private String getHeader(Header[] headers, String name, String defaultValue) {
        for (int i = 0; i < headers.length; ++i) {
            Header header = headers[i];
            if (header.getName().equals(name)) {
                return header.getValue();
            }
        }
        return defaultValue;
    }

    // FIXME add Authorization header to key
    private Resource getCached(Header[] headers) {
        String uri = getRequestURI().toString();
        if (!mStorage.hasResource(uri)) {
            return null;
        }
        return mStorage.get(uri).getResource();
    }

    // FIXME add Authorization header to key
    private void store(ResourceWrapper wrapper, Header[] headers) {
        String uri = getRequestURI().toString();
        mStorage.put(uri, wrapper);
    }
}
