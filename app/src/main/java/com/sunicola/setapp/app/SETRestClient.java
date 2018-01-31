package com.sunicola.setapp.app;

/**
 * Created by soaresbo on 31/01/2018.
 */

import com.loopj.android.http.*;

public class SETRestClient {
    private static final String BASE_URL = "http://sccug-330-04.lancs.ac.uk:8000/api/v1/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
