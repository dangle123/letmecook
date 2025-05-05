package com.example.letmecook.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.Message;
import com.example.letmecook.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    private List<Message> messageList;
    private Context context;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;

        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals("User")) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_messe_user, parent, false);

            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
            ((UserMessageViewHolder) holder).textMessage.setGravity(Gravity.END);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setData(List<Message> newList) {
        this.messageList = newList;
        notifyDataSetChanged();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessageUser);

        }

        void bind(Message message) {
            textMessage.setText(message.getText());
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public BotMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }

        void bind(Message message) {
            textMessage.setText(message.getText());
        }
    }
}
