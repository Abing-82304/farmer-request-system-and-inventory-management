package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class activities extends AppCompatActivity {

    private ImageButton menuButton;
    private TextInputEditText etSearchEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activities);

        menuButton = findViewById(R.id.menuButton);
        etSearchEmployee = findViewById(R.id.etSearchEmployee);

        // Back navigation to Admin Dashboard
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> finish());
        }

        // Live Search / Filter setup for reports
        if (etSearchEmployee != null) {
            etSearchEmployee.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterReports(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filterReports(String query) {
        // Real-time query filtering for report logs
    }
}
