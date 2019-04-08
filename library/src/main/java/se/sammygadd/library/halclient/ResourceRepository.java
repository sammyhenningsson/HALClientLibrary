package se.sammygadd.library.halclient;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;

public class ResourceRepository {
    private Storage mStorage;

    private static ResourceRepository mRepository;

    public static ResourceRepository get(Storage storage) {
        if (mRepository == null) {
            mRepository = new ResourceRepository(storage);
        }
        return mRepository;
    }

    public static ResourceRepository get() {
        return get(null);
    }

    public ResourceRepository(Storage storage) {
        mStorage = storage;
    }

    private ApiService apiService() {
        return ApiService.get();
    }

    private HalResponseHandler getResponseHandler(MutableLiveData<ResourceWrapper> data) {
        return new HalResponseHandler(data, mStorage);
    }

    public LiveData<ResourceWrapper> getResource(String uri) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();
        getCachedOrFreshResource(uri, data);
        return data;
    }

    public LiveData<ResourceWrapper> submitForm(Form form) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();
        submitForm(form, getResponseHandler(data));
        return data;
    }

    private void getCachedOrFreshResource(String uri, MutableLiveData<ResourceWrapper> data) {
        String etag = null;

        if (mStorage.hasResource(uri)) {
            Resource cachedResource = mStorage.get(uri).getResource();
            if (cachedResource.isStale()) {
                etag = cachedResource.getEtag();
            } else {
                Log.d(Constants.TAG, "Returning resource from cache");
                data.setValue(new ResourceWrapper(cachedResource));
                return;
            }
        }

        apiService().get(uri, etag, getResponseHandler(data));
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
            case "DELETE":
                apiService().delete(url, responseHandler);
                break;
        }
    }
}
