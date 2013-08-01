package com.example.shoppinglist.auth;

import com.parse.ParseUser;

/**
 * Created by eandreevici on 31/07/13.
 */
public class ShoppingListAuthenticationClient implements IAuthenticationClient {

    @Override
    public String signUp(String email, String password) throws Exception {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.signUp();
        return user.getSessionToken();
    }

    @Override
    public String signIn(String email, String password) throws Exception {
        ParseUser user = ParseUser.logIn(email, password);
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
