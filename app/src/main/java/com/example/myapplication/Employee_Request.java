
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Employee_Request extends AppCompatActivity {

    private static final String BASE_URL =
            "http://100.72.152.12:8000";

    private RecyclerView recyclerEmployeeRequests;
    private Button add_request;

    private TextView ppeCount;
    private TextView irrigCount;
    private TextView animalCount;
    private TextView otherCount;

    private EmployeeRequestAdapter employeeRequestAdapter;
    private List<ItemRequest1> employeeRequestList;

    private int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_request);

        add_request = findViewById(R.id.add_request);

        ppeCount = findViewById(R.id.ppeCount);
        irrigCount = findViewById(R.id.irrigCount);
        animalCount = findViewById(R.id.animalCount);
        otherCount = findViewById(R.id.otherCount);

        recyclerEmployeeRequests =
                findViewById(R.id.recyclerEmployeeRequests);

        recyclerEmployeeRequests.setLayoutManager(
                new LinearLayoutManager(this));

        employeeRequestList = new ArrayList<>();

        employeeRequestAdapter =
                new EmployeeRequestAdapter(employeeRequestList);

        recyclerEmployeeRequests.setAdapter(employeeRequestAdapter);

        employeeId = getSharedPreferences("farmate", MODE_PRIVATE)
                .getInt("employee_id", -1);

        add_request.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Employee_Request.this,
                    request_form.class
            );
            startActivity(intent);
        });

        loadEmployeeRequests();
    }

    // Refresh dashboard whenever the employee returns to this screen
    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerEmployeeRequests != null && employeeId > 0) {
            loadEmployeeRequests();
        }
    }

    private void loadEmployeeRequests() {

        if (employeeId <= 0) {
            Toast.makeText(this,
                    "Please log in first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {

            HttpURLConnection conn = null;

            try {
                URL url = new URL(
                        BASE_URL + "/api/employee/requests?employee_id="
                                + employeeId
                );

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();

                InputStream stream = responseCode >= 200
                        && responseCode < 300
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                StringBuilder response = new StringBuilder();

                if (stream != null) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(stream)
                    );

                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                }

                if (responseCode < 200 || responseCode >= 300) {
                    throw new Exception(
                            "Server returned " + responseCode
                                    + ": " + response
                    );
                }

                JSONArray requests = new JSONArray(response.toString());

                List<ItemRequest1> newList = new ArrayList<>();

                int approved = 0;
                int declined = 0;
                int pending = 0;

                for (int i = 0; i < requests.length(); i++) {

                    JSONObject obj = requests.getJSONObject(i);

                    ItemRequest1 item = new ItemRequest1();
                    item.id = obj.optInt("id");
                    item.item_name = obj.optString("item_name", "");
                    item.quantity = obj.optDouble("quantity", 0.0);
                    item.unit = obj.optString("unit", "");
                    item.farm_area = obj.optString("farm_area", "");
                    item.purpose = obj.optString("purpose", "");
                    item.status = obj.optString("status", "PENDING");
                    item.admin_remarks = obj.optString("admin_remarks", "");
                    item.created_at = obj.optString("created_at", obj.optString("date_time", obj.optString("request_date", "")));
                    item.date_time = item.created_at;

                    newList.add(item);

                    switch (item.status.toUpperCase()) {
                        case "APPROVED":
                            approved++;
                            break;

                        case "DECLINED":
                        case "REJECTED":
                            declined++;
                            break;

                        case "PENDING":
                            pending++;
                            break;
                    }
                }

                final int finalApproved = approved;
                final int finalDeclined = declined;
                final int finalPending = pending;

                runOnUiThread(() -> {

                    employeeRequestList.clear();
                    employeeRequestList.addAll(newList);

                    employeeRequestAdapter.notifyDataSetChanged();

                    ppeCount.setText(String.valueOf(finalApproved));
                    irrigCount.setText(String.valueOf(finalDeclined));
                    animalCount.setText(String.valueOf(finalPending));
                    otherCount.setText(String.valueOf(newList.size()));
                });

            } catch (Exception e) {

                Log.e(
                        "EmployeeRequests",
                        "Failed to load requests",
                        e
                );

                runOnUiThread(() ->
                        Toast.makeText(
                                Employee_Request.this,
                                "Unable to load requests: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

        }).start();
    }
}
