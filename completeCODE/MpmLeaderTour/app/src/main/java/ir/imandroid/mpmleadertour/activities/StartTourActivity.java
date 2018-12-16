package ir.imandroid.mpmleadertour.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ir.imandroid.mpmleadertour.G;
import ir.imandroid.mpmleadertour.fragments.TimeLineTour;
import ir.imandroid.mpmleadertour.fragments.TourInfo;
import ir.imandroid.mpmleadertour.util.Constant;
import ir.imandroid.mpmleadertour.util.MultiDrawable;
import ir.imandroid.mpmleadertour.service.MyService2;
import ir.imandroid.mpmleadertour.util.OnInfoWindowElemTouchListener;
import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.model.Pin;
import retrofit2.http.PUT;

import static ir.imandroid.mpmleadertour.util.Constant.PAUSE;
import static ir.imandroid.mpmleadertour.util.Constant.PLAY;
import static ir.imandroid.mpmleadertour.util.Constant.PREF_KEY_PIN_SELECTED;
import static ir.imandroid.mpmleadertour.util.Constant.PREF_KEY_STATE;
import static ir.imandroid.mpmleadertour.util.Constant.STATE;
import static ir.imandroid.mpmleadertour.util.Constant.STOP;


public class StartTourActivity extends AppCompatActivity implements OnMapReadyCallback,ClusterManager.OnClusterClickListener<Pin>,
        ClusterManager.OnClusterInfoWindowClickListener<Pin>,
        ClusterManager.OnClusterItemClickListener<Pin>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Pin>,
        View.OnTouchListener{


    private SupportMapFragment mapFragment;
    public Circle mCircle;
    public Marker mMarker;
    FragmentPagerAdapter adapterViewPager;
    List<Pin> locsNearByList;
    ArrayList<String> nearByFiles;
    PagerTabStrip p;
    public Typeface font_irmob_bold;
    ImageView img_auto,img_hamber;
    LinearLayout lyt_shut_down,lyt_about;
    DrawerLayout drawer_layout;

    private static final String TAG = "tag";
    private MyService2.PlayerBinder myPlayerService ;
    private static final String POSITION = "lastpos";

    private SlidingUpPanelLayout mLayout;

    private SeekBar seekbar_sliding;
//    private ImageView img_play_sliding;
    private FABProgressCircle fabProgressCircle;
    private FloatingActionButton fab_play;
    private FloatingActionButton img_stop_sliding;
    private ImageView img_person_sliding;
    private TextView txt_person_sliding,txt_username_nav,txt_register;
    private ViewGroup infoWindow;

    private ClusterManager<Pin> mClusterManager;
    private Random mRandom = new Random(1984);
    private GoogleMap mMap;


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        SeekBar tmpSeekBar = (SeekBar)view;
        myPlayerService.mediaSeekTo(( tmpSeekBar.getProgress() ));
        return false;
    }

    private class PinRenderer extends DefaultClusterRenderer<Pin> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private TextView textView = null;
        private final int mDimension;


        public PinRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            textView = (TextView) multiProfile.findViewById(R.id.amu_text);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Pin pin, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(pin.getPhoto());
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(pin.getTitle());

        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Pin> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Pin p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.getPhoto());
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        G.e.putString(Constant.PREF_KEY_STATE, "0").apply();
        G.e.putString(Constant.PREF_KEY_PIN_SELECTED, "").apply();
        setUpMap();

        img_auto=findViewById(R.id.img_auto);
        img_hamber=findViewById(R.id.img_hamber);
        lyt_shut_down=findViewById(R.id.lyt_shut_down);
        lyt_about=findViewById(R.id.lyt_about);
        drawer_layout=findViewById(R.id.drawer_layout);
        txt_register =  findViewById(R.id.txt_register);
        p= findViewById(R.id.pager_header);
        seekbar_sliding=findViewById(R.id.seekbar_sliding);
        fab_play = findViewById(R.id.fab_play);
        fabProgressCircle = findViewById(R.id.fabProgressCircle);
        font_irmob_bold = Typeface.createFromAsset(getAssets(), "fonts/irmob_bold.ttf");
        nearByFiles=new ArrayList<>();

        locsNearByList =new ArrayList<>();


        p.setTextSize(1,16);
        p.setTabIndicatorColor(Color.WHITE);
        p.setTextColor(Color.WHITE);

        for (int i = 0; i < p.getChildCount(); ++i) {
            View nextChild = p.getChildAt(i);
            if (nextChild instanceof TextView) {
                TextView textViewToConvert = (TextView) nextChild;
                textViewToConvert.setTypeface(font_irmob_bold);
                textViewToConvert.setTextSize(14);
            }

        }

        img_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.getPrefs.getBoolean(Constant.PREF_KEY_AUTO_PLAY,true)){
                    G.e.putBoolean(Constant.PREF_KEY_AUTO_PLAY,false).apply();
                    img_auto.setImageResource(R.drawable.autob);
                }else{
                    G.e.putBoolean(Constant.PREF_KEY_AUTO_PLAY,true).apply();
                    img_auto.setImageResource(R.drawable.auto);
                }
            }
        });

        img_hamber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer_layout.isDrawerOpen(Gravity.LEFT))
                    drawer_layout.openDrawer(Gravity.LEFT);
            }
        });


        lyt_shut_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(StartTourActivity.this)
                        .title("Exit")
                        .content("Are you sure you want to exit ? ")
                        .positiveText("YES")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                drawer_layout.closeDrawer(Gravity.LEFT);
                                buttonStop();
                                finish();
                                System.exit(0);

                            }
                        })
                        .negativeText("NO")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                drawer_layout.closeDrawer(Gravity.LEFT);
                            }
                        })
                        .show();


            }
        });

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartTourActivity.this,LoginActivity.class));
            }
        });

        seekbar_sliding.setOnTouchListener(this);

    }







    @Override
    public boolean onClusterClick(Cluster<Pin> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getTitle();
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 45));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    @Override
    public void onClusterInfoWindowClick(Cluster<Pin> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(Pin item) {
//        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        img_person_sliding.setImageResource(item.getPhoto());

        buttonStop();

        fab_play.setImageResource(R.drawable.ic_play_arrow_white_36dp);

        G.e.putString(Constant.PREF_KEY_PIN_SELECTED,new Gson().toJson(item)).apply();



        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Pin item) {
        // Does nothing, but you could go into the user's profile page, for example.
//        Toast.makeText(this, item.name, Toast.LENGTH_SHORT).show();

    }


    private void drawMarkerWithCircle(LatLng position) {
        double radiusInMeters = 500.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = getMap().addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        mMarker = getMap().addMarker(markerOptions);
    }

    private void setUpMap() {
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
//        Toast.makeText(context, "!!", Toast.LENGTH_SHORT).show();

    }

    protected GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        getMap().getUiSettings().setMyLocationButtonEnabled(true);
        getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                drawMarkerWithCircle(latLng);
            }
        });

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = null;
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, true));
        }else {
            Log.e(TAG, "startMap:locationManager is null ", new Exception().getCause());
        }

        if (location!=null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
        }else {
            Log.e(TAG, "startMap: location is null ", new Exception().getCause());

        }


