package com.example.myapplication;

import com.google.gson.annotations.SerializedName;


public class Item {

    @SerializedName("id")
    private int stock_id;

    @SerializedName("name")
    private String name;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("available")
    private boolean available;

    @SerializedName("unit")
    private String Unit;

    public int getId() { return stock_id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    public String getUnit(){return Unit; }
    public boolean isAvailable() { return available; }
}
