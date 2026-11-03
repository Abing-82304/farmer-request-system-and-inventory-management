package com.example.farmersportal; // TODO: change to your app's actual package name

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Sign-in screen for the Farmers Monitoring and Request Portal System.
 *
 * Two account types share this one screen: Administrator and Farm Employee.
 * The selected role is sent alongside the credentials so the backend can route
 * the user to the correct dashboard after authentication.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "farmers_portal_prefs";
    private static final String KEY_REMEMBERED_USERNAME = "remembered_username";
    private static final String KEY_ROLE = "selected_role";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_EMPLOYEE = "employee";

    private TextView btnRoleAdmin, btnRoleEmployee;
    private TextView tvError, tvForgotPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialCheckBox cbRemember;
    private Button btnSignIn;
    private ProgressBar progressSignIn;

    private String selectedRole = ROLE_ADMIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
        restoreRememberedUsername();
        setupRoleToggle();
        setupForgotPassword();
        setupSignIn();
    }

    private void bindViews() {
        btnRoleAdmin = findViewById(R.id.btnRoleAdmin);
        btnRoleEmployee = findViewById(R.id.btnRoleEmployee);
        tvError = findViewById(R.id.tvError);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressSignIn = findViewById(R.id.progressSignIn);
    }

    // ------------------------------------------------------------------
    // Role toggle (Administrator / Farm Employee)
    // ------------------------------------------------------------------

    private void setupRoleToggle() {
        btnRoleAdmin.setOnClickListener(v -> selectRole(ROLE_ADMIN));
        btnRoleEmployee.setOnClickListener(v -> selectRole(ROLE_EMPLOYEE));
    }

    private void selectRole(String role) {
        selectedRole = role;

        boolean isAdmin = role.equals(ROLE_ADMIN);

        btnRoleAdmin.setBackgroundResource(isAdmin ? R.drawable.bg_role_pill_active : 0);
        btnRoleAdmin.setTextColor(getColorRes(isAdmin ? R.color.green_800 : R.color.ink_soft));

        btnRoleEmployee.setBackgroundResource(!isAdmin ? R.drawable.bg_role_pill_active : 0);
        btnRoleEmployee.setTextColor(getColorRes(!isAdmin ? R.color.green_800 : R.color.ink_soft));
    }

    // ------------------------------------------------------------------
    // Forgot password
    // ------------------------------------------------------------------

    private void setupForgotPassword() {
        tvForgotPassword.setOnClickListener(v -> {
            // TODO: launch your ForgotPasswordActivity, or open a reset-link flow
            Toast.makeText(this, "Forgot password flow goes here", Toast.LENGTH_SHORT).show();
        });
    }

    // ------------------------------------------------------------------
    // Sign in
    // ------------------------------------------------------------------

    private void setupSignIn() {
        btnSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        hideError();

        String username = safeText(etUsername);
        String password = safeText(etPassword);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showError(getString(R.string.error_empty_fields));
            return;
        }

        setLoading(true);

        // TODO: replace this stub with a real network call to your Flask backend, e.g.:
        //
        //   POST /api/v1/auth/login
        //   body: { "username": username, "password": password, "role": selectedRole }
        //
        // Using Retrofit/OkHttp, on success:
        //   - store the returned JWT (e.g. in EncryptedSharedPreferences)
        //   - navigate to AdminDashboardActivity or EmployeeDashboardActivity based on role
        // on failure (401/invalid credentials):
        //   - setLoading(false); showError(getString(R.string.error_login_failed));

        simulateNetworkLogin(username, password);
    }

    /**
     * Placeholder so the screen is runnable out of the box.
     * Delete this method once real authentication is wired up.
     */
    private void simulateNetworkLogin(String username, String password) {
        btnSignIn.postDelayed(() -> {
            setLoading(false);

            if (cbRemember.isChecked()) {
                rememberUsername(username);
            }

            Toast.makeText(
                    this,
                    "Signed in as " + username + " (" + selectedRole + ")",
                    Toast.LENGTH_SHORT
            ).show();

            // TODO: route by role once real dashboards exist, e.g.:
            // Intent intent = new Intent(this,
            //         selectedRole.equals(ROLE_ADMIN) ? AdminDashboardActivity.class : EmployeeDashboardActivity.class);
            // startActivity(intent);
            // finish();

        }, 900);
    }

    // ------------------------------------------------------------------
    // UI helpers
    // ------------------------------------------------------------------

    private void setLoading(boolean loading) {
        btnSignIn.setEnabled(!loading);
        btnSignIn.setText(loading ? getString(R.string.signing_in) : getString(R.string.sign_in));
        progressSignIn.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private String safeText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private int getColorRes(int colorResId) {
        return getResources().getColor(colorResId, getTheme());
    }

    // ------------------------------------------------------------------
    // Remember-me persistence (username only — never store the password)
    // ------------------------------------------------------------------

    private void rememberUsername(String username) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_REMEMBERED_USERNAME, username)
                .putString(KEY_ROLE, selectedRole)
                .apply();
    }

    private void restoreRememberedUsername() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String remembered = prefs.getString(KEY_REMEMBERED_USERNAME, null);
        String rememberedRole = prefs.getString(KEY_ROLE, ROLE_ADMIN);

        if (remembered != null) {
            etUsername.setText(remembered);
            cbRemember.setChecked(true);
        }
        selectRole(rememberedRole); // sets the default toggle state either way
    }
}
