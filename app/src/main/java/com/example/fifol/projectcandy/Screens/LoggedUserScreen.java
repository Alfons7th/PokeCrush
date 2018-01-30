package com.example.fifol.projectcandy.Screens;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.fifol.projectcandy.R;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class LoggedUserScreen extends BaseActivity {

    private String name;
    private boolean firstEnter;
    private TextView userNameTtl,welcomeTtl;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);

        progressBar = findViewById(R.id.indeterminateBar);
        userNameTtl = findViewById(R.id.nameTtl);
        welcomeTtl = findViewById(R.id.welcome);
        findViewById(R.id.door).setOnClickListener(doorClicked);

        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        name = backendlessHelper.getName();
        firstEnter = backendlessHelper.isFirstEnter();
        userNameTtl.setText("-"+ name+"-");
        if(firstEnter)welcomeMsgDialog();
    }

    public void logoutClick(View view) {
        makeSoundEnter();
        progressBar.setVisibility(View.VISIBLE);
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                showToast(name+" has logged out");
                moveToActivity(LoginScreen.class,progressBar);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progressBar.setVisibility(View.GONE);
                showToast("Error - logout failed");
            }
        });
    }

    @Override
    public void onBackPressed() {
        quitDialog();
    }

    public void moveToLevelSelect(View view) {
        moveToActivity(LevelSelectScreen.class);
    }

    private void setFonts(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nice-font.ttf");
        welcomeTtl.setTypeface(typeface);
        userNameTtl.setTypeface(typeface);
    }

    //This Dialog appears when the player first enter this screen.
    private void welcomeMsgDialog(){
        makeSoundEnter();
        View view = getLayoutInflater().inflate(R.layout.welcomemsgdialog, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();
        view.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firstEnter = false;
                backendlessHelper.setFirstEnter(false);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }
}

