package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.models.Chat;
import com.example.graduationproject.models.User;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


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
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat msg = chats.get(position);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            timeMessage = itemView.findViewById(R.id.time_message);
            //this.onItemClickListener = onItemClickListener;

        }
    }

    @Override
    public int getItemViewType(int position) {
        //only for test view type
        if (position % 2 == 0)
            return MSG_TYPE_LEFT;
        else
            return MSG_TYPE_RIGHT;
    }
}

