package com.bnnvara.kiespijn.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bnnvara.kiespijn.SingleFragmentActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class LoginActivity extends SingleFragmentActivity {

    private static final String TAG = "LoginActivity";
    private static final String LOGGING_OUT = "logging_out";

    // Facebook Parameters
    CallbackManager mCallbackManager;
    Boolean isLoggingOut = false;


    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected LoginFragment createFragment() {
        Intent i = getIntent();
        isLoggingOut = i.getBooleanExtra(LOGGING_OUT, false);
        return LoginFragment.newInstance(isLoggingOut);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        Collection<String> facebookPersmissions = Arrays.asList("email", "public_profile", "user_friends");

        LoginManager.getInstance().logInWithReadPermissions(this, facebookPersmissions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
