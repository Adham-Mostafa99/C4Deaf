package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;

public class NormalMessageAdapter extends RecyclerView.Adapter<NormalMessageAdapter.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT_RECORD = 2;
    public static final int MSG_TYPE_RIGHT_RECORD = 3;

    private ArrayList<Chat> chats;
    private Context context;

    public NormalMessageAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public NormalMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        if (holder.showMessage != null) //may be record (no message)
            holder.showMessage.setText(msg.getMessage());

        if (holder.leftPlayRecord != null) //may be text (no record)  for left message
            holder.leftPlayRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.leftPlayRecord.getImageAlpha() == R.drawable.left_play_record_icon) {
                        Toast.makeText(context, "left record play  " + position, Toast.LENGTH_SHORT).show();
                        holder.leftPlayRecord.setImageResource(R.drawable.left_pause_record_icon);
                    }
                    else {
                        Toast.makeText(context, "left record pause  " + position, Toast.LENGTH_SHORT).show();
                        holder.leftPlayRecord.setImageResource(R.drawable.left_play_record_icon);
                    }

                }
            });

        if (holder.rightPlayRecord != null) //may be text (no record)  for right message
            holder.rightPlayRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "right record play  " + position, Toast.LENGTH_SHORT).show();
                    holder.rightPlayRecord.setImageResource(R.drawable.right_pause_record_icon);
                }
            });

        //time will for all
        holder.timeMessage.setText(msg.getTime());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;
        TextView timeMessage;
        ImageView leftPlayRecord;
        ImageView rightPlayRecord;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            //do specify message which each user
            //which may be right for person or left for parentUser
            if (viewType == MSG_TYPE_RIGHT) {
                showMessage = itemView.findViewById(R.id.right_show_message);
                timeMessage = itemView.findViewById(R.id.right_time_message);
            } else if (viewType == MSG_TYPE_RIGHT_RECORD) {
                timeMessage = itemView.findViewById(R.id.right_time_message_record);
                rightPlayRecord = itemView.findViewById(R.id.right_play_record);
            } else if (viewType == MSG_TYPE_LEFT) {
                showMessage = itemView.findViewById(R.id.left_show_message);
                timeMessage = itemView.findViewById(R.id.left_time_message);
            } else {
                timeMessage = itemView.findViewById(R.id.left_time_message_record);
                leftPlayRecord = itemView.findViewById(R.id.left_play_record);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        //only for test view type
        if (position == 0 || position == 3 || position == 6 || position == 9 || position == 12 || position == 15)
            return MSG_TYPE_LEFT_RECORD;
        else if (position == 1 || position == 4 || position == 7 || position == 10 || position == 13 || position == 16)
            return MSG_TYPE_RIGHT_RECORD;
        else if (position == 2 || position == 5 || position == 8)
            return MSG_TYPE_LEFT;
        else
            return MSG_TYPE_RIGHT;
    }

}

