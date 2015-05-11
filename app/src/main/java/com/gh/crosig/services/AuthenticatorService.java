package com.gh.crosig.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by henrique on 11/05/15.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
