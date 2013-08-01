package com.example.shoppinglist.auth;

/**
 * An interface for handling the authentication process of an application.
 * <p/>
 * Created by eandreevici on 31/07/13.
 */
public interface IAuthenticationClient {

    /**
     * Register a user with specified credentials.
     *
     * @param email    User's email
     * @param password User's password
     * @return Authentication token for this user
     */
    String signUp(final String email, final String password);

    /**
     * Log in the user with specified credentials.
     *
     * @param email    User's email
     * @param password User's password
     * @return Authentication token for this user
     */
    String signIn(final String email, final String password);

    /**
     * Check whether current user is authenticated on this device.
     *
     * @return <code>true</code> if current user is authenticated, <code>false</code>
     * otherwise
     */
    boolean isUserAuthenticated();

    /**
     * Log current user out.
     */
    void logOut();
}
