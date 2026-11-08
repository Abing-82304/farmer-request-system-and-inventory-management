package com.example.myapplication;

public class ItemRequest {
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
}

