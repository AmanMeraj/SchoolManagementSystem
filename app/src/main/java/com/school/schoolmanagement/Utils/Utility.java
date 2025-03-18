package com.school.schoolmanagement.Utils;

import static android.os.Build.VERSION_CODES.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility extends AppCompatActivity {

    public SharedPref pref = new SharedPref();
    public String device_type = "Android";


    public void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }


//    @SuppressLint("HardwareIds")
//    public int getDeviceId(){
//        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//    }

    public String getVersionName(Context c){
        String version="";
        try {
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public static boolean isNumberOnly(String val){
        if(TextUtils.isDigitsOnly(val)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

//    public void setBottomNavigationColor(){
//        //bottom navigation bar color
//        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
//    }

    public boolean isInternetConnected(Context con) {
        boolean result =false;
        if(con!=null) {
            ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                result = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
            if (!result) {
                showToastGrey(con, "No Internet Connection");
            }
        }
        return result;
    }

    public void dialNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }


    public void showToastGrey(Context c , String msg){
        Toast toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        if (Build.VERSION.SDK_INT < R) {
            //view.setBackgroundResource(R.drawable.bg_toast_grey);
            TextView text = view.findViewById(android.R.id.message);
            //text.setTextColor(getResources().getColor(R.color.white));
        }
        toast.show();
    }

    public void showAlert(Context c,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    //do things
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public String capsFirstCharacter(String s){
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public boolean isDeveloperOptionsEnabled() {
        ContentResolver resolver = getContentResolver();
        try {
            int devOptionsEnabled = Settings.Global.getInt(resolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED);
            return devOptionsEnabled == 1; // Developer Options is enabled
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false; // Default to false if setting not found
        }
    }

    public void showDeveloperOptionsAlert() {
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new androidx.appcompat.app.AlertDialog.Builder(Utility.this)
                        .setTitle("Developer Options Enabled")
                        .setMessage("Developer Options is currently enabled. Please disable it in your device settings to continue using the app.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                            openDeveloperOptions(); // Open Developer Options in settings
                        })
                        .show();
            }
        },1500);

    }

    private void openDeveloperOptions() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Fall back to the main settings page if Developer Options is not available
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
