package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemRequest1 {
    public int id;
    public int employee_id;
    public String employee_name; // Maps from your Deno SELECT join
    public String item_name;
    public double quantity;
    public String unit;
    public String farm_area;
    public String purpose;
    public String status;
    public String admin_remarks; // Updated to match your database field name
    public String created_at;
    public String date_time;

    public String getFormattedDateTime() {
        String dt = (created_at != null && !created_at.trim().isEmpty())
                ? created_at
                : date_time;

        if (dt == null || dt.trim().isEmpty()) {
            return "";
        }

        try {
            if (dt.contains("T")) {
                String clean = dt.substring(0, 19).replace("T", " ");
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = input.parse(clean);
                if (date != null) {
                    SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
                    return output.format(date);
                }
            } else if (dt.length() >= 19) {
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = input.parse(dt.substring(0, 19));
                if (date != null) {
                    SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
                    return output.format(date);
                }
            }
        } catch (Exception ignored) {
        }
        return dt;
    }
}

