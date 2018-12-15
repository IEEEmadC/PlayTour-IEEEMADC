package ir.imandroid.mpmleadertour.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

import ir.imandroid.mpmleadertour.G;
import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.model.Pin;
import ir.imandroid.mpmleadertour.ui.slideShow.AutoPlayManager;
import ir.imandroid.mpmleadertour.ui.slideShow.ImageIndicatorView;
import ir.imandroid.mpmleadertour.util.Constant;



public class TourInfo extends Fragment  {

    // TODO: Customize parameter argument names
    private static final String ARG_PIN = "PIN";
    // TODO: Customize parameters
    private String mParam ;
    Pin pin;
    Gson gson =new Gson();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TourInfo() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TourInfo newInstance(String title) {
        TourInfo fragment = new TourInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PIN, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PIN);
//            Type collectionType = new TypeToken<Collection<Pin>>(){}.getType();
//            pin = gson.fromJson(mParam,Pin.class);



        }
    }

    TextView txt_desc_tour;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_tour_info, container, false);
        final ViewPager vpPager = (ViewPager) getActivity().findViewById(R.id.vpPager);



        txt_desc_tour=view.findViewById(R.id.txt_desc_tour);
        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pin = gson.fromJson(G.getPrefs.getString(Constant.PREF_KEY_PIN_SELECTED,""),Pin.class);
                if (pin!=null){
                    txt_desc_tour.setText(pin.getInfo());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        txt_desc_tour.setText(getPrefs.getString(Constant.PREF_KEY_INFO_PIN_SELECTED,""));

        // Set the adapter
//        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            ImageIndicatorView imageIndicatorView = (ImageIndicatorView) view.findViewById(R.id.indicate_view);

            final Integer[] resArray = new Integer[] { R.drawable.bgs1, R.drawable.bgs2 ,
                    R.drawable.bgs3};

            imageIndicatorView.setupLayoutByDrawable(resArray);
            imageIndicatorView.setIndicateStyle(ImageIndicatorView.INDICATE_ARROW_ROUND_STYLE);
            imageIndicatorView.show();
            AutoPlayManager autoBrocastManager =  new AutoPlayManager(imageIndicatorView);
            autoBrocastManager.setBroadcastEnable(true);
            autoBrocastManager.setBroadCastTimes(5);//loop times
            autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);//set first play time and interval
            autoBrocastManager.loop();
//        }
        return view;
    }



}
