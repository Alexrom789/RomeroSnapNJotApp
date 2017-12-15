package com.aromero.app;

import android.app.Application;

import com.aromero.app.api.Api;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class App extends Application {

    private Api api;
    private GoogleSignInOptions gso;

    @Override
    public void onCreate() {
        super.onCreate();

        api = new Api();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("Your Token Here")
                .requestEmail()
                .build();
    }

    public GoogleSignInOptions getGso() {
        return gso;
    }

    public Api getApi() {
        return api;
    }
}
