package com.example.shoppinglist.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * A set of convenience methods for querying the {@link AccountManager}
 * <p/>
 * Created by eandreevici on 31/07/13.
 */
public final class AccountUtils {

    private AccountUtils() {
    }

    /**
     * Check whether an auth of this type has been registered. Assumption is that there
     * can only be one single auth of a type.
     *
     * @param context     A {@link Context} object
     * @param accountType Type of the auth
     * @return <code>true</code> if an auth of given type exists, <code>false</code> otherwise
     */
    public static boolean isAccountRegistered(Context context, String accountType) {
        return getAccount(context, accountType) != null;
    }

    /**
     * Retrieve the {@link Account} object for given <code>accountName</code> and <code>accountType</code>.
     *
     * @param context     A {@link Context} object
     * @param accountName Name of the auth
     * @param accountType Type of the auth
     * @return {@link Account} object with specified parameters, <code>null</code> if there is no
     * auth with such <code>accountName</code>, or there are no accounts of this
     * <code>accountType</code>
     */
    public static Account getAccount(Context context, String accountName, String accountType) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        for (Account account : accounts) {
            if (account.name.equals(accountName)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Retrieve the {@link Account} object for given <code>accountType</code>, assuming that
     * there's one single auth of this type.
     *
     * @param context     A {@link Context} object
     * @param accountType Type of the auth
     * @return {@link Account} object with specified parameters, <code>null</code> if there
     * are no accounts of this <code>accountType</code>
     */
    public static Account getAccount(Context context, String accountType) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    /**
     * Check whether an {@link Account} with given <code>accountName</code> and <code>accountType</code>
     * exists.
     *
     * @param context     A {@link Context} object
     * @param accountName Name of the auth
     * @param accountType Type of the auth
     * @return <code>true</code> if an {@link Account} with specified parameters exists, <code>false</code>
     * otherwise
     */
    public static boolean accountExists(Context context, String accountName, String accountType) {
        return getAccount(context, accountName, accountType) != null;
    }

    /**
     * Retrieve the authentication token for given <code>auth</code>.
     *
     * @param context       A {@link Context} object
     * @param account       An {@link Account}
     * @param authTokenType The type of the token
     * @return Authentication token for given <code>auth</code>, <code>null</code>
     * if there is no token for some reason
     */
    public static String getAuthToken(Context context, Account account, String authTokenType) {
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.peekAuthToken(account, authTokenType);
    }

    /**
     * Check whether there exists an authentication token for given <code>auth</code>.
     *
     * @param context       A {@link Context} object
     * @param account       An {@link Account}
     * @param authTokenType The type of the token
     * @return <code>true</code> if an authentication for specified <code>auth</code> exists,
     * <code>false</code> otherwise
     */
    public static boolean isTokenValid(Context context, Account account, String authTokenType) {
        return getAuthToken(context, account, authTokenType) != null;
    }

    /**
     * Invalidate the authentication token for specified <code>auth</code>.
     *
     * @param context       A {@link Context} object
     * @param account       An {@link Account}
     * @param authTokenType The type of the token
     */
    public static void invalidateToken(Context context, Account account, String authTokenType) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.invalidateAuthToken(account.type, getAuthToken(context, account, authTokenType));
    }
}
