package com.compulynx.meetingroombooking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.StreamHandler;

/**
 * This activity shows available timings for each room on a particular day selected by the user
 */

public class NewBookings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String fullname;
    private String username;
    private AppBarLayout mAppBarLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private boolean isExpanded = false;
    private CompactCalendarView mCompactCalendarView;
    private int year, month, day;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private final String[] allTime = {"08:00:00","08:30:00","09:00:00","09:30:00","10:00:00","10:30:00","11:00:00","11:30:00","12:00:00","12:30:00","13:00:00","13:30:00","14:00:00","14:30:00","15:00:00","15:30:00","16:00:00", "16:30:00", "17:00:00","17:30:00"};
    GestureDetector gestureScanner;
    ImageView arrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bookings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fullname = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");
        PlaceholderFragment.fname = fullname;
        PlaceholderFragment.uname = username;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.homepage_name);
        TextView user = header.findViewById(R.id.homepage_user);
        name.setText(fullname);
        user.setText(username);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);




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
                PlaceholderFragment.checkRoom1 = false;
                PlaceholderFragment.checkRoom2 = false;
                PlaceholderFragment.checkRoom3 = false;
                PlaceholderFragment.checkRoom4 = false;
                DecimalFormat mFormat= new DecimalFormat("00");
                mFormat.setRoundingMode(RoundingMode.DOWN);
                ArrayList<String> data = new ArrayList<String>();
                data.add("Getting available timings...");
                PlaceholderFragment.dateDisplay =  mFormat.format(Double.valueOf(day)) + "/" +  mFormat.format(Double.valueOf(month)) + "/" +  mFormat.format(Double.valueOf(year));
                PlaceholderFragment.date =  mFormat.format(Double.valueOf(year)) + "-" +  mFormat.format(Double.valueOf(month)) + "-" +  mFormat.format(Double.valueOf(day));

//                remove the list with the timings and show the message "Getting available timings" for all tabs
                PlaceholderFragment.adapter1 = new ArrayAdapter<>(getApplicationContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text, data);
                try {
                    PlaceholderFragment.mListView1.setAdapter(PlaceholderFragment.adapter1);
                    PlaceholderFragment.mListView1.setOnItemClickListener(null);
                    PlaceholderFragment.mListView2.setAdapter(PlaceholderFragment.adapter1);
                    PlaceholderFragment.mListView2.setOnItemClickListener(null);
                }
                catch (Exception ignored){}
                try {
                    PlaceholderFragment.mListView3.setAdapter(PlaceholderFragment.adapter1);
                    PlaceholderFragment.mListView3.setOnItemClickListener(null);
                }
                catch (Exception ignored){}
                try {
                    PlaceholderFragment.mListView4.setAdapter(PlaceholderFragment.adapter1);
                    PlaceholderFragment.mListView4.setOnItemClickListener(null);
                }
                catch (Exception ignored){}

                GetAvailableTimings task = new GetAvailableTimings(PlaceholderFragment.date,1, PlaceholderFragment.dateDisplay);
                task.execute();
                GetAvailableTimings task2 = new GetAvailableTimings(PlaceholderFragment.date,2, PlaceholderFragment.dateDisplay);
                task2.execute();
                GetAvailableTimings task3 = new GetAvailableTimings(PlaceholderFragment.date,3, PlaceholderFragment.dateDisplay);
                task3.execute();
                GetAvailableTimings task4 = new GetAvailableTimings(PlaceholderFragment.date,4, PlaceholderFragment.dateDisplay);
                task4.execute();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });


        arrow = (ImageView) findViewById(R.id.date_picker_arrow);

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
        PlaceholderFragment.checkRoom1 = false;
        PlaceholderFragment.checkRoom2 = false;
        PlaceholderFragment.checkRoom3 = false;
        PlaceholderFragment.checkRoom4 = false;
        DecimalFormat mFormat= new DecimalFormat("00");
        mFormat.setRoundingMode(RoundingMode.DOWN);
        PlaceholderFragment.dateDisplay =  mFormat.format(Double.valueOf(day)) + "/" +  mFormat.format(Double.valueOf(month)) + "/" +  mFormat.format(Double.valueOf(year));
        PlaceholderFragment.date =  mFormat.format(Double.valueOf(year)) + "-" +  mFormat.format(Double.valueOf(month)) + "-" +  mFormat.format(Double.valueOf(day));
        GetAvailableTimings task = new GetAvailableTimings(PlaceholderFragment.date,1, PlaceholderFragment.dateDisplay);
        task.execute();
        GetAvailableTimings task2 = new GetAvailableTimings(PlaceholderFragment.date,2, PlaceholderFragment.dateDisplay);
        task2.execute();
        GetAvailableTimings task3 = new GetAvailableTimings(PlaceholderFragment.date,3, PlaceholderFragment.dateDisplay);
        task3.execute();
        GetAvailableTimings task4 = new GetAvailableTimings(PlaceholderFragment.date,4, PlaceholderFragment.dateDisplay);
        task4.execute();

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


    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ListView mListView;
