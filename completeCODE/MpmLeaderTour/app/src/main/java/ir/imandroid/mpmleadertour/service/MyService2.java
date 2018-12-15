package ir.imandroid.mpmleadertour.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ir.imandroid.mpmleadertour.G;
import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.activities.StartTourActivity;
import ir.imandroid.mpmleadertour.model.Pin;
import ir.imandroid.mpmleadertour.util.Constant;
import ir.imandroid.mpmleadertour.util.Utility;

import static ir.imandroid.mpmleadertour.util.Constant.PAUSE;
import static ir.imandroid.mpmleadertour.util.Constant.PLAY;
import static ir.imandroid.mpmleadertour.util.Constant.PREF_KEY_PIN_SELECTED;
import static ir.imandroid.mpmleadertour.util.Constant.PREF_KEY_STATE;
import static ir.imandroid.mpmleadertour.util.Constant.STATE;
import static ir.imandroid.mpmleadertour.util.Constant.STOP;



public class MyService2 extends Service {
    public static final String RADIUS = "radius";
    private static final String TAG = "TAG";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final CharSequence CHANNEL_NAME = "CHANNEL_NAME";
    private LocationManager mLocationManager = null;
    private int lengthOfAudio;

    List<Pin> locList;
    List<Pin> nearbyPins;

    float distance = 0;
    Player player;
    int flag;


    public MediaPlayer mediaPlayer;
    //    protected String state = "0";
    public Pin pinSong;
    public int LOCATION_INTERVAL;
    public float LOCATION_DISTANCE;
    private final Binder binder = new PlayerBinder();
    private Handler handler;

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//
//        return START_STICKY;
//    }

    //release resources when unbind
//    @Override
//    public boolean onUnbind(Intent intent){
//        mediaPlayer.stop();
//        mediaPlayer.reset();
//        mediaPlayer.release();
//        return false;
//    }


    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
        Log.i(TAG, "onCreate: PREF_KEY_STATE set to 0 ! ");
        G.e.putString(Constant.PREF_KEY_STATE, "0").apply();
        LOCATION_DISTANCE = G.getPrefs.getFloat(Constant.PREF_KEY_MIN_DIST, 5);
        LOCATION_INTERVAL = G.getPrefs.getInt(Constant.PREF_KEY_MIN_TIME, 5000);
        G.e.putBoolean(Constant.PREF_KEY_AUTO_PLAY, true).apply();

