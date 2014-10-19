package me.sumwu.heartbeat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
* Created by justin on 10/18/14.
*/
public class EndomondoApiUsage {

    static int workoutBpm = 0;

    public static int workoutBpm() {
        EndomondoApi.get("/1/workouts", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
                workoutBpm = parseJson(response);
            }
        });

        return workoutBpm;
    }

    private static int parseJson(org.json.JSONObject workoutResults) {
        int[] parsedWorkoutResults = new int[2];
//        JSONObject data = workoutResults.get("data")[0];
//        parsedWorkoutResults[0] = data.get("steps_total");
//        parsedWorkoutResults[1] = data.get("duration_total");
//        return calculatePace(parsedWorkoutResults);
        return 5;
    }

    private static int calculatePace(int[] paceData) {
        int workoutMinutes = paceData[1] / 60;
        return (int) paceData[0] / workoutMinutes;
    }

}
