package com.example.shoppinglist.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by eandreevici on 31/07/13.
 */
public class AuthenticatorService extends Service {

    private ShoppingListAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        this.authenticator = new ShoppingListAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
