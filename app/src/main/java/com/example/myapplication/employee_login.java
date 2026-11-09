    package com.example.myapplication;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.Button;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import com.google.android.material.appbar.MaterialToolbar;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class employee_login extends AppCompatActivity {

        EditText etEmployeeUsername;
        EditText etEmployeePassword;

        Button btnEmployeeLogin;

        ApiService apiService;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_employee_login);


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

            etEmployeeUsername = findViewById(R.id.etEmployeeUsername);
            etEmployeePassword = findViewById(R.id.etEmployeePassword);
            btnEmployeeLogin = findViewById(R.id.btnEmployeeLogin);

            // ==========================
            // RETROFIT
            // ==========================

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl( "http://100.72.152.12:8000/")
//                    .baseUrl("http://10.5.50.38:8000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);

            btnEmployeeLogin.setOnClickListener(v -> {

                String username = etEmployeeUsername.getText().toString().trim();
                String password = etEmployeePassword.getText().toString().trim();

                if (username.isEmpty()) {
                    etEmployeeUsername.setError("Enter your email");
                    return;
                }
                if (password.isEmpty()) {
                    etEmployeePassword.setError("Enter your password");
                    return;
                }

                loginEmployee(username, password);
            });
        }
        private void loginEmployee(String username, String password) {

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
                                            && "employee".equalsIgnoreCase(
                                            loginResponse
                                                    .getUser()
                                                    .getRole())) {


                                        Toast.makeText(
                                                employee_login.this,
                                                "Login successful",
                                                Toast.LENGTH_SHORT
                                        ).show();


                                        getSharedPreferences("farmate", MODE_PRIVATE)
                                                .edit()
                                                .putInt("employee_id", loginResponse.getUser().getId())
                                                .apply();

                                        Intent intent = new Intent(employee_login.this, employee_dashboard.class);
                                        startActivity(intent);
                                        finish();

                                    } else {

                                        Toast.makeText(
                                                employee_login.this,
                                                "This account is not an employee.",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }

                                } else {

                                    Toast.makeText(
                                            employee_login.this,
                                            loginResponse.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } else {

                                Toast.makeText(
                                        employee_login.this,
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
                                    employee_login.this,
                                    "Connection error: "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });


        }
    }