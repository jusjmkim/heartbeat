package me.sumwu.heartbeat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.Random;

public class MainActivity extends ActionBarActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    ArrayList<String[]> en_potential_songs = new ArrayList<String[]>();
    ArrayList<String[]> md_potential_songs = new ArrayList<String[]>();
    ArrayList<String> current_playlist = new ArrayList<String>();
    ArrayList<String> final_playlist = new ArrayList<String>();

    TextView seekbar_bpm;
    private SeekBar seekbar = null;

    // Music Dealers
    String token;
    int bpm;

    // Spotify
    private Player mPlayer;
    boolean paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekbar = (SeekBar) findViewById(R.id.slider);
        seekbar_bpm = (TextView) findViewById(R.id.bpm);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress + 50;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                seekbar_bpm.setText(progressChanged + " bpm");
                bpm = progressChanged;
                mPlayer.clearQueue();
                findMusic();
            }
        });

        // initialize beats per minute and paused music
        bpm = 120;
        paused = true;
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

    public void mdConnect(View view) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Connecting");
        progress.setMessage("Just a few more seconds...");
        progress.show();

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
                progress.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println(errorResponse);
            }
        });
    }

    public void spotifyConnect(View view) {
        RelativeLayout auth = (RelativeLayout) findViewById(R.id.auth);
        auth.setVisibility(View.GONE);

        SpotifyAuthentication.openAuthWindow(getString(R.string.spotify_client_id), "token", getString(R.string.spotify_redirect_uri), new String[]{"user-read-private", "streaming"}, null, this);
    }

    /*
        GENERATING THE PLAYLIST
     */
    public void findMusic() {
        // Find list of songs with the right bpm with Music Dealer Api
        Header[] headers = {new BasicHeader("X-Auth-Token", token)};
        RequestParams md_params = new RequestParams();
        md_params.put("bpm_min", bpm - 5);
        md_params.put("bpm_max", bpm + 5);
        MusicDealersApi.post(getApplicationContext(), "/songs", headers, md_params, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // Get all songs with the required bpm
                try {
                    JSONArray songs = response.getJSONArray("results");
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject song = songs.getJSONObject(i);
                        JSONObject artist = song.getJSONObject("artist");

                        String[] potential_song = {song.get("title").toString(), artist.get("name").toString()};
                        md_potential_songs.add(potential_song);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Determine which of those songs are available on Spotify
                for (int i = 0; i < md_potential_songs.size(); i++) {
                    String title = md_potential_songs.get(i)[0];
                    String artist = md_potential_songs.get(i)[1];
                    checkSpotifyAvailability(title, artist);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                super.onFailure(statusCode, headers, errorResponse, e);
                System.out.println(errorResponse);
            }
        });
        // Create playlist with EchoNest Api
        RequestParams en_params = new RequestParams();
        en_params.put("api_key", getString(R.string.echonest_api_key));
        en_params.put("min_tempo", bpm - 5);
        en_params.put("max_tempo", bpm + 5);
        en_params.put("results", 20);
        en_params.put("min_energy", 0.8);
        en_params.put("song_min_hotttnesss", 0.8);
        EchoNestApi.get("song/search", en_params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                // extract names and artists from echonest
                try {
                    JSONArray songs = response.getJSONObject("response").getJSONArray("songs");
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject song = songs.getJSONObject(i);
                        String[] potential_song = {song.get("title").toString(), song.get("artist_name").toString()};
                        en_potential_songs.add(potential_song);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Determine which of those songs are available on Spotify
                for (int i = 0; i < en_potential_songs.size(); i++) {
                    String title = en_potential_songs.get(i)[0];
                    String artist = en_potential_songs.get(i)[1];
                    checkSpotifyAvailability(title, artist);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                super.onFailure(statusCode, headers, errorResponse, e);
                System.out.println("ECHONEST ERROR" + errorResponse);
            }
        });

    }

    // CHECK WHICH SONGS FROM MUSIC DEALERS AND ECHONEST ARE AVAILABLE ON SPOTIFY
    private void checkSpotifyAvailability(final String title, final String artist) {
        // Find list of songs from Spotify
        RequestParams spotify_params = new RequestParams();
        spotify_params.put("type", "track");
        spotify_params.put("limit", 1);
        spotify_params.put("q", title);
        SpotifyApi.get("search", spotify_params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONArray items = response.getJSONObject("tracks").getJSONArray("items");

                    JSONArray spotify_artists = items.getJSONObject(0).getJSONArray("artists");
                    String spotify_artist = spotify_artists.getJSONObject(0).get("name").toString();
                    if (!artist.equals(spotify_artist)) { return; }

                    String song_id = items.getJSONObject(0).get("id").toString();
                    current_playlist.add(song_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                super.onFailure(statusCode, headers, errorResponse, e);
                System.out.println("SPOTIFY ERROR" + errorResponse);
            }
        });

    }

    // Update queue with playlists
    private void updateQueue() {
        long seed = System.nanoTime();
        Collections.shuffle(current_playlist, new Random(seed));
        for (int i = 0; i < current_playlist.size(); i++) {
            mPlayer.queue("spotify:track:" + current_playlist.get(i));
        }
        en_potential_songs = new ArrayList<String[]>();
        md_potential_songs = new ArrayList<String[]>();
        current_playlist = new ArrayList<String>();
        Log.i("queue", current_playlist.toString());
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

        // Select workout
        startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
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
        ImageView selection = (ImageView) view;

        updateQueue();

        // Get current state of play/pause button
        if (paused) {
            // Play Song
            mPlayer.resume();
            selection.setImageResource(R.drawable.f_pause_button);
            paused = false;
        } else {
            // Pause Music
            mPlayer.pause();
            selection.setImageResource(R.drawable.e_play_button);;
            paused = true;
        }
    }

    public void playNextSong(View view) {
        mPlayer.skipToNext();
    }

    public void finish(View view) {
        Intent intent = new Intent(getApplicationContext(), EndActivity.class);
        intent.putExtra("AVERAGE_BPM", bpm);
        intent.putStringArrayListExtra("PLAYLIST", final_playlist);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}