package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.google.android.material.card.MaterialCardView;

public class main_ui extends AppCompatActivity {
    MaterialCardView cardAdmin;
    MaterialCardView cardEmployee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        cardAdmin = findViewById(R.id.cardAdmin);
        cardEmployee = findViewById(R.id.cardEmployee);

        cardAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(main_ui.this,
                    admin_login.class);
            startActivity(intent);
        });

        cardEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(main_ui.this,
                    employee_login.class);
            startActivity(intent);
        });




    }
}