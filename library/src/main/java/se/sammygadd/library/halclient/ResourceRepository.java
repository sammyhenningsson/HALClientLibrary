package se.sammygadd.library.halclient;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import java.util.HashMap;

public class ResourceRepository {
    private HashMap<String, Resource> mStorage;

    private static ResourceRepository mRepository;

    public static ResourceRepository get() {
        if (mRepository == null) {
            mRepository = new ResourceRepository();
        }
        return mRepository;
    }

    public ResourceRepository() {
        mStorage = new HashMap<>();
    }

    private ApiService apiService() {
        return ApiService.get();
    }

    public LiveData<ResourceWrapper> getResource(String uri) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();
        apiService().get(uri, getResponseHandler(data));
        return data;
    }

    public LiveData<ResourceWrapper> submitForm(Form form) {
        MutableLiveData<ResourceWrapper> data = new MutableLiveData<>();
        submitForm(form, getResponseHandler(data));
        return data;
    }

    private HalResponseHandler getResponseHandler(MutableLiveData<ResourceWrapper> data) {
        return new HalResponseHandler(data, (result, headers) -> {
            if (!result.isArray() && !result.isError()) {
                store(result.getResource(), headers);
            }
        });
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

    public void store(Resource resource, Header[] headers) {
        String href = resource.getLink("self").href();
        mStorage.put(href, resource);
    }
}
