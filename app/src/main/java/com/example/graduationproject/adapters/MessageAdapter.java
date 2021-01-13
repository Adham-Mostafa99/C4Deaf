package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;
import java.util.Random;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT_RECORD = 2;
    public static final int MSG_TYPE_RIGHT_RECORD = 3;


    private ArrayList<Chat> chats;
    private Context context;
    //MessageAdapter.OnItemClickListener onItemClickListener;

    public MessageAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //do specify message which each user
        //which may be right for person or left for parentUser
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view, viewType);
        } else if (viewType == MSG_TYPE_RIGHT_RECORD) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_record_right, parent, false);
            return new ViewHolder(view, viewType);
        } else if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view, viewType);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_record_left, parent, false);
            return new ViewHolder(view, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat msg = chats.get(position);
        if (holder.showMessage != null)
            holder.showMessage.setText(msg.getMessage());
        holder.timeMessage.setText(msg.getTime());

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;
        TextView timeMessage;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == MSG_TYPE_RIGHT) {
                showMessage = itemView.findViewById(R.id.right_show_message);
                timeMessage = itemView.findViewById(R.id.right_time_message);
            } else if (viewType == MSG_TYPE_RIGHT_RECORD) {
                timeMessage = itemView.findViewById(R.id.right_time_message_record);
            } else if (viewType == MSG_TYPE_LEFT) {
                showMessage = itemView.findViewById(R.id.left_show_message);
                timeMessage = itemView.findViewById(R.id.left_time_message);
            } else {
                timeMessage = itemView.findViewById(R.id.left_time_message_record);
            }

            
            //this.onItemClickListener = onItemClickListener;

        }
    }

    @Override
    public int getItemViewType(int position) {
        //only for test view type
        Random random = new Random();
        int test = random.nextInt(4);

        switch (test) {
            case 0:
                return MSG_TYPE_LEFT;
            case 1:
                return MSG_TYPE_RIGHT;
            case 2:
                return MSG_TYPE_LEFT_RECORD;
            default:
                return MSG_TYPE_RIGHT_RECORD;
        }
    }
}

