package com.compulynx.meetingroombooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This activity allows the user to delete any of their upcoming bookings
 */

public class DeleteBooking extends AppCompatActivity {
    private String fullname;
    private String username;
    private View mProgressBar;
    private View mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_booking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fullname = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = findViewById(R.id.progressBar);
        mList = findViewById(R.id.delete_list);

//        run the url call is another thread
        DeleteBookings deleteBookings = new DeleteBookings();
        deleteBookings.execute((Void) null);
        showProgress(true);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,HomePageUser.class);
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

        mList.setVisibility(show ? View.GONE : View.VISIBLE);
        mList.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mList.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

//    this class calls a url which deletes a bookings
    private class DeleteBookings extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            try {
                String link = getString(R.string.url) + "getbooking.php?username=" + username;
                System.out.println(link);
                URL url = new URL(link);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                urlConnection.disconnect();


            } catch (Exception e) {
                return "";
            }

            return result.toString();

        }
        @Override
        protected void onPostExecute(String result) {
            showProgress(false);
            TextView noBooking = (TextView) findViewById(R.id.textView2);
            ListView mListView = (ListView) findViewById(R.id.delete_list);
            if(result.equals("")){
                mListView.setVisibility(View.GONE);
                noBooking.setText("Unexpected error");
            }
            else {
                JSONObject jobject = null;

                try {
                    jobject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int succ = 0;
                try {
                    succ = jobject.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (succ == 0) {
                    mListView.setVisibility(View.GONE);
                    noBooking.setText("No booking available");
                    noBooking.setVisibility(View.VISIBLE);

                } else {
                    final ArrayList<Booking> bookings = Booking.getBookings(result);
                    UpcomingBookingAdapter adapter = new UpcomingBookingAdapter(getApplicationContext(), bookings);
                    mListView.setAdapter(adapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final Booking book = bookings.get(position);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeleteBooking.this);
                            alertDialogBuilder.setTitle("Delete Details");
                            alertDialogBuilder.setMessage("Are you sure you want to delete this booking");
                            alertDialogBuilder.setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Delete delete = new Delete(book.bookingDate,book.bookingTime,book.room);
                                            delete.execute((Void) null);
                                        }
                                    });
                            alertDialogBuilder.setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }

                    });
                }
            }

        }

        @Override
        protected void onCancelled() {

        }


    }

    private class Delete extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlConnection = null;
        private final String mDate;
        private final String mTime;
        private final int mRoom;


        Delete(String date, String time, int room){
            mDate = date;
            mTime = time;
            mRoom = room;
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            try {
                String link = getString(R.string.url) + "cancelbooking.php?username=" + username + "&room=" + mRoom + "&booking_date=" + mDate + "&booking_time=" + mTime;
                System.out.println(link);
                URL url = new URL(link);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                urlConnection.disconnect();


            } catch (Exception e) {
                return "";
            }

            return result.toString();

        }
        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(getApplicationContext(),DeleteBooking.class);
            i.putExtra("fullname",fullname);
            i.putExtra("username",username);
            startActivity(i);
        }

        @Override
        protected void onCancelled() {

        }


    }


}
