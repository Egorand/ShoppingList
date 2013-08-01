package com.example.shoppinglist;

import android.util.Log;

import com.example.shoppinglist.auth.Constants;
import com.example.shoppinglist.auth.IAuthenticationClient;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by eandreevici on 31/07/13.
 */
public class ShoppingListAuthenticationClient implements IAuthenticationClient {

    @Override
    public String signUp(String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        if (user.isNew()) {
            try {
                user.signUp();
            } catch (ParseException e) {
                Log.e(Constants.DEBUG_TAG, "Sign up error: ", e);
            }
        }
        return user.getSessionToken();
    }

    @Override
    public String signIn(String email, String password) {
        ParseUser user = null;
        try {
            user = ParseUser.logIn(email, password);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user == null ? null : user.getSessionToken();
    }

    @Override
    public void logOut() {
        ParseUser.logOut();
    }

    @Override
    public boolean isUserAuthenticated() {
        return ParseUser.getCurrentUser().isAuthenticated();
    }
}
