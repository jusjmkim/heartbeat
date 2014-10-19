//package me.sumwu.heartbeat;
//
//import android.content.Context;
//
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//import com.loopj.android.http.ResponseHandlerInterface;
//import com.loopj.android.http.JsonHttpResponseHandler;
//
//import org.apache.http.Header;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//
///**
//* Created by justin on 10/18/14.
//*/
//public class EndomondoApiUsage {
//
//    static int workoutBpm = 0;
//    static RequestParams params = new RequestParams();
//    static Context mainContext;
//    static String endomondoKey;
//    static String endomondoSecret;
//    static String endomondoRedirectUri;
//
//    private static void throwException(Throwable e) {
//        System.out.println("merp. endomondoApiUsage failed");
//        throw new RuntimeException(e);
//    }
//
//    public static int workoutBpm() {
//        EndomondoApi.get("/move/resource/v1/user/me/activity/sessions", params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
//                workoutBpm = parseJson(response);
//            }
//
//            @Override
//            public void onFailure (int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                throwException(e);
//            }
//        });
//
//        return workoutBpm;
//    }
//
//    private static int parseJson(org.json.JSONObject workoutResults) {
//        int[] parsedWorkoutResults = new int[2];
//        System.out.println("hello world");
////        System.out.println(workoutResults.get("sessions"));
////        JSONObject data = workoutResults.get("sessions")[0];
////        parsedWorkoutResults[0] = data.get("steps");
////        parsedWorkoutResults[1] = data.get("duration");
////        return calculatePace(parsedWorkoutResults);
//        return 5;
//    }
//
//    private static int calculatePace(int[] paceData) {
//        int workoutMinutes = paceData[1] / 60;
//        return (int) paceData[0] / workoutMinutes;
//    }
//
//    public static void request_token (Context context) {
//        setContext(context);
//        String authorizeUrl = "https://api.endomondo.com/oauth/request_token";
//        System.out.println(authorizeUrl);
//        EndomondoApi.get(authorizeUrl, null, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess (int statusCode, Header[] headers, byte[] response) {
//                System.out.println("The authorization data is ");
//                System.out.println(response);
//                authorize(response);
//            }
//
//            @Override
//            public void onFailure (int statusCode, Header[] headers, byte[] response, Throwable e) {
//                throwException(e);
//            }
//        });
//    }
//
//    private static void setContext(Context context) {
//        mainContext = context;
//        endomondoKey = mainContext.getString(R.string.endomondo_key);
//        endomondoSecret = mainContext.getString(R.string.endomondo_secret);
//        endomondoRedirectUri = mainContext.getString(R.string.endomondo_redirect_uri);
//    }
//
//    private static void authorize (byte[] code) {
//        String accessTokenUrl = "https://api.endomondo.com/oauth/authorize";
//        EndomondoApi.post(accessTokenUrl,
//                accessTokenParams(code), new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess (int statusCode, Header[] headers, byte[] response) {
//                        System.out.println(response);
//                        getAccessTokens(response);
//                    }
//
//                    @Override
//                    public void onFailure (int statusCode, Header[] headers, byte[] response, Throwable e) {
//                        throwException(e);
//                    }
//                });
//    }
//
//    private static RequestParams accessTokenParams(byte[] code) {
//        RequestParams requestParams = new RequestParams();
//        requestParams.put("client_id", endomondoKey);
//        requestParams.put("client_secret", endomondoSecret);
//
//        return requestParams;
//    }
//
//    private static void getAccessTokens(JSONObject response) {
////        String access_token = response.get("access_token").toString();
////        System.out.println("The Endomondo access token is " + access_token);
////        params.put("access_token", access_token);
//    }
//
//}
//
