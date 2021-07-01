package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.models.UserPublicInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {
    Context context;
    ArrayList<UserPublicInfo> friendsArray;
    OnItemClick onItemClick;
    AddFriend onAddFriend;
    CancelFriend cancelFriend;
    FriendOrNot friendOrNot;

    public AddFriendAdapter(Context context, ArrayList<UserPublicInfo> friendsArray, OnItemClick onItemClick, AddFriend onAcceptFriend, CancelFriend cancelFriend, FriendOrNot friendOrNot) {
        this.context = context;
        this.friendsArray = friendsArray;
        this.onItemClick = onItemClick;
        this.onAddFriend = onAcceptFriend;
        this.cancelFriend = cancelFriend;
        this.friendOrNot = friendOrNot;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_friend_item, parent, false);
        return new ViewHolder(view, onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        friendOrNot.friendOrNot(position, holder.addFriend);

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddFriend.onAddFriend(position);
                holder.addFriend.setVisibility(View.INVISIBLE);
                holder.cancelFriend.setVisibility(View.VISIBLE);
            }
        });

        holder.cancelFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFriend.onCancelFriend(position);
                holder.addFriend.setVisibility(View.VISIBLE);
                holder.cancelFriend.setVisibility(View.INVISIBLE);
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
        private Button addFriend;
        private Button cancelFriend;

        private OnItemClick onItemClick;

        public ViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            friendPhoto = itemView.findViewById(R.id.add_friend_item_photo);
            displayName = itemView.findViewById(R.id.add_friend_display_name);
            addFriend = itemView.findViewById(R.id.add_friend);
            cancelFriend = itemView.findViewById(R.id.cancel_friend);

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

    public interface AddFriend {
        void onAddFriend(int position);
    }

    public interface CancelFriend {
        void onCancelFriend(int position);
    }

    public interface FriendOrNot {
        void friendOrNot(int position, Button add);
    }

}