package com.example.fifol.projectcandy.Screens;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.fifol.projectcandy.R;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */


public class LoginScreen extends BaseActivity {

    private EditText emailInput, passInput;
    private ProgressBar progressBar = null;
    // Boolean that represent the checkbox.
    private boolean rememberMeStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        emailInput = findViewById(R.id.emailInputLogin);
        passInput = findViewById(R.id.passInputLogin);
        progressBar = findViewById(R.id.indeterminateBar);
        findViewById(R.id.door).setOnClickListener(doorClicked);
    }

     public void loginBtnClick(View view) {
        makeSoundEnter();

       if (inputValidation.isInputEmpty(emailInput) || inputValidation.isInputEmpty(passInput)) {
            showToast("Please fill all fields");
        }else if (!inputValidation.isEmailValid(emailInput)) {
            showToast("illegal Email");
        }else if (!inputValidation.onlyDigitsAndLetters(passInput)) {
           showToast("Name And Password can contain only letters and digits");
        }else if (!checkConnectivity()) {
            showToast("You don't have internet access");
        }else {

            progressBar.setVisibility(View.VISIBLE);
            Backendless.UserService.login(emailInput.getText().toString(), passInput.getText().toString(), new AsyncCallback<BackendlessUser>() {

                public void handleResponse(BackendlessUser user) {
                    backendlessHelper.prepareUser(user);
                    showToast(backendlessHelper.getName() + " Connected");
                    moveToActivity(LoggedUserScreen.class, progressBar);
                }

                public void handleFault(BackendlessFault fault) {
                    progressBar.setVisibility(View.GONE);
                    showToast("Incorrect Email Or Password");
                }
            }, rememberMeStatus);
        }
    }

    public void rememberMeChange(View view) {
        makeSoundEnter();
        rememberMeStatus = !rememberMeStatus;
    }

    public void moveToRegistrationBtn(View view) {
        moveToActivity(RegistrationScreen.class);
    }

    @Override
    public void onBackPressed() {
        quitDialog();
    }


}

