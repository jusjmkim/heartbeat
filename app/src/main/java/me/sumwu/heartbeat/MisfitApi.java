package me.sumwu.heartbeat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by justin on 10/18/14.
 */
public class MisfitApi {

    private static final String BASE_URL = "https://api.misfitwearables.com";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(java.lang.String url,
                          RequestParams params,
                          ResponseHandlerInterface responseHandler) {
        // url should be "/move/resource/v1/user/me/activity/sessions"
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) { return BASE_URL + relativeUrl;}

}

//todo set up api keys