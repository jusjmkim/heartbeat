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

    private static final String BASE_URL = "https://api.endomondo.com/api";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static int get(java.lang.String url,
                          RequestParams params,
                          ResponseHandlerInterface responseHandler) {
        client.get(getAbsoluteUrl("/1/workouts"), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) { return BASE_URL + relativeUrl;}

    private static int parseJson(org.json.JSONObject workoutResults) {
        int[] parsedWorkoutResults = new int[2];
        JSONObject data = parsedWorkoutResults.get("data")[0];
        parsedWorkoutResults[0] = data.get("steps_total");
        parsedWorkoutResults[1] = data.get("duration_total");
        return calculatePace(parsedWorkoutResults);
    }

    private static int calculatePace(int[] paceData) {
        int workoutMinutes = paceData[1] / 60;
        return (int) paceData[0] / workoutMinutes;
    }

}

//todo set up api keys