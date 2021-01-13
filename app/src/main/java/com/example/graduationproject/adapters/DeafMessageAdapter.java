package com.example.graduationproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;

public class DeafMessageAdapter extends RecyclerView.Adapter<DeafMessageAdapter.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT_VIDEO = 2;
    public static final int MSG_TYPE_RIGHT_VIDEO = 3;

    private ArrayList<Chat> chats;
    private Context context;
    private VideoView videoView;

    public DeafMessageAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;

    }

    @NonNull
    @Override
    public DeafMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //do specify message which each user
        //which may be right for person or left for parentUser
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view, viewType);
        } else if (viewType == MSG_TYPE_RIGHT_VIDEO) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_video_right, parent, false);
            return new ViewHolder(view, viewType);
        } else if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view, viewType);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_video_left, parent, false);
            return new ViewHolder(view, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DeafMessageAdapter.ViewHolder holder, int position) {
        Chat msg = chats.get(position);

        if (holder.showMessage != null) //may be record (no message)
            holder.showMessage.setText(msg.getMessage());

        if (holder.leftPlayVideo != null) //may be text (no record)  for left message
            holder.leftPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopVideoView();
                    playVideo();

                }
            });

        if (holder.rightPlayVideo != null) //may be text (no record)  for right message
            holder.rightPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopVideoView();
                    playVideo();
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
        ImageView leftPlayVideo;
        ImageView rightPlayVideo;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            //do specify message which each user
            //which may be right for person or left for parentUser
            if (viewType == MSG_TYPE_RIGHT) {
                showMessage = itemView.findViewById(R.id.right_show_message);
                timeMessage = itemView.findViewById(R.id.right_time_message);
            } else if (viewType == MSG_TYPE_RIGHT_VIDEO) {
                timeMessage = itemView.findViewById(R.id.right_time_message_video);
                rightPlayVideo = itemView.findViewById(R.id.right_video_icon);
            } else if (viewType == MSG_TYPE_LEFT) {
                showMessage = itemView.findViewById(R.id.left_show_message);
                timeMessage = itemView.findViewById(R.id.left_time_message);
            } else {
                timeMessage = itemView.findViewById(R.id.left_time_message_video);
                leftPlayVideo = itemView.findViewById(R.id.left_video_icon);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        //only for test view type
        if (position == 0 || position == 3 || position == 6 || position == 9 || position == 12 || position == 15)
            return MSG_TYPE_LEFT_VIDEO;
        else if (position == 1 || position == 4 || position == 7 || position == 10 || position == 13 || position == 16)
            return MSG_TYPE_RIGHT_VIDEO;
        else if (position == 2 || position == 5 || position == 8)
            return MSG_TYPE_LEFT;
        else
            return MSG_TYPE_RIGHT;
    }

    //make popup window or video
    public void openPopVideoView() {
        //make the width and height for the pop Window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // lets taps outside the popup also dismiss it
        boolean focusable = true;

        //inflate new layout with specific layout(pop_layout) for the pop window
        View popLayout = LayoutInflater.from(context).inflate(R.layout.vedio_message, null);

        //create instance of popupWindow by specific view, width, and height
        PopupWindow popupWindow = new PopupWindow(popLayout, width, height, focusable);

        //show the created instance in specific location
        popupWindow.showAtLocation(popLayout, Gravity.CENTER, 0, 0);

        //declare videoView which will appear in pop up window
        videoView = popLayout.findViewById(R.id.video_view);
    }

    public void playVideo() {
        //create the path of video
        String videoPath = "android.resource://" + context.getPackageName() + "/" + R.raw.test;

        //create uri with specific path
        Uri uri = Uri.parse(videoPath);

        //set path to the videoView
        videoView.setVideoURI(uri);

        //create MediaController for control the video
        //like play ,stop and etc...
        MediaController mediaController = new MediaController(context);

        //set this controller to the video view
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        //start video
        videoView.start();
    }

}