//        drawMarkerWithCircle(latLng);

        mLayout =  findViewById(R.id.sliding_layout);
        seekbar_sliding =  findViewById(R.id.seekbar_sliding);
        img_person_sliding =  findViewById(R.id.img_person_sliding);
        fab_play =  findViewById(R.id.fab_play);
        fabProgressCircle =  findViewById(R.id.fabProgressCircle);
        img_stop_sliding =  findViewById(R.id.img_stop_sliding);
        txt_person_sliding =  findViewById(R.id.txt_person_sliding);
        txt_username_nav =  findViewById(R.id.txt_username_nav);




        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Play Clicked");

                if (G.getPrefs.getString(Constant.PREF_KEY_PIN_SELECTED , "").length()>0) {
                    if (!getState().equals(PLAY)){
                        Log.i(TAG, "onClick: Play Clicked");
                        buttonPlay();
                        fab_play.setImageResource(android.R.drawable.ic_media_pause);
                    }else{
                        buttonPause();
                        fab_play.setImageResource(android.R.drawable.ic_media_play);

                    }
                }


            }
        });

        img_stop_sliding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop();
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("TAG", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("TAG", "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
//        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
//        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
//        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
//


        getMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                return null;
            }
        });

        mClusterManager = new ClusterManager<Pin>(this, getMap());
        mClusterManager.setRenderer(new PinRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        List<Pin> pinList=new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArrayPoints =obj.getJSONArray("points");
            Log.i("TAG", "jsonArrayPoints-->"+jsonArrayPoints.length());

            LatLng latLngs;
            String title,info,sound,id;
            int idPic;
            for (int i =0; i<jsonArrayPoints.length();i++){
                latLngs=new LatLng(jsonArrayPoints.getJSONObject(i).getDouble("lat")
                        ,jsonArrayPoints.getJSONObject(i).getDouble("lang"));
                title=jsonArrayPoints.getJSONObject(i).getString("title");
                info="about place i "+i;
                sound=jsonArrayPoints.getJSONObject(i).getString("sound");
                id=jsonArrayPoints.getJSONObject(i).getString("id");
                idPic=R.drawable.pp_1;
                pinList.add(new Pin(id,latLngs,title,info,idPic,sound));
                addItem(id , latLngs,title,info,idPic,sound);
            }

            Log.i("TAG", Arrays.toString(pinList.toArray()));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG", "Exception-->"+e.getMessage().toString());

        }

        mClusterManager.cluster();

    }


    public String getState() {
        return G.getPrefs.getString(Constant.PREF_KEY_STATE,"-1");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(playerServiceConnection);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
        set_img_auto();
        updateState();
//        mHandler.removeCallbacks(mRunnable);
//        updateSeekProgress();

//        if(myPlayerService!=null){
//            myPlayerService.play();
//
//        }
    }

    @Override
    protected void onPause() {
//        myPlayerService.stop();
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else  if (drawer_layout.isDrawerOpen(Gravity.LEFT)){

            drawer_layout.closeDrawer(Gravity.LEFT);


        }else {
//            new MaterialDialog.Builder(StartTourActivity.this)
//                    .title("Exit")
//                    .content("Are you sure you want to exit ? ")
//                    .positiveText("YES")
//                    .cancelable(false)
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            drawer_layout.closeDrawer(Gravity.LEFT);
//                            buttonStop();
//                            finish();
//                            System.exit(0);
//
//                        }
//                    })
//                    .negativeText("NO")
//                    .onNegative(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            drawer_layout.closeDrawer(Gravity.LEFT);
//                        }
//                    })
//                    .show();
            super.onBackPressed();
        }
    }

