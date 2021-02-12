package com.example.graduationproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.models.UserMenuChat;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {


    ArrayList<UserMenuChat> friends;
    Context context;
    OnItemClickListener onItemClickListener;

    public ChatListAdapter(Context context, ArrayList<UserMenuChat> friends, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.friends = friends;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserMenuChat userMenuChat = this.friends.get(position);
        holder.userName.setText(userMenuChat.getUserName());
        holder.userMessage.setText(userMenuChat.getUserMessage());
        holder.messageTime.setText(userMenuChat.getMessageTime());
        String imageUrl = userMenuChat.getUserPhotoUrl();

        Glide
                .with(context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(holder.userPhoto);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userPhoto;
        TextView userName;
        TextView userMessage;
        TextView messageTime;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_photo);
            userName = itemView.findViewById(R.id.user_name);
            userMessage = itemView.findViewById(R.id.user_message);
            messageTime = itemView.findViewById(R.id.message_time);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClickItem(getAdapterPosition());
                }
            });
        }
    }

    //make my onClick for the recyclerView
    public interface OnItemClickListener {
        void onClickItem(int position);
    }

}


