package me.sumwu.heartbeat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by swu on 10/18/14.
 */
public class EchoNestApi {

    private static final String BASE_URL = "http://developer.echonest.com/api/v4/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(java.lang.String url,
                            RequestParams params,
                            ResponseHandlerInterface responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
        System.out.println("just submitted a get request for echonestapi so this should be working");
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
