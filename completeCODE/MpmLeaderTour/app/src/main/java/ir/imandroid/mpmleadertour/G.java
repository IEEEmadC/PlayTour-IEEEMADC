package ir.imandroid.mpmleadertour;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.backtory.java.BuildConfig;
import com.backtory.java.internal.BacktoryClient;
import com.backtory.java.internal.KeyConfiguration;
import com.backtory.java.internal.LogLevel;

import ir.imandroid.mpmleadertour.util.Constant;


public class G extends Application {
    public static Context context;
    public  static SharedPreferences getPrefs;
    public  static SharedPreferences.Editor e;
    public static final String TAG = G.class.getSimpleName();
    public String radius;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        e = getPrefs.edit();
        MultiDex.install(this);
//        radius = getPrefs.getString(Constant.PREF_KEY_RADIUS,Constant.DEFAULT_RADIUS);


    }
}
