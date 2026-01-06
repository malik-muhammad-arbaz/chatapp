package com.messageapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.messageapp.R;
import com.messageapp.adapters.MessageAdapter;
import com.messageapp.models.Message;

import java.util.ArrayList;
import java.util.List;

public class HelpBotActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private TextView tvAvatar;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private Handler handler;
    
    private static final String BOT_ID = "HELPBOT";
    private static final String USER_ID = "USER";
    private static final String BOT_NAME = "HelpBot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpbot);

        initViews();
        handler = new Handler(Looper.getMainLooper());
        messages = new ArrayList<>();

        setupRecyclerView();
        showWelcomeMessage();

        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvAvatar = findViewById(R.id.tvAvatar);
        if (tvAvatar != null) {
            tvAvatar.setText("?");
        }
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messages, USER_ID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);
    }

    private void showWelcomeMessage() {
        addBotMessage("👋 Hi! I'm HelpBot, your assistant for MessageApp!\n\nHere's how I can help you:\n\n" +
                "• Type 'start' - How to get started\n" +
                "• Type 'search' - How to find users\n" +
                "• Type 'chat' - How to send messages\n" +
                "• Type 'id' - About your Device ID\n" +
                "• Type 'help' - Show all commands\n\n" +
                "What would you like to know?");
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        
        if (TextUtils.isEmpty(content)) {
            return;
        }

        // Add user message
        Message userMessage = new Message(USER_ID, BOT_ID, content);
        messages.add(userMessage);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        rvMessages.scrollToPosition(messages.size() - 1);
        
        etMessage.setText("");

        // Simulate bot typing delay
        handler.postDelayed(() -> {
            String response = getBotResponse(content.toLowerCase());
            addBotMessage(response);
        }, 500);
    }

    private void addBotMessage(String content) {
        Message botMessage = new Message(BOT_ID, USER_ID, content);
        messages.add(botMessage);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        rvMessages.scrollToPosition(messages.size() - 1);
    }

    private String getBotResponse(String input) {
        if (input.contains("start") || input.contains("begin") || input.contains("new")) {
            return "🚀 **Getting Started**\n\n" +
                    "1️⃣ When you first open the app, you'll be asked to Sign Up\n\n" +
                    "2️⃣ Enter your name and email\n\n" +
                    "3️⃣ You'll receive a unique Device ID (like A1B2-C3D4)\n\n" +
                    "4️⃣ Share this ID with friends so they can message you!\n\n" +
                    "5️⃣ To login on another device, use your Device ID";
        }
        
        if (input.contains("search") || input.contains("find") || input.contains("user")) {
            return "🔍 **Finding Users**\n\n" +
                    "1️⃣ Tap the purple + button on the main screen\n\n" +
                    "2️⃣ Enter the Device ID of the person you want to chat with\n\n" +
                    "3️⃣ The search will show matching users\n\n" +
                    "4️⃣ Tap on a user to start chatting!\n\n" +
                    "💡 Tip: Ask your friends for their Device ID to connect";
        }
        
        if (input.contains("chat") || input.contains("message") || input.contains("send")) {
            return "💬 **Sending Messages**\n\n" +
                    "1️⃣ Find and tap on a user from search results\n\n" +
                    "2️⃣ You'll see the chat screen\n\n" +
                    "3️⃣ Type your message in the text box\n\n" +
                    "4️⃣ Tap the send button (purple arrow)\n\n" +
                    "5️⃣ Your messages appear on the right, received messages on the left\n\n" +
                    "💡 Messages are stored locally on your device";
        }
        
        if (input.contains("id") || input.contains("device")) {
            return "🆔 **About Device ID**\n\n" +
                    "Your Device ID is a unique code like A1B2-C3D4\n\n" +
                    "• It's your identity in MessageApp\n" +
                    "• Share it with friends to let them message you\n" +
                    "• It's shown on your main screen\n" +
                    "• Use it to login on any device\n\n" +
                    "💡 Keep your Device ID safe - it's like your username!";
        }
        
        if (input.contains("help") || input.contains("command")) {
            return "📚 **Available Commands**\n\n" +
                    "• 'start' - How to get started\n" +
                    "• 'search' - How to find users\n" +
                    "• 'chat' - How to send messages\n" +
                    "• 'id' - About Device ID\n" +
                    "• 'help' - Show this list\n\n" +
                    "Just type any keyword and I'll help you!";
        }
        
        if (input.contains("hi") || input.contains("hello") || input.contains("hey")) {
            return "👋 Hello! How can I help you today?\n\nType 'help' to see what I can assist with!";
        }
        
        if (input.contains("thank") || input.contains("thanks")) {
            return "😊 You're welcome! Happy to help!\n\nFeel free to ask if you have more questions!";
        }
        
        if (input.contains("bye") || input.contains("exit") || input.contains("quit")) {
            return "👋 Goodbye! Have a great time using MessageApp!\n\nCome back anytime you need help!";
        }

        return "🤔 I'm not sure about that.\n\nTry typing one of these:\n" +
                "• 'start' - Getting started guide\n" +
                "• 'search' - Find users\n" +
                "• 'chat' - Send messages\n" +
                "• 'id' - Device ID info\n" +
                "• 'help' - All commands";
    }
}
