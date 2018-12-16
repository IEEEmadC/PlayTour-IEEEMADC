package ir.imandroid.mpmleadertour.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


import ir.imandroid.mpmleadertour.G;
import ir.imandroid.mpmleadertour.R;
import ir.imandroid.mpmleadertour.interfaces.Service;
import ir.imandroid.mpmleadertour.util.Constant;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BaseAppCompatActivity extends AppCompatActivity {
    public Animation slideLeft, slideRight ,fade;
//    public  ACProgressPie dialog;
//    public  AlertDialog dialog;
    public AlertDialog dialog;
    public Vibrator vibrator;
    public Typeface irmob;
    public String username,name,family ,token,pid,org_id;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
    public Service service;
    public  static SharedPreferences getPrefs;
    public  static SharedPreferences.Editor e;
    HttpLoggingInterceptor interceptor;
    OkHttpClient client;
    Gson gson =new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        irmob = Typeface.createFromAsset(getAssets(), "fonts/irmob.ttf");

        interceptor = new HttpLoggingInterceptor();
          interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
          client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        service = new Retrofit.Builder()
                .baseUrl("http://91.106.95.190:1398")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(Service.class);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
       getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
       e = getPrefs.edit();



    }
    public  void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
    public void update_shared_preferences() {
        name=getPrefs.getString("name","");
        family=getPrefs.getString("family","");
        pid=getPrefs.getString("pid","");
        token=getPrefs.getString("token","");
        org_id=getPrefs.getString("org_id","");
    }
    public String versionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        return version;
    }

    public int versionNumber() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = pInfo.versionCode;
        return version;
    }
    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }
    public void logUser() {

        username=getPrefs.getString("username","defusername");
        name=getPrefs.getString("name","defname");
        family=getPrefs.getString("family","deffamily");

    }


    public boolean isNetCheck() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            return true ;
        } else {
            return false;
        }
    }


public void showpDialog(Context context) {
    try{
        AlertDialog.Builder  builder =new AlertDialog.Builder(context);
        LayoutInflater layoutInflater =getLayoutInflater();

        builder.setView(layoutInflater.inflate(R.layout.loading,null));

         dialog =builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }
    catch (Exception e)
    {
        //nothing
        Log.i("HidePDialog",e+"");
    }


}


    public void hidepDialog() {

        try {

            dialog.dismiss();

        }catch (Exception e)
        {
            //nothing
            Log.i("HidePDialog",e+"");
        }



    }

//    public  void toast(String msg){
//        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
//    }




    public void warningDialog(AppCompatActivity context, String title, String content){
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText("باشه")
                .show();
//        new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
//                .setTitleText(title)
//                .setContentText(content)
//                .setConfirmText(getString(R.string.ok))
//                .show();
    }

    public void successDialog(String title, String content){
        new MaterialDialog.Builder(getApplicationContext())
                .title(title)
                .content(content)
                .positiveText("باشه")
                .show();
//        new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
//                .setTitleText(title)
//                .setContentText(content)
//                .setConfirmText(getString(R.string.ok))
//                .show();
    }

    public void answers(String category){

//        Answers.getInstance().logContentView(new ContentViewEvent()
//                .putContentName(category)
//                .putCustomAttribute("name And Family", name+" "+family)
//                .putCustomAttribute("Username", username)
//                .putCustomAttribute("User Phone Date", "Time:"+sdf.format(new Date()))
//                .putCustomAttribute("Android Version :","Api Version :"+android.os.Build.VERSION.SDK_INT)
//                .putCustomAttribute("device Name", android.os.Build.MODEL)
//                .putCustomAttribute("Manufacturer", android.os.Build.MANUFACTURER)
//
//        );
    }


    public  void toast(String text){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.lyt_toast_root));
        TextView txt_toast = (TextView) layout.findViewById(R.id.txt_toast);
        txt_toast.setText(text);
        Toast toast =new Toast(G.context);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.AXIS_X_SHIFT, 10,10);
        toast.show();
    }




}

