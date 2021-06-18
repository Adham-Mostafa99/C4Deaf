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
import com.example.graduationproject.models.UserPublicInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestsAdapter extends RecyclerView.Adapter<FriendsRequestsAdapter.ViewHolder> {
    Context context;
    ArrayList<UserPublicInfo> friendsArray;
    OnItemClick onItemClick;
    OnAcceptFriend onAcceptFriend;
    OnIgnoreFriend onIgnoreFriend;

    public FriendsRequestsAdapter(Context context, ArrayList<UserPublicInfo> friendsArray, OnItemClick onItemClick, OnAcceptFriend onAcceptFriend, OnIgnoreFriend onIgnoreFriend) {
        this.context = context;
        this.friendsArray = friendsArray;
        this.onItemClick = onItemClick;
        this.onAcceptFriend = onAcceptFriend;
        this.onIgnoreFriend = onIgnoreFriend;
    }

    @NonNull
    @Override
    public FriendsRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_requests_item, parent, false);
        return new FriendsRequestsAdapter.ViewHolder(view, onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRequestsAdapter.ViewHolder holder, int position) {
        //get instance of current friend
        UserPublicInfo currentFriend = friendsArray.get(position);

        //set friend photo
        Glide
                .with(context)
                .load(currentFriend.getUserPhotoPath())
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(holder.friendPhoto);

        //set friend display name
        holder.displayName.setText(currentFriend.getUserDisplayName());

        holder.acceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptFriend.onAcceptFriend(position);
            }
        });

        holder.ignoreFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIgnoreFriend.onIgnoreFriend(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsArray.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView friendPhoto;
        private TextView displayName;
        private CircleImageView acceptFriend;
        private CircleImageView ignoreFriend;

        private OnItemClick onItemClick;

        public ViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            friendPhoto = itemView.findViewById(R.id.friend_request_item_photo);
            displayName = itemView.findViewById(R.id.friend_request_item_display_name);
            acceptFriend = itemView.findViewById(R.id.accept_friend_request);
            ignoreFriend = itemView.findViewById(R.id.ignore_friend_request);

            this.onItemClick = onItemClick;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClick {
        public void onItemClick(int position);
    }

    public interface OnAcceptFriend {
        void onAcceptFriend(int position);
    }

    public interface OnIgnoreFriend {
        void onIgnoreFriend(int position);
    }
}
