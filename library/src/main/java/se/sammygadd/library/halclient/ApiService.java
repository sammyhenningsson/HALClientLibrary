package se.sammygadd.library.halclient;

import android.content.Context;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class ApiService {
    private Context mContext;
    private AsyncHttpClient mClient;

    private static ApiService mService;

    public static void create(Context context) {
        if (mService == null) {
            context = context.getApplicationContext();
            mService = new ApiService(context);
        }
    }

    public static ApiService get() {
        return mService;
    }

    private ApiService(Context context) {
        mContext = context;
        mClient = new AsyncHttpClient();
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        mClient.get(url, responseHandler);
    }

    public void post(String url, HttpEntity body, String contentType, AsyncHttpResponseHandler responseHandler) {
        mClient.post(mContext, url, body, contentType, responseHandler);
    }

    public void put(String url, HttpEntity body, String contentType, AsyncHttpResponseHandler responseHandler) {
        mClient.put(mContext, url, body, contentType, responseHandler);
    }

    public void patch(String url, HttpEntity body, String contentType, AsyncHttpResponseHandler responseHandler) {
        mClient.patch(mContext, url, body, contentType, responseHandler);
    }

    public void delete(String url, Header[] headers, AsyncHttpResponseHandler responseHandler) {
        mClient.delete(mContext, url, headers, responseHandler);
    }
}
