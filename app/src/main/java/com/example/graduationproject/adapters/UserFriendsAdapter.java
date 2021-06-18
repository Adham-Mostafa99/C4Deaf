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

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.ViewHolder> {
    Context context;
    ArrayList<UserPublicInfo> friendsArray;
    OnItemClick onItemClick;

    public UserFriendsAdapter(Context context, ArrayList<UserPublicInfo> friendsArray, OnItemClick onItemClick) {
        this.context = context;
        this.friendsArray = friendsArray;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public UserFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_item, parent, false);
        return new UserFriendsAdapter.ViewHolder(view, onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFriendsAdapter.ViewHolder holder, int position) {
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

    }

    @Override
    public int getItemCount() {
        return friendsArray.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView friendPhoto;
        private TextView displayName;
        private OnItemClick onItemClick;

        public ViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            friendPhoto = itemView.findViewById(R.id.friend_item_photo);
            displayName = itemView.findViewById(R.id.friend_item_display_name);
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
}
