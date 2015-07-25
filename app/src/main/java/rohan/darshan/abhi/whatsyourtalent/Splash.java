package rohan.darshan.abhi.whatsyourtalent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;


public class Splash extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences("SignIn", Context.MODE_PRIVATE);
                int check = sharedPreferences.getInt("Yes", 0);

                if (!haveNetworkConnection()) {
                    callDialog();
                } else {
                    if (check == 0) {
                        Intent i = new Intent(getApplicationContext(), SignIn.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(getApplicationContext(), TimeLineActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                //delete this and uncomment below one


                /*SharedPreferences sharedPreferences = getSharedPreferences("SignIn", Context.MODE_PRIVATE);
                int check = sharedPreferences.getInt("Yes", 0);
                if (check == 0) {
                    Intent i = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), TimeLineActivity.class);
                    startActivity(i);
                    finish();
                }*/
            }
        }.start();

    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void callDialog() {
        new AlertDialog.Builder(this).setTitle("Warning!").setMessage("Not connected to Internet")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
//                        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Splash.this.finish();
            }
        }).setCancelable(false).show();
    }
}
