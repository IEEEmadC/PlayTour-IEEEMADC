package ir.imandroid.mpmleadertour.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.activities.StartTourActivity;
import ir.imandroid.mpmleadertour.adapter.TimeLineAdapter;
import ir.imandroid.mpmleadertour.model.Pin;

import ir.imandroid.mpmleadertour.service.MyService2;
import ir.imandroid.mpmleadertour.util.Constant;



public class TimeLineTour extends Fragment {



    public TimeLineTour() {
    }


    public static TimeLineTour newInstance(MyService2.PlayerBinder playerBinder) {
        TimeLineTour fragment = new TimeLineTour();
//        Bundle args = new Bundle();
//
//
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }else {
            Log.i("mytagggg", "getArguments is null ");

        }
    }

    BroadcastReceiver receiver;
    List<Pin> locsNearByList;
    TimeLineAdapter timeLineAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_line, container, false);
        locsNearByList = new ArrayList<>();
        final ViewPager vpPager = (ViewPager) getActivity().findViewById(R.id.vpPager);

        final RecyclerView recycle_timeline=view.findViewById(R.id.recycle_timeline);
        recycle_timeline.setLayoutManager(new LinearLayoutManager(getActivity()));
        timeLineAdapter = new TimeLineAdapter(locsNearByList, (AppCompatActivity) getActivity(), vpPager, new TimeLineAdapter.OnItemClicked() {
            @Override
            public void onClick() {

                ((StartTourActivity)getActivity()).buttonStop();
                ((StartTourActivity)getActivity()).buttonPlay();
            }
        });
        recycle_timeline.setAdapter(timeLineAdapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    Log.i("tagggg","Receive Bundle Details from Service!");
                    // Extract vars
                    locsNearByList.clear();

                    locsNearByList = Parcels.unwrap(intent.getParcelableExtra("user"));

                    Log.i("tagggg","locsNearByList size="+locsNearByList.size());

                    timeLineAdapter = new TimeLineAdapter(locsNearByList, (AppCompatActivity) getActivity(), vpPager, new TimeLineAdapter.OnItemClicked() {
                        @Override
                        public void onClick() {
                            ((StartTourActivity)getActivity()).buttonStop();
                            ((StartTourActivity)getActivity()).buttonPlay();

                        }
                    });
                    recycle_timeline.setAdapter(timeLineAdapter);
                }

            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(Constant.BROADCAST_KEY_NOTIFICATION));






        return view;
    }

//    public List<String> dummy(){
//        List<String> strings =new ArrayList<>();
//
//        for (int i=0;i<getResources().getStringArray(R.array.title_timeline).length;i++){
//            strings.add(getResources().getStringArray(R.array.title_timeline)[i]);
//        }
//        return strings;
//    }



    @Override
    public void onDestroyView() {
        if (receiver!=null){
            getActivity().unregisterReceiver(receiver);
        }

        super.onDestroyView();
    }
}
