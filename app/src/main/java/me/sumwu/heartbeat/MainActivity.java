package me.sumwu.heartbeat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    String token;
    int bpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMaxValue(180);
        np.setMinValue(50);
        np.setValue(100);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                bpm = newVal;
            }
        });

        bpm = np.getValue();

        authenticate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void authenticate() {
        // Authenticate with Music Dealer Api
        RequestParams login_params = new RequestParams();
        login_params.put("username", getString(R.string.md_username));
        login_params.put("password", getString(R.string.md_password));
        MusicDealersApi.post(getApplicationContext(), "/authentication/login", null, login_params, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200"
                try {
                    token = response.get("token").toString();
                    System.out.println(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println(errorResponse);
            }
        });
    }

    public void findMusic(View view) {
        Log.i("Heartbeat BPM", "" + bpm);

        // Find list of songs with the right bpm with Music Dealer Api
        Header[] headers = {new BasicHeader("X-Auth-Token", token)};
        RequestParams bpm_params = new RequestParams();
        bpm_params.put("bpm_min", bpm - 5);
        bpm_params.put("bpm_max", bpm + 5);
        MusicDealersApi.post(getApplicationContext(), "/songs", headers, bpm_params, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println(response);

                try {
                    JSONArray songs = response.getJSONArray("results");
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject song = songs.getJSONObject(i);
                        System.out.println(song.get("title"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                super.onFailure(statusCode, headers, errorResponse, e);
                System.out.println(errorResponse);
            }
        });

    }

    public void playMusic(String title) {
        // Use SpotifyApi to play music
    }
}
