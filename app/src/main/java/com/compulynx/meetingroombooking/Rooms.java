package com.compulynx.meetingroombooking;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity shows all the information about the current meeting rooms in a tabbed format
 */

public class Rooms extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private String fullname;
    private String username;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fullname = getIntent().getStringExtra("fullname");
        username = getIntent().getStringExtra("username");

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rooms, container, false);
            ImageView img = (ImageView) rootView.findViewById(R.id.imageView) ;
            TextView room = (TextView) rootView.findViewById(R.id.section_label);
            TextView capacity =  (TextView) rootView.findViewById(R.id.capacity);
            TextView features =  (TextView) rootView.findViewById(R.id.features);

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                room.setText(R.string.room1_room);
                capacity.setText(R.string.room1_capacity);
                features.setText(R.string.room1_features);
                img.setImageResource(R.drawable.room1);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                room.setText(R.string.room2_room);
                capacity.setText(R.string.room2_capacity);
                features.setText(R.string.room2_features);
                img.setImageResource(R.drawable.room2);

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                room.setText(R.string.room3_room);
                capacity.setText(R.string.room3_capacity);
                features.setText(R.string.room3_features);
                img.setImageResource(R.drawable.room3);

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 4){
                room.setText(R.string.room4_room);
                capacity.setText(R.string.room4_capacity);
                features.setText(R.string.room4_features);
                img.setImageResource(R.drawable.room4);

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
            return PlaceholderFragment.newInstance(position + 1);
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
}
