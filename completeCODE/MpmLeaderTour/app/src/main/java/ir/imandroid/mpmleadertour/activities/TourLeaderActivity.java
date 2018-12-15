package ir.imandroid.mpmleadertour.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ir.imandroid.mpmleadertour.R;

public class TourLeaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_leader);
        Typeface typeface =Typeface.createFromAsset(getAssets(), "fonts/irmob_bold.ttf");

        Button btn_start = findViewById(R.id.btn_start);
        btn_start.setTypeface(typeface);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TourLeaderActivity.this,StartTourActivity.class));

            }
        });
        findViewById(R.id.rate_intro).setClickable(false);
        findViewById(R.id.rate_intro).setFocusableInTouchMode(false);
    }
}
