package com.messageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.messageapp.R;
import com.messageapp.database.UserDAO;
import com.messageapp.models.User;
import com.messageapp.utils.DeviceIdGenerator;

public class LoginActivity extends AppCompatActivity {

    private EditText etDeviceId;
    private Button btnLogin;
    private TextView tvSignUp;
    private ProgressBar progressBar;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        userDAO = new UserDAO(this);

        btnLogin.setOnClickListener(v -> handleLogin());
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void initViews() {
        etDeviceId = findViewById(R.id.etDeviceId);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void handleLogin() {
        String deviceId = etDeviceId.getText().toString().trim().toUpperCase();

        // Validate input
        if (TextUtils.isEmpty(deviceId)) {
            etDeviceId.setError("Device ID is required");
            etDeviceId.requestFocus();
            return;
        }

        if (!DeviceIdGenerator.isValidDeviceId(deviceId)) {
            etDeviceId.setError("Invalid Device ID format (e.g., A1B2-C3D4)");
            etDeviceId.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Check if user exists
        User user = userDAO.getUserByDeviceId(deviceId);

        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        if (user != null) {
            // Save to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("MessageApp", MODE_PRIVATE);
            prefs.edit()
                    .putString("device_id", user.getDeviceId())
                    .putString("user_name", user.getName())
                    .putString("user_email", user.getEmail())
                    .apply();

            // Navigate to main
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Device ID not found. Please sign up first.", Toast.LENGTH_LONG).show();
        }
    }
}
