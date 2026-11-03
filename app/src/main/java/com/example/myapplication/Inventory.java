package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inventory extends AppCompatActivity {

    private TableLayout table;

    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Must be the layout file that contains your TableLayout with id "table"
        setContentView(R.layout.activity_inventory);
         add = findViewById(R.id.add);

        add.setOnClickListener(v -> {
            Intent intent = new Intent(Inventory.this,
                    ItemFormActivity.class);
            startActivity(intent);
        });

        table = findViewById(R.id.table);
        loadData();
    }


    public void onResponse(Call<Void> call, Response<Void> res) {
        if (res.isSuccessful()) {
            Toast.makeText(Inventory.this, "Updated!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(Inventory.this, "Server error: " + res.code(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadData() {
        RetrofitClient.getApi().getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showItems(response.body());
                } else {
                    Toast.makeText(Inventory.this,
                            "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(Inventory.this,
                        "Can't reach server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showItems(List<Item> items) {
        // Keep the first 2 rows (title + header), remove any old data rows
        int keep = 2;
        if (table.getChildCount() > keep) {
            table.removeViews(keep, table.getChildCount() - keep);
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);
            row.setPadding(dp(3), dp(3), dp(3), dp(3));
            row.setBackgroundColor(Color.parseColor(i % 2 == 0 ? "#bdc3c7" : "#ffffff"));

            row.addView(makeCell(String.valueOf(item.getId())));
            row.addView(makeCell(item.getName()));
            row.addView(makeCell((item.getQuantity() + " " + item.getUnit()).trim()));
            row.addView(makeCell(item.isAvailable() ? "Yes" : "No"));

            table.addView(row);
        }
    }

    private TextView makeCell(String text) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTypeface(Typeface.SERIF);
        return tv;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}