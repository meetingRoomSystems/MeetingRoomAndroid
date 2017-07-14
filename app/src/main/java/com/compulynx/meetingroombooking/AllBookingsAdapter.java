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
 * Custom adapter for the ListView shown in AllBookings activity
 */

class AllBookingsAdapter extends BaseAdapter{

    private final LayoutInflater mInflater;
    private final ArrayList<Booking> mDataSource;

    AllBookingsAdapter(Context context, ArrayList<Booking> items) {
        mDataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View rowView = mInflater.inflate(R.layout.all_booking_list_item,viewGroup,false);


        ImageView img = (ImageView) rowView.findViewById(R.id.all_booking_img);
        TextView name = (TextView) rowView.findViewById(R.id.all_booking_name);
        TextView time = (TextView) rowView.findViewById(R.id.all_booking_time);
        TextView capacity = (TextView) rowView.findViewById(R.id.all_booking_capacity);
        TextView room = (TextView) rowView.findViewById(R.id.all_booking_room);

        Booking booking = (Booking) getItem(i);
        name.setText("By: " + booking.fullname);
        time.setText("Time: " + booking.bookingTime + "-" + booking.bookingTimeEnd);
        capacity.setText("Capacity: " + booking.capacity);
        room.setText("Room:" + " " + booking.room);
        if(booking.room == 1){
            img.setImageResource(R.drawable.room1);
        }
        else if(booking.room == 2){
            img.setImageResource(R.drawable.room2);
        }
        else if(booking.room == 3){
            img.setImageResource(R.drawable.room3);
        }
        else if(booking.room == 4){
            img.setImageResource(R.drawable.room4);
        }
        return rowView;
    }
}
