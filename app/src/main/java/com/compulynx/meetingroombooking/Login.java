package com.compulynx.meetingroombooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.commons.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via username and password.
 */

public class Login extends AppCompatActivity{


    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegister = (Button) findViewById(R.id.login_register);
        mRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void register() {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }


    /**
     * Attempts to sign into account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (!isUsernameValid(user)){
            mUserView.setError("Username cannot have any spaces");
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
            mAuthTask = new UserLoginTask(user, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isUsernameValid(String username){
        return !username.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }



    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUser;
        private final String mPassword;
        private HttpURLConnection urlConnection;


        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            if(isNetworkAvailable()) {
                try {
                    String link = getString(R.string.url) +  "login.php?username=" + mUser + "&user_password=" + mPassword;
                    URL url = new URL(link);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                } catch (Exception e) {
                    return "";
                } finally {
                    urlConnection.disconnect();
                }
                return result.toString();
            }
            else{
                return "ni";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            CoordinatorLayout mCoordinatorLayout;
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_form);
            mAuthTask = null;
            showProgress(false);

            if (result.equals("ni")){
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connect to the internet", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else if(result.equals("")){
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Unexpected error occured", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else {
                JSONObject jobject = null;
                try {
                    jobject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int success = 0;
                try {
                    success = jobject.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success == 1) {
                    String name = null;
                    String user = null;
                    try {
                        name = jobject.get("fullname").toString();
                        user = jobject.get("username").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    goTo(name, user);
                } else {
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.error_incorrect, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    mUserView.setText(null);
                    mPasswordView.setText(null);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * If successful login take user to the home screen
     */
    private void goTo(String name, String user) {
        Intent i = new Intent(this,HomePageUser.class);
        i.putExtra("fullname", WordUtils.capitalize(name));
        i.putExtra("username", user);
        startActivity(i);
        finish();
    }

    /**
     * Check if user is connected to the internet
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