//    private void addItem() {
//        mClusterManager.addItem(new Person(new LatLng(31.356269,48.679877),getString(R.string.LOC1),R.drawable.john));
//        mClusterManager.addItem(new Person(new LatLng(31.35430, 48.679329),getString(R.string.LOC2),R.drawable.ruth));
//        mClusterManager.addItem(new Person(new LatLng(31.350952, 48.678407),getString(R.string.LOC3),R.drawable.dr));
//
//
//    }
private void addItem(String id , LatLng latLng, String title, String info, int img, String sound) {
    mClusterManager.addItem(new Pin(id , latLng,title,info,img,sound));

}
    private void clearItems() {
        mClusterManager.clearItems();

    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }

    private ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            // Store a ref to the service
            myPlayerService = (MyService2.PlayerBinder) service;
//            img_play_sliding = (ImageView) findViewById(R.id.img_play_sliding);
            fab_play =  findViewById(R.id.fab_play);

            // Request the service sends us the current state so UI can be updated
            updateState();
//            changeIcon();
//            myPlayerService.play();


            ViewPager vpPager =  findViewById(R.id.vpPager);
            adapterViewPager = new PagerAdapterMap(getSupportFragmentManager());
            vpPager.setAdapter(adapterViewPager);
            vpPager.setPageTransformer(true, new ScaleInOutTransformer());
            vpPager.setCurrentItem(1);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            myPlayerService = null;
        }
    };
    private void set_img_auto() {
        if (G.getPrefs.getBoolean(Constant.PREF_KEY_AUTO_PLAY,true)){
            img_auto.setImageResource(R.drawable.auto);
        }else {
            img_auto.setImageResource(R.drawable.autob);
        }

    }
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"Call onReceive from Service!");

            set_img_auto();
            // Grab extra vars attached to the broadcast

            if (intent.getAction().equalsIgnoreCase(Constant.BROADCAST_KEY_NOTIFICATION))
            {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    Log.i(TAG,"Receive Bundle Details from Service!");

                    locsNearByList.clear();

                    locsNearByList = Parcels.unwrap(intent.getParcelableExtra("user"));

                    Log.i(TAG,"UPDATE LOC LIST!!!");

                    if (locsNearByList!=null && locsNearByList.size()>0 ) {

                        Log.i(TAG, "First-->" + locsNearByList.get(0).getTitle());

                        clearItems();

                        for (int i=0;i<locsNearByList.size();i++){

                            addItem(locsNearByList.get(i).getId(),
                                    locsNearByList.get(i).getLatLng(),
                                    locsNearByList.get(i).getTitle(),
                                    locsNearByList.get(i).getInfo(),
                                    locsNearByList.get(i).getPhoto(),
                                    locsNearByList.get(i).getSound()

                            );
                        }


                    }


                    String callback = bundle.getString(Constant.BROADCAST_KEY_CALLBACK);

                    int queue = locsNearByList.size()+1;

                    // Perform some UI work depending on callback
                    Boolean nextState = queue > 1;
                    if (callback!=null && !callback.equals("")){

                        switch (callback) {
//                            case PLAY:
//                                fab_play.setImageResource(android.R.drawable.ic_media_pause);
////                        customListAdapter=new CustomListAdapter(StartTourActivity.this,locsNearByList);
////                        listView.setAdapter(customListAdapter);
////                        customListAdapter.notifyDataSetChanged();
//                                if (G.getPrefs.getInt(POSITION, -1)!=-1 && locsNearByList.size()>0) {
//                                    txt_person_sliding.setText(locsNearByList.get(G.getPrefs.getInt(POSITION, -1)).getTitle());
//                                }
////                        textView_status.setText("► " + artist + " - " + title + " (" + queue + ")");
////                        setButtonStates(false, true, nextState, true);
//                                break;
//                            case PAUSE:
//                                fab_play.setImageResource(android.R.drawable.ic_media_play);
//
////                        textView_status.setText("▋▋ " + artist + " - " + title + " (" + queue + ")");
////                        setButtonStates(true, false, nextState, true);
//                                break;
//                            case STOP:
//                                fab_play.setImageResource(android.R.drawable.ic_media_play);
//                                txt_person_sliding = (TextView) findViewById(R.id.txt_person_sliding);
//
//                                txt_person_sliding.setText("");
//
//                                break;

                            case STATE:

                                updateState();

                                break;


                            case Constant.BROADCAST_KEY_SHOW_PROGRESS:
                                showProgressPlaying();

                                break;

                            case Constant.BROADCAST_KEY_HIDE_PROGRESS:
                                hideProgressPlaying();

                                break;
                            case Constant.BROADCAST_KEY_UPDATE_SEEKBAR:
//                                updateSeekProgress();

                                break;

                            case Constant.BROADCAST_KEY_CHECK_AUTOPLAY:

                                if (myPlayerService!=null && myPlayerService.isBinderAlive() && !getState().equals(PLAY) ){

                                    if (G.getPrefs.getBoolean(Constant.PREF_KEY_AUTO_PLAY,true)) {
                                        Log.i(TAG, "onLocationChanged: call setup from service...");
                                        if(locsNearByList !=null && locsNearByList.size()>0){
                                            G.e.putString(Constant.PREF_KEY_PIN_SELECTED,new Gson().toJson(locsNearByList.get(0))).apply();
                                            myPlayerService.play();
                                        }
                                    }


                                }

                                break;



                        }

                    }


                    int seekbarPercentage = bundle.getInt(Constant.BROADCAST_KEY_PERCENTAGE,-1);
                    if (seekbarPercentage!=-1 ){
                        int percentage = (int) (((float) seekbarPercentage / myPlayerService.getLentghAudio()) * 100);
                        seekbar_sliding.setProgress(percentage);
                        Log.i("PercentageTag", "onReceive: " +  percentage);
                    }
//                    int seekbarPercentage2 = bundle.getInt(Constant.BROADCAST_KEY_PERCENTAGE2,-1);
//                    if (seekbarPercentage!=-1){
//                        seekbar_sliding.setSecondaryProgress(seekbarPercentage2);
//                    }


                }


            }



        }
    };



    void updateState(){

        Log.i("UpdateStateMethod", "updateState in StartTourActivity.... ="+G.getPrefs.getString(STATE,"-1"));

        String state = G.getPrefs.getString(PREF_KEY_STATE,"-1");

        if (state.equals(PLAY)){
            Log.i("UpdateStateMethod", "onReceive: Playing " );
            fab_play.setImageResource(android.R.drawable.ic_media_pause);
            if (locsNearByList.size()>0) {
                if (G.getPrefs.getString(Constant.PREF_KEY_PIN_SELECTED , "").length()>0){

                    txt_person_sliding.setText(new Gson().fromJson(G.getPrefs.getString(PREF_KEY_PIN_SELECTED,""),Pin.class).getTitle());
                }
            }
        }else if (state.equals(PAUSE)){
            Log.i("UpdateStateMethod", "onReceive: Pause " );

            fab_play.setImageResource(android.R.drawable.ic_media_play);
        }else if(state.equals(STOP)) {
            Log.i("UpdateStateMethod", "onReceive: Stop " );
            fab_play.setImageResource(android.R.drawable.ic_media_play);
            txt_person_sliding = findViewById(R.id.txt_person_sliding);
            txt_person_sliding.setText("");
        }
        else {
            Log.i("UpdateStateMethod", "onReceive: Else " + STATE);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        Toast.makeText(this, "!", Toast.LENGTH_SHORT).show();
        // Have to start using startService so service isn't killed when this activity unbinds from
        // it onStop
        Intent intent= new Intent(this, MyService2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i(TAG,"onStart--StartTourActivity");
        startService(intent);
        bindService(intent, playerServiceConnection, 0);
        // Register a receiver to the service's callbacks
        registerReceiver(receiver, new IntentFilter(Constant.BROADCAST_KEY_NOTIFICATION));
        changeIcon();
        Log.i(TAG,"size locsNearByList-->"+locsNearByList.size());

        if (locsNearByList.size()>0 && myPlayerService!=null && !myPlayerService.mediaIsPlaying()) {
            clearItems();
            for (int i=0;i<locsNearByList.size();i++){

                addItem(locsNearByList.get(i).getId(),
                        locsNearByList.get(i).getLatLng(),
                        locsNearByList.get(i).getTitle(),
                        locsNearByList.get(i).getInfo(),
                        locsNearByList.get(i).getPhoto(),
                        locsNearByList.get(i).getSound()

                );
            }






//            customListAdapter=new CustomListAdapter(StartTourActivity.this,locsNearByList);
//            listView.setAdapter(customListAdapter);
//            customListAdapter.notifyDataSetChanged();

        }
    }

    public void changeIcon(){
        if(myPlayerService!=null){
            if (!G.getPrefs.getString(STATE,"0").equals(PLAY)){
                fab_play.setImageResource(android.R.drawable.ic_media_pause);
            }else {
                fab_play.setImageResource(android.R.drawable.ic_media_play);
            }
        }

    }

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
    public void buttonPlay() {

        if (G.getPrefs.getString(Constant.PREF_KEY_PIN_SELECTED , "").length()>0) {
            myPlayerService.play();
            set_img_auto();
        }

    }
    public void buttonPause() {
        myPlayerService.pause();
        set_img_auto();

    }
//    public void buttonNext(View v) {
//        myPlayerService.next();
//        set_img_auto();
//
//    }
    public void buttonStop() {
        G.e.putString(Constant.PREF_KEY_PIN_SELECTED, "").apply();
        G.e.putString(Constant.PREF_KEY_STATE, STOP).apply();
        myPlayerService.stop();
        fab_play.setImageResource(android.R.drawable.ic_media_play);
        set_img_auto();

    }

//    void songSelected(Pin loc) {
//        myPlayerService.setup(loc);
//    }


    void showProgressPlaying() {
        fabProgressCircle.show();
    }

    void hideProgressPlaying() {
        fabProgressCircle.hide();
    }



    public  class PagerAdapterMap extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 2;

        public PagerAdapterMap(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment

                    return   new TourInfo();

                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return TimeLineTour.newInstance(myPlayerService);

                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence title="";
            switch (position)
            {
                case 0:

                    title= "Info";
                    break;
                case 1:
                    title= "Nearby";
                    break;


            }
            return title;

        }

        @Override
        public int getItemPosition(@NonNull Object object) {


            return super.getItemPosition(object);
        }
    }

}