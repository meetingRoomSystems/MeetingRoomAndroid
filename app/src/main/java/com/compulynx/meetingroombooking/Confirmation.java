package com.compulynx.meetingroombooking;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * This activity contains information about the booking the user is about to make.
 */

public class Confirmation extends AppCompatActivity {

    private String fullname;
    private String username;
    private String date;
    private String time;
    private String displayDate;
    private String room;
    private EditText results_capacity;
    private Spinner spinner;
    private int maxCapacity = 0;

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

        TextView results_date = (TextView) findViewById(R.id.resultsView_date);
        TextView results_name = (TextView) findViewById(R.id.resultsView_By);
        TextView results_time = (TextView) findViewById(R.id.resultsView_time);
        TextView results_room = (TextView) findViewById(R.id.resultsView_room);
        results_capacity = (EditText) findViewById(R.id.resultsView_capacity);
        TextView textView_room = (TextView) findViewById(R.id.textView_capacity);
        spinner = (Spinner) findViewById(R.id.spinner);


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
        results_name.setText(fullname);
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
            if(String.valueOf(spinner.getSelectedItem()).equals("30 mins")){
                duration = "30";
            }
            else if(String.valueOf(spinner.getSelectedItem()).equals("30 mins")){
                duration = "60";
            }
            else if(String.valueOf(spinner.getSelectedItem()).equals("90 mins")){
                duration = "90";
            }
//          run the url call is another thread
            ConfirmBooking confirmBooking = new ConfirmBooking(fullname,username,date,time,room,results_capacity.getText().toString(),displayDate,duration);
            confirmBooking.execute((Void) null);
        }

    }

    public void cancel(@SuppressWarnings("UnusedParameters") View view) {
        onBackPressed();
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
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            if(isNetworkAvailable()) {
                try {
                    String link = getString(R.string.url) +  "setbooking.php?username="+mUserName+"&capacity="+mCapacity+"&room="+mRoom+"&booking_date="+mDate+"&booking_time="+mTime+"&length="+mDuration;
                    System.out.println(link);
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
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.confirmation);

            if (result.equals("ni") ){
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Connect to the internet", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else if(result.equals("")){
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
//
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
                }
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
