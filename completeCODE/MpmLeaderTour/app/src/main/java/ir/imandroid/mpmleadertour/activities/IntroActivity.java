package ir.imandroid.mpmleadertour.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;

import java.util.ArrayList;
import java.util.List;

import ir.imandroid.mpmleadertour.R;

public class IntroActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    Button btnEndIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();


        constraintLayout = findViewById(R.id.constraintLayout);
        btnEndIntro = findViewById(R.id.btnFinishIntro);
        PaperOnboardingPage scr1 = new PaperOnboardingPage("You Are Not Alone",
                "",
                Color.parseColor("#ce3e43"), R.drawable.application_map_icon, R.drawable.onboarding_pager_circle_icon);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Cool Experience With Cool Guides",
                "",
                Color.parseColor("#65B0B4"), R.drawable.appicns_safari, R.drawable.onboarding_pager_circle_icon);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("No Stranger In PlayTour",
                "",
                Color.parseColor("#007A6C"), R.drawable.logo_tour, R.drawable.onboarding_pager_circle_icon);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);


        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();


        onBoardingFragment.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int i, int i1) {
                if (i1 == 2) {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    btnEndIntro.setVisibility(View.VISIBLE);
                } else {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    btnEndIntro.setVisibility(View.GONE);
                }
            }
        });

        btnEndIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this, StartTourActivity.class));
                finish();
            }
        });


    }
}