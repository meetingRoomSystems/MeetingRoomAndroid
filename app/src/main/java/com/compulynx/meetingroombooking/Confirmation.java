package com.compulynx.meetingroombooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This activity contains information about the booking the user is about to make.
 */

public class Confirmation extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private String fullname;
    private String username;
    private String date;
    private String time;
    private String displayDate;
    private String room;
    private ImageButton button;
    private EditText results_capacity;
    private Spinner spinner;
    private int maxCapacity = 0;
    MultiSpinner multiSpinner;
    List<String> items;
    Map<String, String> users;
    private View mProgressView;
    private View mConfirmationFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullname = this.getIntent().getExtras().getString("fullname");
        username = this.getIntent().getExtras().getString("username");
        date = this.getIntent().getExtras().getString("date");
        time = this.getIntent().getExtras().getString("time");
        room = this.getIntent().getExtras().getString("room");
        displayDate = this.getIntent().getExtras().getString("displayDate");
        GetAllUsers getAllUsers = new GetAllUsers();
        getAllUsers.execute((Void) null);

        mConfirmationFormView = findViewById(R.id.confirm_form);
        mProgressView = findViewById(R.id.confirm_progress);

        TextView results_date = (TextView) findViewById(R.id.resultsView_date);
        TextView results_time = (TextView) findViewById(R.id.resultsView_time);
        TextView results_room = (TextView) findViewById(R.id.resultsView_room);
        results_capacity = (EditText) findViewById(R.id.resultsView_capacity);
        TextView textView_room = (TextView) findViewById(R.id.textView_capacity);
        spinner = (Spinner) findViewById(R.id.spinner);



        button = (ImageButton) findViewById(R.id.submit);
        button.setClickable(true);


        if(room.equals("1")){
            maxCapacity = 10;
            textView_room.setText("Number of\nPeople\n(max 10):");

        }
        else if(room.equals("2")){
            maxCapacity = 15;
            textView_room.setText("Number of\nPeople\n(max 15):");
        }
        else if(room.equals("3")){
            maxCapacity = 5;
            textView_room.setText("Number of\nPeople\n(max 5):");
        }
        else if(room.equals("4")){
            maxCapacity = 20;
            textView_room.setText("Number of\nPeople\n(max 20):");
        }

        results_date.setText(displayDate);
        results_time.setText(time);
        results_room.setText(room);




    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,NewBookings.class);
        i.putExtra("fullname", fullname);
        i.putExtra("username", username);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
        mConfirmationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mConfirmationFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mConfirmationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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


    public void submit(@SuppressWarnings("UnusedParameters") View view){

        if(TextUtils.isEmpty(results_capacity.getText().toString())){
            results_capacity.setError(getString(R.string.error_field_required));
            results_capacity.requestFocus();
        }
        else if( Integer.parseInt(results_capacity.getText().toString()) > maxCapacity || Integer.parseInt(results_capacity.getText().toString()) <= 1){
            results_capacity.setError("Cannot accommodate that many people");
        }
        else{
            String duration = new String();
            if(spinner.getSelectedItem().toString().equals("30")){
                duration = "30";
            }
            else if(spinner.getSelectedItem().toString().equals("60")){
                duration = "60";
            }
            else if(spinner.getSelectedItem().toString().equals("90")){
                duration = "90";
            }
            else{
                duration = "30";
            }
            button.setClickable(false);
            showProgress(true);
//          run the url call is another thread
            ConfirmBooking confirmBooking = new ConfirmBooking(fullname,username,date,time,room,results_capacity.getText().toString(),displayDate,duration);
            confirmBooking.execute((Void) null);
        }

    }

    public void cancel(@SuppressWarnings("UnusedParameters") View view) {
        onBackPressed();
    }

    @Override
    public void onItemsSelected(boolean[] selected) {

    }

    //    this class calls a url which makes a booking for the user with the infromation from this activity and NewBookings
    private class ConfirmBooking extends AsyncTask<Void, Void, String> {

        private final String mFullName;
        private final String mUserName;
        private final String mDisplayDate;
        private final String mDate;
        private final String mTime;
        private final String mRoom;
        private final String mCapacity;
        private final String mDuration;
        private HttpURLConnection urlConnection;
        boolean[] selected;
        ArrayList<String> tagsFullname;
        ArrayList<String> tagsUsername;


        ConfirmBooking(String fullname, String username,String date,String time,String room,String capacity,String displayDate,String duration) {
            mFullName = fullname;
            mUserName = username;
            mDate = date;
            mTime = time;
            mRoom = room;
            mCapacity = capacity;
            mDisplayDate = displayDate;
            mDuration = duration;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selected = multiSpinner.getSelected();
            tagsFullname = new ArrayList<>();
            tagsUsername = new ArrayList<>();
            for(int i=0;i<selected.length;i++){
                if(selected[i]){
                    tagsFullname.add(items.get(i));
                    System.out.println(items.get(i));
                }
            }
            if(tagsFullname.size() > 0) {
                for (int i = 0; i < tagsFullname.size(); i++) {
                    tagsUsername.add(users.get(tagsFullname.get(i)));
                }
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();

            if(isNetworkAvailable()) {
                try {
                    String link = "";
                    if(tagsFullname.size()>0){
                         link += getString(R.string.url) + "setbooking.php?username=" + mUserName + "&capacity=" + mCapacity + "&room=" + mRoom + "&booking_date=" + mDate + "&booking_time=" + mTime + "&length=" + mDuration;
                        for(String tag : tagsUsername){
                            link += "&tags[]=" + tag;
                        }
                    }
                    else {
                        link = getString(R.string.url) + "setbooking.php?username=" + mUserName + "&capacity=" + mCapacity + "&room=" + mRoom + "&booking_date=" + mDate + "&booking_time=" + mTime + "&length=" + mDuration + "&tags[]=none";
                    }
                    System.out.println(link);
                    URL url = new URL(link);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    result.append("");

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
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.confirmation);
            showProgress(false);
            button.setClickable(true);
            if (result.equals("ni") ){
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connect to the internet", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else if(result.equals("")){
                System.out.println("Unexpected error");
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Unexpected error occurred", Snackbar.LENGTH_LONG);
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
                    assert jobject != null;
                    success = jobject.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success == 1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Confirmation.this);
                    alertDialogBuilder.setTitle("Booking Made");
                    alertDialogBuilder.setMessage("Dear " + mFullName + ", meeting room " + mRoom + " has been booked for " + mDisplayDate + " at " + mTime + " for " + mDuration + " mins with " + mCapacity + " people.");
                            alertDialogBuilder.setPositiveButton("Done",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent i = new Intent(getApplicationContext(),HomePageUser.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                            i.putExtra("fullname", mFullName);
                                            i.putExtra("username", mUserName);
                                            startActivity(i);
                                            finish();

                                        }
                                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Booking cannot be made as no free slot for " + mDuration + " mins" , Snackbar.LENGTH_LONG);
                    snackbar.show();
                    showProgress(false);
                }
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private class GetAllUsers extends AsyncTask<Void,Void,String> implements MultiSpinner.MultiSpinnerListener {

        private HttpURLConnection urlConnection;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
            users = new HashMap<String, String>();
            items = new ArrayList<>();
            multiSpinner.setEnabled(false);
            multiSpinner.setItems(items,"retrieving usernames",this);
        }

        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            if(isNetworkAvailable()) {
                try {
                    String link = getString(R.string.url) +  "getAllUsers.php?username="+username;
                    System.out.println(link);
                    URL url = new URL(link);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    result.append("");


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

        protected void onPostExecute(String result) {
            JSONObject jobject = null;
            try {
                jobject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int success = 0;
            try {
                assert jobject != null;
                success = jobject.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray names = new JSONArray();
            try {
                names = jobject.getJSONArray("names");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success == 1) {
                for(int i = 0; i < names.length(); i++) {
                    String fname;
                    String uname;
                    try {
                        fname= names.getJSONObject(i).getString("fullname");
                        uname= names.getJSONObject(i).getString("username");
                    } catch (JSONException e) {
                        fname = "";
                        uname = "";
                    }
                    items.add(fname);
                    users.put(fname,uname);
                }
                multiSpinner.setItems(items, "No one selected", this);
                multiSpinner.setEnabled(true);
            } else {
                ArrayList<String> noItems = new ArrayList<>();
                noItems.add("No users");
                multiSpinner.setItems(noItems, "No one selected", this);
                multiSpinner.setEnabled(true);
            }
        }

        @Override
        public void onItemsSelected(boolean[] selected) {

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
