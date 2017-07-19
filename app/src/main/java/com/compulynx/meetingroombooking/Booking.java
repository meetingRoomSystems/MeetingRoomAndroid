package com.compulynx.meetingroombooking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Booking class to store booking information's
 */

class Booking {
    public String fullname;
    private String username;
    public int capacity;
    public int room;
    public int reminder;
    public int duration;
    String bookingDate;
    String bookingTime;
    String bookingTimeEnd;
    String others;
    JSONArray allBookings;

    //return a ArrayList of all bookings from a JSON data
    static ArrayList<Booking> getBookings(String jsonData){
        final ArrayList<Booking> bookingList = new ArrayList<>();

        try {
            // Load data
            JSONObject json = new JSONObject(jsonData);
            JSONArray bookings = json.getJSONArray("bookings");

            // Get booking objects from data
            for(int i = 0; i < bookings.length(); i++){
                Booking book = new Booking();

                try {
                    book.fullname = bookings.getJSONObject(i).getString("fullname");
                }
                catch (Exception ignored){}

                try {
                    book.username = bookings.getJSONObject(i).getString("username");
                }
                catch (Exception ignored){}
                book.capacity = bookings.getJSONObject(i).getInt("capacity");
                book.room = bookings.getJSONObject(i).getInt("room");
                book.bookingDate = bookings.getJSONObject(i).getString("booking_date");
                book.bookingTime = bookings.getJSONObject(i).getString("booking_start");
                try {
                    book.bookingTimeEnd = bookings.getJSONObject(i).getString("booking_end");
                }
                catch (Exception ignored){}
                try {
                    book.duration = bookings.getJSONObject(i).getInt("duration");
                }
                catch (Exception ignored){}
                try {
                    book.allBookings = bookings.getJSONObject(i).getJSONArray("all_bookings");
                }
                catch (Exception ignored){}
                try {
                    book.reminder = bookings.getJSONObject(i).getInt("reminder");
                }
                catch (Exception ignored){}
                try {
                    book.others = bookings.getJSONObject(i).getString("others");
                }
                catch (Exception ignored){}

                bookingList.add(book);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return bookingList;
    }
}

