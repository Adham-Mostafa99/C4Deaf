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
import com.example.graduationproject.models.User;

import java.util.ArrayList;

import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {


    ArrayList<User> users;
    Context context;
    OnItemClickListener onItemClickListener;

    public ChatListAdapter(Context context, ArrayList<User> users,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.users = users;
        this.onItemClickListener=onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getUserName());
        holder.userMessage.setText(user.getUserMessage());
        holder.messageTime.setText(user.getMessageTime());
        String imageUrl = user.getUserImageUrl();
        Glide
                .with(context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(holder.userPhoto);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userPhoto;
        TextView userName;
        TextView userMessage;
        TextView messageTime;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView ,OnItemClickListener onItemClickListener) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_photo);
            userName = itemView.findViewById(R.id.user_name);
            userMessage = itemView.findViewById(R.id.user_message);
            messageTime = itemView.findViewById(R.id.message_time);
            this.onItemClickListener=onItemClickListener;

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


