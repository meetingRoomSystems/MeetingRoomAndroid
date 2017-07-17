package com.compulynx.meetingroombooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Calendar;

/**
 * Home page screen where a user can see their upcoming bookings and can make or edit their bookings
 */

public class HomePageUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String fullname;
    private String username;
    private View mProgressBar;
    private View mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fullname = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),NewBookings.class);
                i.putExtra("fullname", fullname);
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mProgressBar = findViewById(R.id.upcoming_progress);
        mList = findViewById(R.id.upcoming_listView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.homepage_name);
        TextView user = (TextView) header.findViewById(R.id.homepage_user);
        name.setText(fullname);
        user.setText(username);
        // run the url call is another thread
        CreateReminders createReminders = new CreateReminders();
        createReminders.execute((Void) null);
        GetUpcomingBooking mAuthTask = new GetUpcomingBooking();
        mAuthTask.execute((Void) null);
        showProgress(true);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home){
            Intent i = new Intent(this,HomePageUser.class);
            i.putExtra("fullname", fullname);
            i.putExtra("username", username);
            startActivity(i);

        } else if (id == R.id.nav_new_booking) {
            Intent i = new Intent(this,NewBookings.class);
            i.putExtra("fullname", fullname);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_delete_booking) {
            Intent i = new Intent(this,DeleteBooking.class);
            i.putExtra("fullname", fullname);
            i.putExtra("username", username);
            startActivity(i);

        } else if (id == R.id.nav_all_booking) {
            Intent i = new Intent(this,AllBookings.class);
            i.putExtra("fullname", fullname);
            i.putExtra("username", username);
            startActivity(i);


        } else if (id == R.id.nav_room) {
            Intent i = new Intent(this,Rooms.class);
            i.putExtra("fullname", fullname);
            i.putExtra("username", username);
            startActivity(i);

        }
        else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Log Out");
            alertDialogBuilder.setMessage("Are you sure you wanted to log out");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent i = new Intent(getApplicationContext(),Login.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//    this class calls a url which gets All the upcoming bookings for a user and shows those booking in a ListView
    private class GetUpcomingBooking extends AsyncTask<Void, Void, String> {

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
            TextView noBooking = (TextView) findViewById(R.id.no_booking_text);
            ListView mListView = (ListView) findViewById(R.id.upcoming_listView);
            if(result.equals("")){
                mListView.setVisibility(View.GONE);
                noBooking.setText("Unexpected error");
                noBooking.setVisibility(View.VISIBLE);
            }
            else {
                JSONObject jobject = null;

                try {
                    jobject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int succ = 0;
                JSONArray booking = new JSONArray();
                try {
                    succ = jobject.getInt("success");
                    booking = jobject.getJSONArray("bookings");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (succ == 0) {
                    mListView.setVisibility(View.GONE);
                    noBooking.setText("Unexpected error");
                    noBooking.setVisibility(View.VISIBLE);

                }
                else if(succ == 1 && booking.length() == 0) {
                    mListView.setVisibility(View.GONE);
                    noBooking.setVisibility(View.VISIBLE);
                }
                else{
                    final ArrayList<Booking> bookings = Booking.getBookings(result);
                    UpcomingBookingAdapter adapter = new UpcomingBookingAdapter(getApplicationContext(), bookings);
                    mListView.setAdapter(adapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Booking book = bookings.get(position);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageUser.this);
                            alertDialogBuilder.setTitle("Booking Details");
                            alertDialogBuilder.setMessage("This booking is on " + book.bookingDate + " from " + book.bookingTime + " to " + book.bookingTimeEnd + ". The meeting is in room " + book.room + " and is with " + book.capacity + " people" );
                            alertDialogBuilder.setPositiveButton("Done",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });

                            if(book.room == 1){
                                alertDialogBuilder.setIcon(R.drawable.room1);
                            }
                            else if(book.room == 2){
                                alertDialogBuilder.setIcon(R.drawable.room2);
                            }
                            else if(book.room == 3){
                                alertDialogBuilder.setIcon(R.drawable.room3);
                            }
                            else if(book.room == 4){
                                alertDialogBuilder.setIcon(R.drawable.room4);
                            }
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
//    this class calls a url which creates reminders for bookings made in the application or on the website
    private class CreateReminders extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            try {
                String link = getString(R.string.url) + "getReminders.php?username=" + username;
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
            if(!result.equals("")){
                JSONObject jobject = null;

                try {
                    jobject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int succ = 0;
                JSONArray booking = new JSONArray();
                try {
                    succ = jobject.getInt("success");
                    booking = jobject.getJSONArray("bookings");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(succ == 1 && booking.length() != 0) {
                    final ArrayList<Booking> bookings = Booking.getBookings(result);
                    for(Booking book: bookings){
                        String[] dates = book.bookingDate.split("-");
                        String[] times = book.bookingTime.split(":");
                        int y = Integer.parseInt(dates[0]);
                        int m = Integer.parseInt(dates[1]);
                        int d = Integer.parseInt(dates[2]);
                        int h = Integer.parseInt(times[0]);
                        int mm = Integer.parseInt(times[1]);
                        String uniqueCode = "" + m + d + h + mm + book.room;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, m-1);
                        calendar.set(Calendar.YEAR, y);
                        calendar.set(Calendar.DAY_OF_MONTH, d);

                        calendar.set(Calendar.HOUR_OF_DAY, h-1);
                        calendar.set(Calendar.MINUTE, mm);
                        calendar.set(Calendar.SECOND, 0);
                        Intent myIntent = new Intent(HomePageUser.this, MyReceiver.class);
                        myIntent.putExtra("fullname", book.fullname);
                        myIntent.putExtra("capacity", book.capacity);
                        myIntent.putExtra("room", book.room);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomePageUser.this, Integer.parseInt(uniqueCode), myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        if(book.reminder == 0){
                            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                        }
                        else{
                            pendingIntent.cancel();
                            alarmManager.cancel(pendingIntent);

                        }


                    }

                }
            }

        }

        @Override
        protected void onCancelled() {

        }


    }


}
