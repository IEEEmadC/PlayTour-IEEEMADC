package ir.imandroid.mpmleadertour.adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.google.gson.Gson;

import java.util.List;

import ir.imandroid.mpmleadertour.G;
import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.fragments.TourInfo;
import ir.imandroid.mpmleadertour.model.Pin;
import ir.imandroid.mpmleadertour.service.MyService2;
import ir.imandroid.mpmleadertour.util.Constant;



public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private final List<Pin> mValues;
//    private final OnListFragmentInteractionListener mListener;
    private AppCompatActivity  context;

    ViewPager viewPager ;
    private OnItemClicked onItemClicked;


    public TimeLineAdapter(List<Pin> items, AppCompatActivity context ,ViewPager viewPager , OnItemClicked onItemClicked) {
        this.mValues = items;
//        this.mListener = listener;
        this.context = context;
        this.viewPager = viewPager;
        Intent intent= new Intent(context, MyService2.class);
        this.onItemClicked = onItemClicked;
//        context.bindService(intent, playerServiceConnection, 0);
        Log.i("TimeLineAdapter", "TimeLineAdapter: items size = "+items.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_time_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Pin pin = mValues.get(position);
        holder.text_timeline_title.setText(pin.getTitle());
        holder.txt_distance.setText((int)pin.getDistnace()+ " متر ");

        if (mValues.get(position).getDistnace()<1000){

            holder.timelineView.setMarkerColor(Color.GREEN);

        }else {

            holder.timelineView.setMarkerColor(Color.RED);

        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson =new Gson();
                G.e.putString(Constant.PREF_KEY_PIN_SELECTED,gson.toJson(mValues.get(position))).apply();
                viewPager.setCurrentItem(0);
                //TODO PLAY MUSIC
                onItemClicked.onClick();
            }
        });
        Pin selectedPin = new Gson().fromJson(G.getPrefs.getString(Constant.PREF_KEY_PIN_SELECTED , "") , Pin.class);
        if (mValues.get(position).getId().equals(selectedPin.getId())) {

            holder.cardView.setCardBackgroundColor(Color.parseColor("#2dc100"));
        }
    }



    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction =context.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public  TextView text_timeline_title;
        public  TextView txt_distance;
        public TimelineView timelineView;
        public String title;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            text_timeline_title = view.findViewById(R.id.text_timeline_title);
            timelineView = view.findViewById(R.id.time_marker);
            txt_distance = view.findViewById(R.id.txt_distance);
            cardView = view.findViewById(R.id.card_view);

//            timelineView.setEndLine(Color.RED,viewType);



        }

        @Override
        public String toString() {
            return super.toString() + " '" + text_timeline_title.getText() + "'";
        }

    }

//    private ServiceConnection playerServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d("tag", "onServiceConnected");
//            // Store a ref to the service
//            myPlayerService = (MyService2.PlayerBinder) service;
//
//            // Request the service sends us the current state so UI can be updated
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.d("tag", "onServiceDisconnected");
//            myPlayerService = null;
//        }
//    };


    public interface OnItemClicked{
        void onClick();
    }

}
