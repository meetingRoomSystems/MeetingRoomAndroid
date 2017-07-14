package com.compulynx.meetingroombooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
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
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

/**
 * This page is used to show all the bookings for a particular date selected by the user
 */

public class AllBookings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarLayout mAppBarLayout;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private CompactCalendarView mCompactCalendarView;
    private boolean isExpanded = false;
    private String fullname;
    private String username;
    private View mProgressBar;
    private View mList;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bookings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fullname = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");


        mProgressBar = findViewById(R.id.all_booking_progress);
        mList = findViewById(R.id.all_booking_list);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.homepage_name);
        TextView user = (TextView) header.findViewById(R.id.homepage_user);
        name.setText(fullname);
        user.setText(username);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Force English
        mCompactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
                year = Integer.parseInt(yearFormat.format(dateClicked));
                month = Integer.parseInt(monthFormat.format(dateClicked));
                day = Integer.parseInt(dayFormat.format(dateClicked));
                GetAllBooking mAuthTask = new GetAllBooking(year,month,day);
                mAuthTask.execute((Void) null);
                showProgress(true);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));

            }
        });


        final ImageView arrow = (ImageView) findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = (RelativeLayout) findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                }

                isExpanded = !isExpanded;
                mAppBarLayout.setExpanded(isExpanded, true);
            }
        });

        // Set current date to today
        setCurrentDate(new Date());
    }

    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
        }
        year = Integer.parseInt(yearFormat.format(date));
        month = Integer.parseInt(monthFormat.format(date));
        day = Integer.parseInt(dayFormat.format(date));
        GetAllBooking mAuthTask = new GetAllBooking(year,month,day);
        mAuthTask.execute((Void) null);
        showProgress(true);
    }

   private void setSubtitle(String subtitle) {
       TextView datePickerTextView = (TextView) findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
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

    private class GetAllBooking extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlConnection = null;
        private final int mYear;
        private final int mMonth;
        private final int mDay;

        GetAllBooking(int year, int month, int day){
            mYear = year;
            mMonth = month;
            mDay = day;

        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            try {
                String link = getString(R.string.url) + "getbooking.php?booking_date=" + mYear + "-" + mMonth + "-" + mDay;
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
            System.out.println(result);
            showProgress(false);
            TextView noBooking = (TextView) findViewById(R.id.no_booking_text);
            ListView mListView = (ListView) findViewById(R.id.all_booking_list);
            mListView.setVisibility(View.GONE);
            noBooking.setVisibility(View.GONE);
            if(result.equals("")){
                noBooking.setVisibility(View.VISIBLE);
                final ArrayList<Booking> bookings = Booking.getBookings("{bookings:[]}");
                AllBookingsAdapter adapter = new AllBookingsAdapter(getApplicationContext(), bookings);
                mListView.setAdapter(adapter);
                mListView.setVisibility(View.VISIBLE);
                CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.all_booking_activity);
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
                int succ = 0;
                try {
                    succ = jobject.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray arr = new JSONArray();
                try {
                    arr = jobject.getJSONArray("bookings");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (succ == 0 || arr.length() == 0) {
                    noBooking.setVisibility(View.VISIBLE);
                    final ArrayList<Booking> bookings = Booking.getBookings(result);
                    AllBookingsAdapter adapter = new AllBookingsAdapter(getApplicationContext(), bookings);
                    mListView.setAdapter(adapter);
//                    mListView.setVisibility(View.VISIBLE);

                } else {
                    noBooking.setVisibility(View.GONE);
                    final ArrayList<Booking> bookings = Booking.getBookings(result);
                    AllBookingsAdapter adapter = new AllBookingsAdapter(getApplicationContext(), bookings);
                    mListView.setAdapter(adapter);
                    mListView.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        protected void onCancelled() {

        }


    }

}