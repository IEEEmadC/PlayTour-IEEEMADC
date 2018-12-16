package ir.imandroid.mpmleadertour.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.ui.shimmer.Shimmer;
import ir.imandroid.mpmleadertour.ui.shimmer.ShimmerFrameLayout;

public class SplashActivity extends AppCompatActivity {
    ShimmerFrameLayout shimmer_view_container;
    private Thread mSplashThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        shimmer_view_container=findViewById(R.id.shimmer_view_container);
        Shimmer.AlphaHighlightBuilder shimmer =new Shimmer.AlphaHighlightBuilder();

        shimmer.setRepeatMode(ValueAnimator.REVERSE);



        mSplashThread = new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        wait(3000);
                    }
                }
                catch(InterruptedException ex){
                }

                finish();
                // Run next activity
                Intent intent = new Intent(getBaseContext(), IntroActivity.class);
                startActivity(intent);
//                Splash3Activity.this.overridePendingTransition(R.anim.float_button_scale_unhide, R.anim.float_button_scale_hide);

            }
        };

        mSplashThread.start();

    }
}
