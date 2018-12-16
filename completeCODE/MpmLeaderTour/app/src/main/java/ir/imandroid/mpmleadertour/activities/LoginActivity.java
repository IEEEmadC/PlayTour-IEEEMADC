package ir.imandroid.mpmleadertour.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ir.imandroid.mpmleadertour.R;

public class LoginActivity extends AppCompatActivity {
    ImageView img_hamber;
    DrawerLayout drawer_layout;
    TextView txt_register,txt_username_nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        img_hamber=findViewById(R.id.img_hamber);
        drawer_layout=findViewById(R.id.drawer_layout);
        txt_register=findViewById(R.id.txt_register);
        txt_username_nav=findViewById(R.id.txt_username_nav);
        txt_register.setVisibility(View.INVISIBLE);
        txt_username_nav.setVisibility(View.INVISIBLE);
        img_hamber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer_layout.isDrawerOpen(Gravity.LEFT))
                    drawer_layout.openDrawer(Gravity.LEFT);
            }
        });


    }
}
