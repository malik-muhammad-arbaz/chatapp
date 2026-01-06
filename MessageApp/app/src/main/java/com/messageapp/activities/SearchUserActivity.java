package com.messageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.messageapp.R;
import com.messageapp.adapters.UserAdapter;
import com.messageapp.database.UserDAO;
import com.messageapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private EditText etSearch;
    private ImageButton btnBack;
    private RecyclerView rvUsers;
    private LinearLayout noResultsLayout;
    private UserAdapter userAdapter;
    private UserDAO userDAO;
    private String currentDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        initViews();

        userDAO = new UserDAO(this);

        SharedPreferences prefs = getSharedPreferences("MessageApp", MODE_PRIVATE);
        currentDeviceId = prefs.getString("device_id", "");

        setupRecyclerView();
        setupSearch();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        rvUsers = findViewById(R.id.rvUsers);
        noResultsLayout = findViewById(R.id.noResultsLayout);
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(new ArrayList<>(), this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toUpperCase();
                if (query.isEmpty()) {
                    userAdapter.updateUsers(new ArrayList<>());
                    if (noResultsLayout != null) {
                        noResultsLayout.setVisibility(View.GONE);
                    }
                } else {
                    searchUsers(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchUsers(String query) {
        List<User> results = userDAO.searchUsersByDeviceId(query);
        
        // Remove current user from results
        List<User> filteredResults = new ArrayList<>();
        for (User user : results) {
            if (!user.getDeviceId().equals(currentDeviceId)) {
                filteredResults.add(user);
            }
        }

        if (filteredResults.isEmpty()) {
            if (noResultsLayout != null) {
                noResultsLayout.setVisibility(View.VISIBLE);
            }
            rvUsers.setVisibility(View.GONE);
        } else {
            if (noResultsLayout != null) {
                noResultsLayout.setVisibility(View.GONE);
            }
            rvUsers.setVisibility(View.VISIBLE);
            userAdapter.updateUsers(filteredResults);
        }
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receiver_device_id", user.getDeviceId());
        intent.putExtra("receiver_name", user.getName());
        startActivity(intent);
        finish();
    }
}
