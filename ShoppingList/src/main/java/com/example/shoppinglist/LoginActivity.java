package com.example.shoppinglist;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shoppinglist.auth.IAuthenticationClient;
import com.example.shoppinglist.auth.ShoppingListAccountInfo;

/**
 * Activity which displays a main screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String ARG_DEFAULT_EMAIL = "default_email";

    private static final String ARG_PASSWORD = "password";
    private static final String ARG_NEW_USER = "new_user";

    /**
     * Keep track of the main task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the main attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    private AccountManager accountManager;
    private IAuthenticationClient authenticationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the main form.
        mEmailView = (EditText) findViewById(R.id.email);
        if (getIntent() != null && getIntent().hasExtra(ARG_DEFAULT_EMAIL)) {
            mEmailView.setText(getIntent().getExtras().getString(ARG_DEFAULT_EMAIL));
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });
        if (!TextUtils.isEmpty(mEmailView.getText())) {
            mPasswordView.requestFocus();
        }

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(true);
            }
        });

        this.accountManager = AccountManager.get(this);
        this.authenticationClient = ShoppingListAccountInfo.authenticationClient;
    }

    /**
     * Attempts to sign in or register the auth specified by the main form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual main attempt is made.
     */
    public void attemptLogin(boolean newUser) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the main attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt main and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user main attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask(newUser);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the main form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous main/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final boolean newUser;

        public UserLoginTask(boolean newUser) {
            this.newUser = newUser;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            Bundle data = new Bundle();
            String authToken = null;
            try {
                if (newUser) {
                    authToken = authenticationClient.signUp(mEmail, mPassword);
                    data.putBoolean(ARG_NEW_USER, true);
                } else {
                    authToken = authenticationClient.signIn(mEmail, mPassword);
                }
            } catch (Exception e) {
                data.putString(AccountManager.KEY_ERROR_MESSAGE, e.getLocalizedMessage());
            }
            if (authToken != null) {
                data.putString(AccountManager.KEY_ACCOUNT_NAME, mEmail);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, ShoppingListAccountInfo.ACCOUNT_TYPE);
                data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                data.putString(ARG_PASSWORD, mPassword);
            }
            Intent result = new Intent();
            result.putExtras(data);
            return result;
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (intent.hasExtra(AccountManager.KEY_ERROR_MESSAGE)) {
                mPasswordView.setError(intent.getStringExtra(AccountManager.KEY_ERROR_MESSAGE));
                mPasswordView.requestFocus();
            } else {
                finishLogin(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(ARG_PASSWORD);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        Account account = new Account(accountName, accountType);
        if (!accountManager.addAccountExplicitly(account, accountPassword, null)) {
            accountManager.setPassword(account, accountPassword);
        }
        accountManager.setAuthToken(account, ShoppingListAccountInfo.AUTHTOKEN_TYPE_STANDARD, authToken);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
