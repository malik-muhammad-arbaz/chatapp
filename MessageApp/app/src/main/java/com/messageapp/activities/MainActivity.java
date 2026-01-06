package com.messageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.messageapp.R;
import com.messageapp.adapters.UserAdapter;
import com.messageapp.database.MessageDAO;
import com.messageapp.database.UserDAO;
import com.messageapp.models.Message;
import com.messageapp.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private RecyclerView rvConversations;
    private TextView tvDeviceId, tvUserName;
    private LinearLayout emptyLayout;
    private FloatingActionButton fabSearch;
    private UserAdapter userAdapter;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private String currentDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        userDAO = new UserDAO(this);
        messageDAO = new MessageDAO(this);

        // Get current user
        SharedPreferences prefs = getSharedPreferences("MessageApp", MODE_PRIVATE);
        currentDeviceId = prefs.getString("device_id", "");
        String userName = prefs.getString("user_name", "User");

        tvDeviceId.setText("ID: " + currentDeviceId);
        tvUserName.setText("Welcome, " + userName);

        setupRecyclerView();

        fabSearch.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });
        
        // Help FAB - opens HelpBot
        FloatingActionButton fabHelp = findViewById(R.id.fabHelp);
        if (fabHelp != null) {
            fabHelp.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, HelpBotActivity.class));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void initViews() {
        rvConversations = findViewById(R.id.rvConversations);
        emptyLayout = findViewById(R.id.emptyLayout);
        tvDeviceId = findViewById(R.id.tvDeviceId);
        tvUserName = findViewById(R.id.tvUserName);
        fabSearch = findViewById(R.id.fabSearch);
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(new ArrayList<>(), this);
        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        rvConversations.setAdapter(userAdapter);
    }

    private void loadConversations() {
        List<Message> recentMessages = messageDAO.getRecentConversations(currentDeviceId);
        Set<String> conversationPartners = new HashSet<>();
        
        for (Message msg : recentMessages) {
            if (msg.getSenderId().equals(currentDeviceId)) {
                conversationPartners.add(msg.getReceiverId());
            } else {
                conversationPartners.add(msg.getSenderId());
            }
        }

        List<User> users = new ArrayList<>();
        for (String partnerId : conversationPartners) {
            User user = userDAO.getUserByDeviceId(partnerId);
            if (user != null) {
                users.add(user);
            }
        }

        if (users.isEmpty()) {
            if (emptyLayout != null) {
                emptyLayout.setVisibility(View.VISIBLE);
            }
            rvConversations.setVisibility(View.GONE);
        } else {
            if (emptyLayout != null) {
                emptyLayout.setVisibility(View.GONE);
            }
            rvConversations.setVisibility(View.VISIBLE);
            userAdapter.updateUsers(users);
        }
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receiver_device_id", user.getDeviceId());
        intent.putExtra("receiver_name", user.getName());
        startActivity(intent);
    }
}
