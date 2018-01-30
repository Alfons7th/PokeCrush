package com.example.fifol.projectcandy.Screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.example.fifol.projectcandy.BackendlessHelper;
import com.example.fifol.projectcandy.InputValidation;
import com.example.fifol.projectcandy.R;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class BaseActivity extends AppCompatActivity {

    protected InputValidation inputValidation = new InputValidation();
    protected BackendlessHelper backendlessHelper = BackendlessHelper.getInstance();
    protected SoundPool soundPool;
    protected int btnSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);
        btnSound = soundPool.load(this, R.raw.btn,1);
        hideBars();
       }

    protected void moveToActivity(Class otherActivity){
        makeSoundEnter();
        Intent i = new Intent(this,otherActivity);
        startActivity(i);
    }

    protected void moveToActivity(Class otherActivity,ProgressBar progressBar){
        progressBar.setVisibility(View.GONE);
        Intent i = new Intent(this,otherActivity);
        startActivity(i);
    }

    protected void showToast(String text){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
    }

    protected boolean checkConnectivity(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideBars();
    }

    protected void hideBars() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    protected void quitDialog() {
        makeSoundEnter();
        View view = getLayoutInflater().inflate(R.layout.dialog_two_btn, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();

        Button rightBtn = view.findViewById(R.id.rightBtn);
        rightBtn.setTextColor(Color.parseColor("#e7e20808"));
        view.findViewById(R.id.rightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("You Rock!");
                dialog.dismiss();
            }
        });
        Button leftBtn = view.findViewById(R.id.leftBtn);
        leftBtn.setTextColor(Color.parseColor("#e7149628"));
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAllActivities();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
               showToast("You Rock!");
            }
        });
    }

    protected void finishAllActivities(){
        this.finishAffinity();
    }

    View.OnClickListener doorClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            quitDialog();
        }
    };

    public void makeSoundEnter(){
        soundPool.play(btnSound,1.0f,1.0f,1,0,1.0f);
    }
}
