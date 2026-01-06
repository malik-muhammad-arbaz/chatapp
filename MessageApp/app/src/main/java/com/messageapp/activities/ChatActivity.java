package com.messageapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.messageapp.R;
import com.messageapp.adapters.MessageAdapter;
import com.messageapp.database.MessageDAO;
import com.messageapp.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private TextView tvReceiverName, tvReceiverDeviceId, tvAvatar;
    private MessageAdapter messageAdapter;
    private MessageDAO messageDAO;
    private String currentDeviceId;
    private String receiverDeviceId;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();

        messageDAO = new MessageDAO(this);

        // Get current user
        SharedPreferences prefs = getSharedPreferences("MessageApp", MODE_PRIVATE);
        currentDeviceId = prefs.getString("device_id", "");

        // Get receiver info from intent
        receiverDeviceId = getIntent().getStringExtra("receiver_device_id");
        receiverName = getIntent().getStringExtra("receiver_name");

        tvReceiverName.setText(receiverName);
        tvReceiverDeviceId.setText(receiverDeviceId);
        
        // Set avatar initial
        if (tvAvatar != null && receiverName != null && !receiverName.isEmpty()) {
            tvAvatar.setText(String.valueOf(receiverName.charAt(0)).toUpperCase());
        }

        setupRecyclerView();
        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());

        // Mark messages as read
        messageDAO.markMessagesAsRead(receiverDeviceId, currentDeviceId);
    }

    private void initViews() {
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverDeviceId = findViewById(R.id.tvReceiverDeviceId);
        tvAvatar = findViewById(R.id.tvAvatar);
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(new ArrayList<>(), currentDeviceId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        List<Message> messages = messageDAO.getConversation(currentDeviceId, receiverDeviceId);
        messageAdapter.updateMessages(messages);
        
        // Scroll to bottom
        if (!messages.isEmpty()) {
            rvMessages.scrollToPosition(messages.size() - 1);
        }
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        
        if (TextUtils.isEmpty(content)) {
            return;
        }

        Message message = new Message(currentDeviceId, receiverDeviceId, content);
        long result = messageDAO.insertMessage(message);

        if (result > 0) {
            etMessage.setText("");
            loadMessages();
        }
    }
}