//        ArrayLists to store the the bookings
        private static ArrayList<String> room1 = new ArrayList<>();
        private static ArrayList<String> room2 = new ArrayList<>();
        private static ArrayList<String> room3 = new ArrayList<>();
        private static ArrayList<String> room4 = new ArrayList<>();
//      boolean to check if the bookings has been received from database so that it can be displayed
        private static boolean checkRoom1;
        private static boolean checkRoom2;
        private static boolean checkRoom3;
        private static boolean firstRoom3;
        private static boolean checkRoom4;
//        firstRoom is to check if its the first time we open the Room 4 tab
        private static boolean firstRoom4;
//        boolean to check if date is from today onwards. If true then date from today onwards
        private static boolean checkDate1;
        private static boolean checkDate2;
        private static boolean checkDate3;
        private static boolean checkDate4;
//        listView for each rooms which will show the available timings for each room
        private static ListView mListView1;
        private static ListView mListView2;
        private static ListView mListView3;
        private static ListView mListView4;
//        adapter for the list view (link the ArrayList to the listviews)
        private static ArrayAdapter adapter1;
        private static ArrayAdapter adapter2;
        private static ArrayAdapter adapter3;
        private static ArrayAdapter adapter4;
        private static String date;
        private static String dateDisplay;
        private static String fname;
        private static String uname;

        public PlaceholderFragment() {

        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_booking, container, false);

            TextView room = rootView.findViewById(R.id.section_label);
            TextView capacity = rootView.findViewById(R.id.capacity);
            TextView features = rootView.findViewById(R.id.features);
            mListView =  rootView.findViewById(R.id.list);
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 0){
                room.setText(R.string.room1_room);
                capacity.setText(R.string.room1_capacity);
                features.setText(R.string.room1_features);
                mListView1 = mListView;
                if(checkRoom1){
                    adapter1 = new ArrayAdapter<>(getContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text,room1);
                    mListView1.setAdapter(PlaceholderFragment.adapter1);//
                    if(checkDate1) {
                        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String timeOfBooking = PlaceholderFragment.room1.get(position);
                                Intent i = new Intent(getContext(), Confirmation.class);
                                i.putExtra("fullname", fname);
                                i.putExtra("username", uname);
                                i.putExtra("date", date);
                                i.putExtra("time", timeOfBooking);
                                i.putExtra("room", "1");
                                i.putExtra("displayDate", dateDisplay);
                                startActivity(i);
                            }

                        });
                    }
                    else{
                        mListView1.setOnItemClickListener(null);
                    }
                }

            }


            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                room.setText(R.string.room2_room);
                capacity.setText(R.string.room2_capacity);
                features.setText(R.string.room2_features);
                mListView2 = mListView;
                if(checkRoom2){
                    adapter2 = new ArrayAdapter<>(getContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text,room2);
                    mListView2.setAdapter(PlaceholderFragment.adapter2);
//
                    if(checkDate2) {
                        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String timeOfBooking = PlaceholderFragment.room2.get(position);
                                Intent i = new Intent(getContext(), Confirmation.class);
                                i.putExtra("fullname", fname);
                                i.putExtra("username", uname);
                                i.putExtra("date", date);
                                i.putExtra("time", timeOfBooking);
                                i.putExtra("room", "2");
                                i.putExtra("displayDate", dateDisplay);
                                startActivity(i);
                            }

                        });
                    }
                    else{
                        mListView2.setOnItemClickListener(null);
                    }
                }
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                room.setText(R.string.room3_room);
                capacity.setText(R.string.room3_capacity);
                features.setText(R.string.room3_features);
                mListView3 = mListView;
                if(firstRoom3 || checkRoom3){
                    adapter3 = new ArrayAdapter<>(getContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text,room3);
                    mListView3.setAdapter(PlaceholderFragment.adapter3);//
                    if(checkDate3){
                        mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String timeOfBooking = PlaceholderFragment.room3.get(position);
                                Intent i = new Intent(getContext(), Confirmation.class);
                                i.putExtra("fullname", fname);
                                i.putExtra("username", uname);
                                i.putExtra("date", date);
                                i.putExtra("time", timeOfBooking);
                                i.putExtra("room", "3");
                                i.putExtra("displayDate",dateDisplay);
                                startActivity(i);
                            }

                        });
                    }
                    else{
                        mListView3.setOnItemClickListener(null);
                    }
                }
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                room.setText(R.string.room4_room);
                capacity.setText(R.string.room4_capacity);
                features.setText(R.string.room4_features);
                mListView4 = mListView;
                if(firstRoom4 || checkRoom4){
                    adapter4 = new ArrayAdapter<>(getContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text,room4);
                    mListView4.setAdapter(PlaceholderFragment.adapter4);
//
                    if(checkDate4) {
                        mListView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String timeOfBooking = PlaceholderFragment.room4.get(position);
                                Intent i = new Intent(getContext(), Confirmation.class);
                                i.putExtra("fullname", fname);
                                i.putExtra("username", uname);
                                i.putExtra("date", date);
                                i.putExtra("time", timeOfBooking);
                                i.putExtra("room", "4");
                                i.putExtra("displayDate", dateDisplay);
                                startActivity(i);
                            }

                        });
                    }
                    else{
                        mListView4.setOnItemClickListener(null);
                    }
                }
            }

            return rootView;
        }

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ROOM 1";
                case 1:
                    return "ROOM 2";
                case 2:
                    return "ROOM 3";
                case 3:
                    return "ROOM 4";
            }
            return null;
        }
    }



    private class GetAvailableTimings extends AsyncTask<Void, Void, String> {

        private HttpURLConnection urlConnection = null;
        private final String mDate;
        private final String mDisplayDate;
        private final int mRoom;

        GetAvailableTimings(String date, int room, String displayDate){
            mDate = date;
            mRoom = room;
            mDisplayDate = displayDate;

        }


        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            try {
                String link = getString(R.string.url) + "getbooking.php?booking_date=" + mDate + "&room=" + mRoom;
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
//            checkDate = checks if the date is from today onwards
            boolean checkDate = true;
            final ArrayList<String> time = new ArrayList<>();

            if(result.equals("")){
                PlaceholderFragment.mListView.setVisibility(View.VISIBLE);
            }
            else {
                System.out.println(result);
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

                if(success == 2){
                    time.add("NO BOOKINGS ALLOWED FOR OLD DATES");
                    checkDate = false;
                }
                else {
                    JSONArray bookings = new JSONArray();
                    try {
                        bookings = jobject.getJSONArray("bookings");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<String> bookingTime = new ArrayList<>();

                    if (bookings == null) {
                        bookingTime.add("00:00:00");
                    } else {
                        for (int i = 0; i < bookings.length(); i++) {
                            JSONArray all_bookings = new JSONArray();
                            try {
                                all_bookings = bookings.getJSONObject(i).getJSONArray("all_bookings");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (int j = 0; j < all_bookings.length(); j++){
                                try {
                                    bookingTime.add(all_bookings.getString(j));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                    Date date = new Date();
                    DateFormat format_time = new SimpleDateFormat("HHmmss");
                    DateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");

                    for (String t : allTime) {
                        if (Integer.parseInt(t.replace(":","")) < Integer.parseInt(format_time.format(date)) && mDate.equals(format_date.format(date))) {
                            continue;
                        }
                        if (bookingTime.contains(t)) {
                            continue;
                        }
                        time.add(t);
                    }
                }
                if (mRoom == 1) {
                    PlaceholderFragment.room1 = time;
                    PlaceholderFragment.checkRoom1 = true;
                    PlaceholderFragment.checkDate1 = false;
                    PlaceholderFragment.adapter1 = new ArrayAdapter<>(getApplicationContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text, PlaceholderFragment.room1);
                    PlaceholderFragment.mListView1.setAdapter(PlaceholderFragment.adapter1);
                    if(checkDate) {
                        PlaceholderFragment.checkDate1 = true;
                        PlaceholderFragment.mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String timeOfBooking = PlaceholderFragment.room1.get(position);
                                Intent i = new Intent(getApplicationContext(), Confirmation.class);
                                i.putExtra("fullname", fullname);
                                i.putExtra("username", username);
                                i.putExtra("date", mDate);
                                i.putExtra("time", timeOfBooking);
                                i.putExtra("room", "1");
                                i.putExtra("displayDate", mDisplayDate);
                                startActivity(i);
                            }
                        });
                    }
                    else{
                        PlaceholderFragment.checkDate1 = false;
                        PlaceholderFragment.mListView1.setOnItemClickListener(null);

                    }
                } else if (mRoom == 2) {
                    PlaceholderFragment.room2 = time;
                    PlaceholderFragment.checkRoom2 = true;
                    PlaceholderFragment.adapter2 = new ArrayAdapter<>(getApplicationContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text, PlaceholderFragment.room2);
                    PlaceholderFragment.mListView2.setAdapter(PlaceholderFragment.adapter2);
                    if(checkDate) {
                        PlaceholderFragment.checkDate2 = true;
                        PlaceholderFragment.mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String timeOfBooking = PlaceholderFragment.room2.get(position);
                                Intent i = new Intent(getApplicationContext(), Confirmation.class);
                                i.putExtra("fullname", fullname);
                                i.putExtra("username", username);
                                i.putExtra("date", mDate);
                                i.putExtra("time", timeOfBooking);
                                i.putExtra("room", "2");
                                i.putExtra("displayDate", mDisplayDate);
                                startActivity(i);
                            }

                        });
                    }
                    else{
                        PlaceholderFragment.checkDate2 = false;
                        PlaceholderFragment.mListView2.setOnItemClickListener(null);
                    }

                } else if (mRoom == 3) {
                    PlaceholderFragment.room3 = time;
                    if (PlaceholderFragment.firstRoom3) {
                        PlaceholderFragment.firstRoom3 = false;
                        PlaceholderFragment.checkRoom3 = true;
                        PlaceholderFragment.checkDate3 = true;
                    } else {
                        try {
                            PlaceholderFragment.checkRoom3 = true;
                            PlaceholderFragment.adapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.new_booking_item_list, R.id.new_booking_list_text, PlaceholderFragment.room3);
                            PlaceholderFragment.mListView3.setAdapter(PlaceholderFragment.adapter3);
                            if(checkDate) {
                                PlaceholderFragment.checkDate3 = true;
                                PlaceholderFragment.mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String timeOfBooking = PlaceholderFragment.room3.get(position);
                                        Intent i = new Intent(getApplicationContext(), Confirmation.class);
                                        i.putExtra("fullname", fullname);
                                        i.putExtra("username", username);
                                        i.putExtra("date", mDate);
                                        i.putExtra("time", timeOfBooking);
                                        i.putExtra("room", "3");
                                        i.putExtra("displayDate", mDisplayDate);
                                        startActivity(i);
                                    }

                                });
                            }
                            else{
                                PlaceholderFragment.checkDate3 = false;
                                PlaceholderFragment.mListView3.setOnItemClickListener(null);
                            }
                        } catch (NullPointerException ignored) { }


                    }


                } else if (mRoom == 4) {
                    PlaceholderFragment.room4 = time;
                    if (PlaceholderFragment.firstRoom4) {
                        PlaceholderFragment.firstRoom4 = false;
                        PlaceholderFragment.checkRoom4 = true;
                        PlaceholderFragment.checkDate4 = true;
                    } else {
                        try {
                            PlaceholderFragment.checkRoom4 = true;
                            PlaceholderFragment.adapter4 = new ArrayAdapter<>(getApplicationContext(),R.layout.new_booking_item_list,R.id.new_booking_list_text, PlaceholderFragment.room4);
                            PlaceholderFragment.mListView4.setAdapter(PlaceholderFragment.adapter4);
                            if(checkDate) {
                                PlaceholderFragment.checkRoom4 = true;
                                PlaceholderFragment.mListView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String timeOfBooking = PlaceholderFragment.room4.get(position);
                                        Intent i = new Intent(getApplicationContext(), Confirmation.class);
                                        i.putExtra("fullname", fullname);
                                        i.putExtra("username", username);
                                        i.putExtra("date", mDate);
                                        i.putExtra("time", timeOfBooking);
                                        i.putExtra("room", "4");
                                        i.putExtra("displayDate", mDisplayDate);
                                        startActivity(i);
                                    }

                                });
                            }
                            else{
                                PlaceholderFragment.checkRoom4 = false;
                                PlaceholderFragment.mListView4.setOnItemClickListener(null);
                            }
                        } catch (NullPointerException ignored) {}
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {

        }


    }
}
