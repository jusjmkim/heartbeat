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
 * Created by justin on 10/18/14.
 */
public class MisfitApi {


    private static final String BASE_URL = "https://api.misfitwearables.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static int get(android.content.Context context,
                          java.lang.String url,
                          org.apache.http.Header[] headers,
                          RequestParams params,
                          java.lang.String contentType,
                          ResponseHandlerInterface responseHandler) {
        client.get(context, getAbsoluteUrl(url), headers, params, contentType, responseHandler)
    }

    //This should be abstracted into a separate class, so all the API files can access it
    private static String getAbsoluteUrl(String relativeUrl) { return BASE_URL + relativeUrl;}

    private static int parseJson(org.json.JSONObject workoutResults) {
        int[] parsedWorkoutResults = new int[2];
        parsedWorkoutResults[0] = ("steps", workoutResults.get("steps"));
        parsedWorkoutResults[1] = ("distance", workoutResults.get("distance"));
        return calculatePace(parsedWorkoutResults);
    }

    private static int calculatePace(int[] paceData) {
        
    }

}
