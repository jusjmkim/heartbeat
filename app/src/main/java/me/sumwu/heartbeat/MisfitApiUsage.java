package me.sumwu.heartbeat;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
* Created by justin on 10/18/14.
*/
public class MisfitApiUsage {

    static int workoutBpm = 0;
    static RequestParams params = new RequestParams();
    static Context mainContext;
    static String misfitKey;
    static String misfitSecret;
    static String misfitRedirectUri;

    private static void throwException(Throwable e) {
        System.out.println("merp. MisfitApiUsage failed");
        throw new RuntimeException(e);
    }

    public static int workoutBpm() {
            MisfitApi.get("/move/resource/v1/user/me/activity/sessions", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
                    workoutBpm = parseJson(response);
                }

                @Override
                public void onFailure (int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    throwException(e);
                }
            });

        return workoutBpm;
    }

    private static int parseJson(org.json.JSONObject workoutResults) {
        int[] parsedWorkoutResults = new int[2];
        System.out.println("hello world");
//        System.out.println(workoutResults.get("sessions"));
//        JSONObject data = workoutResults.get("sessions")[0];
//        parsedWorkoutResults[0] = data.get("steps");
//        parsedWorkoutResults[1] = data.get("duration");
//        return calculatePace(parsedWorkoutResults);
        return 5;
    }

    private static int calculatePace(int[] paceData) {
        int workoutMinutes = paceData[1] / 60;
        return (int) paceData[0] / workoutMinutes;
    }

    public static void authenticate(Context context) {
        setContext(context);
        String authorizeUrl = "/auth/dialog/authorize?response_type=code" +
                "&redirect_uri=" + misfitRedirectUri +
                "&client_id=" + misfitKey +
                "&scope=public";
        System.out.println(authorizeUrl);
        MisfitApi.get(authorizeUrl, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess (int statusCode, Header[] headers, byte[] response) {
                System.out.println("The authorization data is ");
                System.out.println(response);
                getAccessToken(response);
            }

            @Override
            public void onFailure (int statusCode, Header[] headers, byte[] response, Throwable e) {
                throwException(e);
            }
        });
    }

    private static void setContext(Context context) {
        mainContext = context;
        misfitKey = mainContext.getString(R.string.misfit_key);
        misfitSecret = mainContext.getString(R.string.misfit_secret);
        misfitRedirectUri = mainContext.getString(R.string.misfit_redirect_uri);
    }

    private static void getAccessToken(byte[] code) {
            String accessTokenUrl = "/auth/tokens/exchange";
            MisfitApi.post(accessTokenUrl,
                    accessTokenParams(code), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess (int statusCode, Header[] headers, byte[] response) {
                            System.out.println(response);
//                configureParams(response);
                        }

                        @Override
                        public void onFailure (int statusCode, Header[] headers, byte[] response, Throwable e) {
                            throwException(e);
                        }
                    });
    }

    private static RequestParams accessTokenParams(byte[] code) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("grant_type", "authorization_code");
        requestParams.put("code", code);
        requestParams.put("client_id", misfitKey);
        requestParams.put("redirect_uri", misfitRedirectUri);
        requestParams.put("client_secret", misfitSecret);

        return requestParams;
    }

    private static void configureParams(JSONObject response) {
//        String access_token = response.get("access_token").toString();
//        System.out.println("The Misfit access token is " + access_token);
//        params.put("access_token", access_token);
    }

}