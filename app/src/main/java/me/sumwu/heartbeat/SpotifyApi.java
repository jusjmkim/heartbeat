package me.sumwu.heartbeat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by swu on 10/18/14.
 */
public class SpotifyApi {

    private static final String BASE_URL = "https://api.spotify.com/v1/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(java.lang.String url,
                           RequestParams params,
                           ResponseHandlerInterface responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


}
