package me.sumwu.heartbeat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by justin on 10/18/14.
 */
public class EndomondoApi {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(java.lang.String url,
                          RequestParams params,
                          ResponseHandlerInterface responseHandler) {
        // url should be "/1/workouts"
        client.get(url, params, responseHandler);
    }

    public static void post(java.lang.String url,
                            RequestParams params,
                            ResponseHandlerInterface responseHandler) {
        client.post(url, params, responseHandler);
    }

}