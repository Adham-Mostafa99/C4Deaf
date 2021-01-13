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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT =1;


    private ArrayList<Chat> chats;
    private Context context;
    private String imageurl;
    //MessageAdapter.OnItemClickListener onItemClickListener;

    public MessageAdapter(ArrayList<Chat> chats, Context context, String imageurl) {
        this.chats = chats;
        this.context = context;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView showMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            showMessage = itemView.findViewById(R.id.show_message);

            //this.onItemClickListener = onItemClickListener;

        }
    }

}

