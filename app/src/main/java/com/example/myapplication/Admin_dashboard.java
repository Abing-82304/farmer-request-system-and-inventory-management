package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class Admin_dashboard extends AppCompatActivity {

    MaterialCardView cardEmployees;

    MaterialCardView cardInventory;

    MaterialCardView cardActivities;

    MaterialCardView cardRequests;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        cardEmployees = findViewById(R.id.cardEmployees);
        cardInventory = findViewById(R.id.cardInventory);
        cardActivities = findViewById(R.id.cardActivities);
        cardRequests = findViewById(R.id.cardRequests);



        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {

                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return windowInsets;
                }
        );


        cardEmployees.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_dashboard.this,
                    Employees.class);
            startActivity(intent);
        });

        cardInventory.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_dashboard.this,
                    Inventory.class);
            startActivity(intent);
        });

        cardActivities.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_dashboard.this,
                    activities.class);
            startActivity(intent);
        });

        cardRequests.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_dashboard.this,
                    RequestActivity.class);
            startActivity(intent);
        });


    }
}