        Log.e(TAG, "onCreate");
        locList = new ArrayList<>();
        nearbyPins = new ArrayList<>();

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    public void initMusicPlayer() {
        //set player properties
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                sendSeekBarPercentage2(i);
            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:

                        sendNotification(Constant.BROADCAST_KEY_SHOW_PROGRESS);

                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        sendNotification(Constant.BROADCAST_KEY_HIDE_PROGRESS);

                        break;
                }
                return true;
            }
        });
    }

    Runnable mUpdateTimeTask = new Runnable() {//http://androidtime.net/android/android-service/
        public void run() {
            long totalDuration = 0;
            long currentDuration = 0;

            try {
                totalDuration = mediaPlayer.getDuration();
                currentDuration = mediaPlayer.getCurrentPosition();
//                textViewSongTime.get().setText(Utility.milliSecondsToTimer(currentDuration) + "/" + Utility.milliSecondsToTimer(totalDuration)); // Displaying time completed playing
                int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDuration));
//                songProgressBar.get().setProgress(progress);	/* Running this thread after 100 milliseconds */
//                progressBarHandler.postDelayed(this, 100);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onDestroy() {
//        startService(new Intent(this,MyService2.class));
        Log.e(TAG, "onDestroy");
//        setBackground();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArrayPoints = obj.getJSONArray("points");
            LatLng latLng;
            String title, info, sound, id;
            int idPic;
            for (int i = 0; i < jsonArrayPoints.length(); i++) {
                id = String.valueOf(i);
                latLng = new LatLng(jsonArrayPoints.getJSONObject(i).getDouble("lat")
                        , jsonArrayPoints.getJSONObject(i).getDouble("lang"));
                title = jsonArrayPoints.getJSONObject(i).getString("title");
                info = " abouut plcae  " + title;
                sound = jsonArrayPoints.getJSONObject(i).getString("sound");
                idPic = R.drawable.pp_1;
                locList.add(new Pin(id, latLng, title, info, idPic, sound));
            }

            Log.i("TAG", Arrays.toString(locList.toArray()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setBackground() {
        // Return the service to the background so it can die
        stopForeground(true);
    }

    protected void sendNotification(String notification) {
        Intent intent = new Intent(Constant.BROADCAST_KEY_NOTIFICATION);
        intent.putExtra(Constant.BROADCAST_KEY_CALLBACK, notification);

        intent.putExtra("user", Parcels.wrap(nearbyPins));

        sendBroadcast(intent);
    }

    protected void sendSeekBarPercentage(int notification) {
        Intent intent = new Intent(Constant.BROADCAST_KEY_NOTIFICATION);
        intent.putExtra(Constant.BROADCAST_KEY_PERCENTAGE, notification);
        intent.putExtra("user", Parcels.wrap(nearbyPins));
        sendBroadcast(intent);
    }


    protected void sendSeekBarPercentage2(int notification) {
        Intent intent = new Intent(Constant.BROADCAST_KEY_NOTIFICATION);
        intent.putExtra(Constant.BROADCAST_KEY_PERCENTAGE2, notification);
        intent.putExtra("user", Parcels.wrap(nearbyPins));
        sendBroadcast(intent);
    }

    void setForeground() {
        // Build a notification to use with startForeground to keep this service running


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = CHANNEL_ID;
            CharSequence channelName = CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            Intent notificationIntent = new Intent(this, StartTourActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, channelId).setContentTitle("mp3player")
                    .setContentText("Music is playing")
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);

        } else {
            Intent notificationIntent = new Intent(this, StartTourActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this).setContentTitle("mp3player")
                    .setContentText("Music is playing")
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
        }

    }

    public class PlayerBinder extends Binder {
        Pin pin;
        int position = 0;
//
//        public void setup(Pin pin) {
//            Log.i(TAG, "setup: service.... pinName="+pin.getTitle());
//
//            this.pin =pin;
//
//
//
//
//                setForeground();
//               this.play();
//        }

        public boolean mediaIsPlaying() {

            if (mediaPlayer != null) {
                return mediaPlayer.isPlaying();
            }
            return false;
        }


        public void mediaSeekTo(int value) {

            mediaPlayer.seekTo((lengthOfAudio / 100) * value);

        }

        public boolean mediaIsNull() {
            return mediaPlayer == null;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public int getLentghAudio() {
            return lengthOfAudio;
        }

        public void play() {
            // Check if we can play
            Log.i(TAG, "Button play pressed!");

            if (G.getPrefs.getString(Constant.PREF_KEY_STATE, "0").equals(PAUSE)) {
                Log.i("MyService222", "PREF_KEY_STATE --> PAUSE: ");
                if (mediaPlayer != null) {
                    mediaPlayer.start();

                    //                updateSeekProgress();
                    sendNotification(Constant.BROADCAST_KEY_UPDATE_SEEKBAR);
                    G.e.putString(Constant.PREF_KEY_STATE, PLAY).apply();
                    sendNotification(STATE);
                }


            } else {
                Log.i("MyService222", "play: ");
                mediaPlayer = new MediaPlayer();
                setForeground();
                pin = new Gson().fromJson(G.getPrefs.getString(Constant.PREF_KEY_PIN_SELECTED, ""), Pin.class);
                if (pin != null) {
                    if (player != null) {
                        player.cancel(true);
                    }
                    player = new Player();
                    player.execute(pin.getSound());
                    Log.i(TAG, "playSound: " + pin.getTitle());
                    G.e.putString(Constant.PREF_KEY_STATE, PLAY).apply();
                    sendNotification(STATE);

                } else {
                    Log.i(TAG, "play: pin is null");
                }

            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    Pin pin = new Gson().fromJson(G.getPrefs.getString(PREF_KEY_PIN_SELECTED, ""), Pin.class);
                    stop();
                    for (int i = 0; i < nearbyPins.size(); i++) {
                        if (pin.getId().equals(nearbyPins.get(i).getId())) {
                            if (i != nearbyPins.size() - 1) {

                                next(i);
                            }
                        }
                    }

                }
            });


        }

        public void pause() {
//            autoPlay=false;
            G.e.putBoolean(Constant.PREF_KEY_AUTO_PLAY, false).apply();

            Log.i(TAG, "Button pause pressed!");

            if (mediaPlayer != null && G.getPrefs.getString(Constant.PREF_KEY_STATE, "0").equals(PLAY)) {
                mediaPlayer.pause();
                G.e.putString(Constant.PREF_KEY_STATE, PAUSE).apply();

            }
            sendNotification(STATE);
        }

        public void stop() {
            Log.i(TAG, "Button stop pressed!");
//            autoPlay=false;
            G.e.putBoolean(Constant.PREF_KEY_AUTO_PLAY, false).apply();
//            if (player!=null){
//                player.cancel(true);
//            }
            if (mediaPlayer != null) {
                // Stop and get rid of the playerMediaPlayer

                handler.removeCallbacksAndMessages(null);
                sendSeekBarPercentage(0);
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                setBackground();
            }

            // Set our state and notify the UI
            G.e.putString(Constant.PREF_KEY_STATE, STOP).apply();

            sendNotification(STATE);


        }

        public void next(int previusPosition) {


            if (nearbyPins.size() != 0) {
                G.e.putString(PREF_KEY_PIN_SELECTED, new Gson().toJson(nearbyPins.get(previusPosition + 1))).apply();
                play();
            } else {
                stop();
            }


        }

    }


    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            Boolean prepared;

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(params[0]);
                mediaPlayer.prepare();
                lengthOfAudio = mediaPlayer.getDuration();

                prepared = true;

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            sendNotification(Constant.BROADCAST_KEY_SHOW_PROGRESS);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
//            sendNotification(Constant.BROADCAST_KEY_HIDE_PROGRESS);
//            if (aBoolean){
//                flag=1;
//            }
//            else {
//                flag=0;
//            }
            if (mediaPlayer != null) {
                mediaPlayer.start();
                // Send callback to UI
                G.e.putString(Constant.PREF_KEY_STATE, PLAY).apply();

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendSeekBarPercentage(mediaPlayer.getCurrentPosition());
                        Log.i(TAG, "run: Runnung");
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
                sendNotification(STATE);
                sendNotification(Constant.BROADCAST_KEY_UPDATE_SEEKBAR);
            }


        }
    }
