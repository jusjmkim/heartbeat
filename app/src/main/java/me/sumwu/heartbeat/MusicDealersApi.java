package me.sumwu.heartbeat;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by swu on 10/17/14.
 */
public class MusicDealersApi {

    private static final String BASE_URL = "https://api.musicdealers.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(android.content.Context context,
                            java.lang.String url,
                            org.apache.http.Header[] headers,
                            RequestParams params,
                            java.lang.String contentType,
                            ResponseHandlerInterface responseHandler) {
        client.post(context, getAbsoluteUrl(url), headers, params, contentType, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}