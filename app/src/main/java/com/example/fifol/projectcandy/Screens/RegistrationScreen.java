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

public class RegistrationScreen extends BaseActivity {

    private EditText emailInput,passInput,nameInput;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailInput = findViewById(R.id.emailInputRegister);
        passInput = findViewById(R.id.passInputRegister);
        nameInput = findViewById(R.id.nameInputRegister);

        progressBar = findViewById(R.id.indeterminateBar);
        findViewById(R.id.door).setOnClickListener(doorClicked);
    }

    public void registerBtnClick(View view) {
        makeSoundEnter();
        if(!checkConnectivity()){
            showToast("You don't have internet access");
            return;
        }
        if (inputValidation.isInputEmpty(emailInput) || inputValidation.isInputEmpty(passInput) || inputValidation.isInputEmpty(nameInput)) {
            showToast("Please fill all fields");
        }else if (!inputValidation.isEmailValid(emailInput)) {
            showToast("illegal Email");
        }else if (!inputValidation.onlyDigitsAndLetters(nameInput)||!inputValidation.onlyDigitsAndLetters(passInput)) {
            showToast("Name And Password can contain only letters and digits");
        }else {
            progressBar.setVisibility(View.VISIBLE);
            BackendlessUser userInfo = new BackendlessUser();
            userInfo.setEmail(emailInput.getText().toString());
            userInfo.setPassword(passInput.getText().toString());
            userInfo.setProperty("name", nameInput.getText().toString());

            Backendless.UserService.register(userInfo, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(BackendlessUser registeredUser) {
                    showToast("RegistrationScreen Succeeded");
                    moveToActivity(LoginScreen.class, progressBar);
                }

                public void handleFault(BackendlessFault fault) {
                    progressBar.setVisibility(View.GONE);
                    showToast("Email already exist");
                }
            });
        }
    }

    public void moveToLogin(View view) {
        moveToActivity(LoginScreen.class);
    }

    @Override
    public void onBackPressed() {
        moveToActivity(LoginScreen.class);
    }
}