//
//    public   Handler handler  = new Handler();
//
//
//    private void updateSeekProgress() {
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
////                seekbar_sliding.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));
//                sendSeekBarPercentage((int) (((float) mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));
//
//                handler.postDelayed(new Runnable()
//                {
//                    @Override
//                    public void run() {
//                        updateSeekProgress();
//
//                    }
//                }, 1000);
//            }
//        }
//    }


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;


        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            if (!G.getPrefs.getString(PREF_KEY_STATE, "-1").equals(PLAY)) {

                Log.i(TAG, "onLocationChanged call: ...");

                PlayerBinder playerBinder = new PlayerBinder();
                Location loc = new Location("");
                nearbyPins.clear();
                for (int i = 0; i < locList.size(); i++) {
                    loc.setLatitude(locList.get(i).getLatLng().latitude);
                    loc.setLongitude(locList.get(i).getLatLng().longitude);

                    distance = location.distanceTo(loc);
                    locList.get(i).setDistnace(distance);
                    Log.e(TAG, "فاصله ی شما تا محدوده ی " + locList.get(i).getTitle() + " برابر است با " + "= " + distance);


                    if (distance < G.getPrefs.getInt(RADIUS, Constant.DEFAULT_RADIUS)) {
                        nearbyPins.add(locList.get(i));
                        Log.e(TAG, "شما توی محدوده ی " + Math.round(distance) + " متری " + locList.get(i).getTitle() + " هستی !");
                    }
                }


                Log.e(TAG, "onLocationChanged: " + location);
//            Log.e(TAG, "Distance from point is = " + distance);
                Collections.sort(nearbyPins, new Comparator<Pin>() {
                    @Override
                    public int compare(Pin o1, Pin o2) {
                        return (int) o1.getDistnace() - (int) o2.getDistnace();
                    }
                });

                mLastLocation.set(location);

//            boolean b=playerBinder.mediaIsPlaying();
                sendNotification(Constant.BROADCAST_KEY_CHECK_AUTOPLAY);
                sendNotification(Constant.BROADCAST_KEY_NOTIFICATION);
                Log.i(TAG, "onLocationChanged: Location Changed");
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("latlongs.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}