package com.messageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.messageapp.R;
import com.messageapp.database.UserDAO;
import com.messageapp.models.User;
import com.messageapp.utils.DeviceIdGenerator;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail;
    private Button btnSignUp;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        userDAO = new UserDAO(this);

        btnSignUp.setOnClickListener(v -> handleSignUp());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Check if email already exists
        if (userDAO.emailExists(email)) {
            etEmail.setError("Email already registered");
            etEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);

        // Generate unique device ID
        String deviceId;
        do {
            deviceId = DeviceIdGenerator.generateDeviceId();
        } while (userDAO.deviceIdExists(deviceId));

        // Create and save user
        User user = new User(deviceId, name, email);
        long result = userDAO.insertUser(user);

        progressBar.setVisibility(View.GONE);
        btnSignUp.setEnabled(true);

        if (result > 0) {
            // Save device ID to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("MessageApp", MODE_PRIVATE);
            prefs.edit()
                    .putString("device_id", deviceId)
                    .putString("user_name", name)
                    .putString("user_email", email)
                    .apply();

            // Show device ID to user
            showDeviceIdDialog(deviceId);
        } else {
            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeviceIdDialog(String deviceId) {
        new AlertDialog.Builder(this)
                .setTitle("Registration Successful!")
                .setMessage("Your unique Device ID is:\n\n" + deviceId + "\n\nPlease save this ID. Others can use it to find and message you.")
                .setPositiveButton("Continue", (dialog, which) -> {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setCancelable(false)
                .show();
    }
}
