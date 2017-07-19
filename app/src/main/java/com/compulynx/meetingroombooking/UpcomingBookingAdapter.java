package com.compulynx.meetingroombooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Custom adapter to show upcoming bookings in the ListView shown in HomePageUser activity
 */

class UpcomingBookingAdapter extends BaseAdapter{

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<Booking> mDataSource;

    UpcomingBookingAdapter(Context context, ArrayList<Booking> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = mInflater.inflate(R.layout.upcoming_booking_list_item,viewGroup,false);

        TextView upcoming_time = (TextView) rowView.findViewById(R.id.upcoming_time);
        TextView upcoming_date = (TextView) rowView.findViewById(R.id.upcoming_date);
        TextView upcoming_room = (TextView) rowView.findViewById(R.id.upcoming_room);
        ImageView upcoming_img = (ImageView) rowView.findViewById(R.id.upcoming_img);

        Booking booking = (Booking) getItem(i);
        upcoming_date.setText("Date: " + booking.bookingDate);
        upcoming_time.setText("Time: " + booking.bookingTime + "-" + booking.bookingTimeEnd);
        upcoming_room.setText("Room:" + " " + booking.room);

        if(booking.room == 1){
            upcoming_img.setImageResource(R.drawable.room1);
        }
        else if(booking.room == 2){
            upcoming_img.setImageResource(R.drawable.room2);
        }
        else if(booking.room == 3){
            upcoming_img.setImageResource(R.drawable.room3);
        }
        else if(booking.room == 4){
            upcoming_img.setImageResource(R.drawable.room4);
        }
        return rowView;
    }
}
