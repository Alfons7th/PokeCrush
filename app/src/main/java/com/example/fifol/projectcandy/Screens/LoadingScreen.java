package com.example.fifol.projectcandy.Screens;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.fifol.projectcandy.R;

/* Created by Alfi Naim with the kadosh baruch hu on 02/10/2017.
 *  This game inspired by "Candy Crush".
 */

public class LoadingScreen extends BaseActivity {

    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Resources r = getResources();
        String appId = r.getString(R.string.appId);
        String secretKey = r.getString(R.string.secretKey);
        Backendless.initApp(this, appId, secretKey);

        progressBar = findViewById(R.id.indeterminateBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        ConnectToSavedUser();
    }

    private void ConnectToSavedUser() {
        AsyncCallback<Boolean> isValidLoginCallback = new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(final Boolean isLoggedIn) {
                final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
                Backendless.Data.of(BackendlessUser.class).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser user) {
                        backendlessHelper.prepareUser(user);
                        moveToActivity(LoggedUserScreen.class,progressBar);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        moveToActivity(LoginScreen.class,progressBar);
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                moveToActivity(LoginScreen.class,progressBar);
            }
        };
        Backendless.UserService.isValidLogin(isValidLoginCallback);
    }
}


