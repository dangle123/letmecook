package com.example.letmecook.Activity;

import static java.security.AccessController.getContext;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.letmecook.Model.ChatbotHelper;
import com.example.letmecook.Model.Message;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private EditText edtUserInput;
    private TextView txtBotResponse;
    private Button btnSend;
    private FirebaseFirestore db;
   private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private ChatbotHelper chatbotHelper;
    private String conversation;
   private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        getWindow().setStatusBarColor(Color.parseColor("#EE7600"));

        edtUserInput = findViewById(R.id.edtUserInput);
        txtBotResponse = findViewById(R.id.txtBotResponse);
        btnSend = findViewById(R.id.btnSend);
        chatbotHelper = new ChatbotHelper();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        recyclerView  = findViewById(R.id.recyclerviewChat);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager MessageAdapter = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(this, messageList);
        recyclerView.setAdapter(messageAdapter);

        loadMessage();

        edtUserInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.postDelayed(() -> {
                        v.getParent().requestChildFocus(v, v);
                    }, 200);
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = edtUserInput.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    edtUserInput.setText("");
                    SaveUserMessage(userMessage,conversation);
                    chatbotHelper.sendMessage(userMessage, new ChatbotHelper.ChatbotCallback() {
                        @Override
                        public void onSuccess(String response) {

                            SaveBotMessage(conversation,response);
                         //   txtBotResponse.setText(response);

                        }



                        @Override
                        public void onFailure(String error) {
                            txtBotResponse.setText("Lỗi: " + error);
                        }
                    });
                }
            }


        });
    }

    private void SaveBotMessage(String conversation, String response) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        db = FirebaseFirestore.getInstance();
        Log.e("FirebaseChat", "conversation ID: " + conversation);
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", "Bot");
        messageData.put("text", response);
        messageData.put("timestamp", System.currentTimeMillis());

        db.collection("conversations").document(conversation)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    loadMessage();
                })
                .addOnFailureListener(e -> {

                });
    }
    private void SaveUserMessage(String userMessage, String conversation) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        db = FirebaseFirestore.getInstance();
        Log.e("FirebaseChat", "conversation ID: " + conversation);
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", "User");
        messageData.put("text", userMessage);
        messageData.put("timestamp", System.currentTimeMillis());

        db.collection("conversations").document(conversation)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    loadMessage();
                })
                .addOnFailureListener(e -> {

                });

    }
    private void loadMessage() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("conversations")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(conversationSnapshots -> {
                    for (DocumentSnapshot conversationDoc : conversationSnapshots) {
                        String conversationId = conversationDoc.getId(); // Lấy ID của conversation
                        Log.e("FirebaseChat", "conversationId: " + conversationId);
                        conversation = conversationDoc.getId();
                        db.collection("conversations").document(conversation)
                                .collection("messages")
                                .orderBy("timestamp", Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(messageSnapshots -> {

                                    List<Message> messages = new ArrayList<>();
                                    for (DocumentSnapshot messageDoc : messageSnapshots) {
                                        Message message2 = messageDoc.toObject(Message.class);
                                        messages.add(message2);
                                        messageAdapter.setData(messages);
                                    }

                                    messageAdapter.notifyDataSetChanged();
                                    if (!messages.isEmpty()) {
                                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firebase", "Lỗi lấy tin nhắn: ", e));
                    }

                })
                .addOnFailureListener(e -> Log.e("Firebase", "Lỗi lấy danh sách conversations: ", e));


    }


}
