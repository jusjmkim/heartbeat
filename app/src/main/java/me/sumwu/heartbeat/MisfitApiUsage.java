package me.sumwu.heartbeat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by justin on 10/18/14.
 */
public class MisfitApiUsage {

    public static int workoutBpm() throws JSONException {
        MisfitApi.get("/move/resource/v1/user/me/activity/sessions", null, new ResponseHandlerInterface()) {
            @Override
            public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
                return parseJson(response);
            }
        }
    }

    private static int parseJson(org.json.JSONObject workoutResults) {
        int[] parsedWorkoutResults = new int[2];
        JSONObject data = parsedWorkoutResults.get("sessions")[0];
        parsedWorkoutResults[0] = data.get("steps");
        parsedWorkoutResults[1] = data.get("duration");
        return calculatePace(parsedWorkoutResults);
    }

    private static int calculatePace(int[] paceData) {
        int workoutMinutes = paceData[1] / 60;
        return (int) paceData[0] / workoutMinutes;
    }

}
