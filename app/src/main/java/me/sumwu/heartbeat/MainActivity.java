package me.sumwu.heartbeat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    ArrayList<String> playlist;

    // Music Dealers
    String token;
    int bpm;

    // Spotify
    private Player mPlayer;

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

        // Music Dealers
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

    public void authenticate() {
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

    public void spotifyConnect(View view) {
        LinearLayout auth = (LinearLayout) findViewById(R.id.auth);
        auth.setVisibility(View.GONE);

        SpotifyAuthentication.openAuthWindow(getString(R.string.spotify_client_id), "token", getString(R.string.spotify_redirect_uri), new String[]{"user-read-private", "streaming"}, null, this);
    }

    /*
        MUSIC DEALER + SPOTIFY
     */
    public void findMusic(View view) {
        Log.i("Heartbeat BPM", "" + bpm);

        // Find list of songs with the right bpm with Music Dealer Api
        Header[] headers = {new BasicHeader("X-Auth-Token", token)};
        RequestParams md_params = new RequestParams();
        md_params.put("bpm_min", bpm - 5);
        md_params.put("bpm_max", bpm + 5);
        MusicDealersApi.post(getApplicationContext(), "/songs", headers, md_params, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println(response);

                try {
                    JSONArray songs = response.getJSONArray("results");
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject song = songs.getJSONObject(i);
                        //System.out.println(song.get("title"));
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

        // Find EchoNest
        RequestParams en_params = new RequestParams();
        en_params.put("api_key", getString(R.string.echonest_api_key));
        en_params.put("genre", "dance+pop");
        en_params.put("format", "json");
        en_params.put("results", 20);
        en_params.put("type", "genre-radio");
        EchoNestApi.post("/playlist/basic", en_params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                super.onFailure(statusCode, headers, errorResponse, e);
                System.out.println(errorResponse);
            }
        });


//        // Find list of songs from Spotify
//        RequestParams spotify_params = new RequestParams();
//        spotify_params.put("type", "track");
//        spotify_params.put("limit", 1);
//        spotify_params.put("q", "burn");
//        SpotifyApi.get("/search", spotify_params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                System.out.println(response);
//
//                try {
//                    JSONArray songs = response.getJSONArray("items");
//                    String song_id = songs.getJSONObject(0).getString("id");
//                    Log.i("burn id", song_id);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
//                super.onFailure(statusCode, headers, errorResponse, e);
//                System.out.println(errorResponse);
//            }
//        });

    }

    /*
        SPOTIFY PLAYER
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());
            mPlayer = spotify.getPlayer(this, "Heartbeat", this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addPlayerNotificationCallback(MainActivity.this);
                    mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    mPlayer.pause();
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MainActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    public void togglePlayPause(View view) {
        // Get current state of play/pause button
        boolean off = ((ToggleButton) view).isChecked();

        if (off) {
            // Play Song
            mPlayer.resume();
        } else {
            // Pause Music
            mPlayer.pause();
        }
    }

    public void playNextSong() {
        mPlayer.skipToNext();
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
