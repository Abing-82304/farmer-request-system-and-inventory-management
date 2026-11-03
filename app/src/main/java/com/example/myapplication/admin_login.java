package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class admin_login extends AppCompatActivity {

    EditText etAdminEmail;
    EditText etAdminPassword;
    Button btnAdminLogin;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_admin_login);

        // ==========================
        // EDGE-TO-EDGE / KEYBOARD
        // ==========================

        View root = findViewById(R.id.loginRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {

            Insets imeInsets =
                    windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            Insets systemBars =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            int bottomPadding = Math.max(
                    imeInsets.bottom,
                    systemBars.bottom
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    bottomPadding
            );

            return windowInsets;
        });


        // ==========================
        // FIND UI COMPONENTS
        // ==========================

        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);


        // ==========================
        // RETROFIT
        // ==========================

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.254.102:8000/")
                .baseUrl("http://192.168.254.100:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);


        // ==========================
        // LOGIN BUTTON
        // ==========================

        btnAdminLogin.setOnClickListener(v -> {

            String email = etAdminEmail.getText()
                    .toString()
                    .trim();

            String password = etAdminPassword.getText()
                    .toString()
                    .trim();


            if (email.isEmpty()) {

                etAdminEmail.setError("Enter your username");
                return;
            }


            if (password.isEmpty()) {

                etAdminPassword.setError("Enter your password");
                return;
            }


            loginAdmin(email, password);
        });
    }


    // ==========================
    // LOGIN WITH DENO API
    // ==========================

    private void loginAdmin(String username, String password) {

        LoginRequest request =
                new LoginRequest(username, password);


        apiService.login(request)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response) {


                        if (response.isSuccessful()
                                && response.body() != null) {

                            LoginResponse loginResponse =
                                    response.body();


                            if (loginResponse.isSuccess()) {

                                // Check that this is actually an ADMIN
                                if (loginResponse.getUser() != null
                                        && "ADMIN".equalsIgnoreCase(
                                        loginResponse
                                                .getUser()
                                                .getRole())) {


                                    Toast.makeText(
                                            admin_login.this,
                                            "Login successful",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    Intent intent =
                                            new Intent(
                                                    admin_login.this,
                                                    Admin_dashboard.class
                                            );

                                    startActivity(intent);

                                    finish();

                                } else {

                                    Toast.makeText(
                                            admin_login.this,
                                            "This account is not an administrator.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } else {

                                Toast.makeText(
                                        admin_login.this,
                                        loginResponse.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                        } else {

                            Toast.makeText(
                                    admin_login.this,
                                    "Invalid username or password",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<LoginResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                admin_login.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}