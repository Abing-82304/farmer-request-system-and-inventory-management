package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class request_form extends AppCompatActivity {

    // NO trailing slash here
    private static final String BASE_URL = "http://100.72.152.12:8000";

    private EditText etItemName, etQuantity, etFarmArea, etPurpose;
    private Spinner spinnerUnit;
    private Button btnClear, btnSubmit;
    private int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);

        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etFarmArea = findViewById(R.id.etFarmArea);
        etPurpose = findViewById(R.id.etPurpose);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnClear = findViewById(R.id.btnClear);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Numeric user id passed from the login screen
        employeeId = getSharedPreferences("farmate", MODE_PRIVATE)
                .getInt("employee_id", -1);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> clearAll());
        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void clearAll() {
        etItemName.setText("");
        etQuantity.setText("");
        etFarmArea.setText("");
        etPurpose.setText("");
        spinnerUnit.setSelection(0);
    }

    private void submitRequest() {
        if (employeeId <= 0) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemName = etItemName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();

        double quantity;
        try {
            quantity = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            quantity = 0;
        }

        if (itemName.isEmpty() || quantity <= 0) {
            Toast.makeText(this, "Enter an item name and a valid quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final double qty = quantity;
        final String unit = spinnerUnit.getSelectedItem().toString();
        final String farmArea = etFarmArea.getText().toString().trim();
        final String purpose = etPurpose.getText().toString().trim();
        final String name = itemName;

        btnSubmit.setEnabled(false);

        // Network calls must NOT run on the main thread
        new Thread(() -> {
            try {
                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                JSONObject json = new JSONObject();
                json.put("item_name", name);
                json.put("quantity", qty);
                json.put("unit", unit);
                json.put("farm_area", farmArea);
                json.put("purpose", purpose);
                json.put("employee_id", employeeId);
                json.put("created_at", currentDateTime);
                json.put("date_time", currentDateTime);

                URL url = new URL(BASE_URL + "/api/request");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }

                final int code = conn.getResponseCode();
                conn.disconnect();

                runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    if (code == 201) {
                        Toast.makeText(this, "Request submitted!", Toast.LENGTH_SHORT).show();
                        clearAll();
                    } else {
                        Toast.makeText(this, "Server error (" + code + ")",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Cannot reach the server", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}