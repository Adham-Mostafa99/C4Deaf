package com.example.graduationproject.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
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

import com.example.graduationproject.ConvertTextToVideo;
import com.example.graduationproject.R;
import com.example.graduationproject.models.DeafChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DeafMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_SENDER_TEXT = 0;
    public static final int MSG_TYPE_RECEIVER_TEXT = 1;
    public static final int MSG_TYPE_RECEIVER_VIDEO = 2;
    public static final int MSG_TYPE_SENDER_VIDEO = 3;

    private ArrayList<DeafChat> chats;
    private Context context;
    private FirebaseUser currentUser;
    OnVideoClick onVideoClick;
    OnWordClick onWordClick;

    public DeafMessageAdapter(ArrayList<DeafChat> deafChats, Context context, OnVideoClick onVideoClick, OnWordClick onWordClick) {
        this.chats = deafChats;
        this.context = context;
        this.onVideoClick = onVideoClick;
        this.onWordClick = onWordClick;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //do specify message which each user
        //which may be right for person or left for parentUser
        if (viewType == MSG_TYPE_SENDER_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new SenderViewHolderText(view);
        } else if (viewType == MSG_TYPE_SENDER_VIDEO) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_video_right, parent, false);
            return new SenderViewHolderVideo(view);
        } else if (viewType == MSG_TYPE_RECEIVER_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ReceiverViewHolderText(view, onWordClick);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_video_left, parent, false);
            return new ReceiverViewHolderVideo(view, onVideoClick);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // get instance from Chat class with current position
        DeafChat msg = chats.get(position);

        //message is text for sender
        if (holder.getClass() == SenderViewHolderText.class) {
            ((SenderViewHolderText) holder).senderShowMessage.setText(msg.getMessage());
            ((SenderViewHolderText) holder).senderTimeMessage.setText(msg.getTime());
        }

        //message is text for receiver
        else if (holder.getClass() == ReceiverViewHolderText.class) {
            ((ReceiverViewHolderText) holder).receiverShowMessage.setText(msg.getMessage());
            ((ReceiverViewHolderText) holder).receiverTimeMessage.setText(msg.getTime());

        }

        //message is video for sender
        else if (holder.getClass() == SenderViewHolderVideo.class) {
            //display time of the message
            ((SenderViewHolderVideo) holder).senderTimeMessageVideo.setText(msg.getTime());


            ((SenderViewHolderVideo) holder).senderVideoDuration.setText(msg.getMediaMsgTime());


        }

        //message is video for receiver
        else if (holder.getClass() == ReceiverViewHolderVideo.class) {
            //display time of the message
            ((ReceiverViewHolderVideo) holder).receiverTimeMessageVideo.setText(msg.getTime());

            ((ReceiverViewHolderVideo) holder).receiverVideoDuration.setText(msg.getMediaMsgTime());

        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getMsgType() == DeafChat.MSG_TEXT_TYPE) {
            if (chats.get(position).getSender().equals(currentUser.getUid())) {
                return MSG_TYPE_SENDER_TEXT;
            } else
                return MSG_TYPE_RECEIVER_TEXT;
        } else if (chats.get(position).getMsgType() == DeafChat.MSG_RECORD_TYPE) {
            if (chats.get(position).getSender().equals(currentUser.getUid())) {
                return MSG_TYPE_SENDER_VIDEO;
            } else
                return MSG_TYPE_RECEIVER_VIDEO;
        }
        return 1;
    }


    /*
      (4 types of messages)
      - sender text
      - receiver text
      - sender record
      - receiver record
     */
    //sender text
    static class SenderViewHolderText extends RecyclerView.ViewHolder {
        TextView senderShowMessage;
        TextView senderTimeMessage;

        public SenderViewHolderText(@NonNull View itemView) {
            super(itemView);
            senderShowMessage = itemView.findViewById(R.id.right_show_message);
            senderTimeMessage = itemView.findViewById(R.id.right_time_message);

        }
    }

    //receiver text
    static class ReceiverViewHolderText extends RecyclerView.ViewHolder {
        TextView receiverShowMessage;
        TextView receiverTimeMessage;
        ImageView receiverPlayMsg;

        public ReceiverViewHolderText(@NonNull View itemView, OnWordClick onWordClick) {
            super(itemView);
            receiverShowMessage = itemView.findViewById(R.id.left_show_message);
            receiverTimeMessage = itemView.findViewById(R.id.left_time_message);
            receiverPlayMsg = itemView.findViewById(R.id.left_play_msg);
            receiverPlayMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWordClick.onWordClick(getAdapterPosition());
                }
            });
        }
    }

    //sender record
    static class SenderViewHolderVideo extends RecyclerView.ViewHolder {
        TextView senderTimeMessageVideo;
        TextView senderVideoDuration;
        ImageView senderPlayVideoIcon;

        public SenderViewHolderVideo(@NonNull View itemView) {
            super(itemView);
            senderTimeMessageVideo = itemView.findViewById(R.id.sender_time_message_video);
            senderVideoDuration = itemView.findViewById(R.id.sender_video_duration);
            senderPlayVideoIcon = itemView.findViewById(R.id.sender_video_icon);
        }
    }

    //receiver record
    static class ReceiverViewHolderVideo extends RecyclerView.ViewHolder {
        TextView receiverTimeMessageVideo;
        TextView receiverVideoDuration;
        ImageView receiverPlayVideoIcon;

        public ReceiverViewHolderVideo(@NonNull View itemView, OnVideoClick onVideoClick) {
            super(itemView);
            receiverTimeMessageVideo = itemView.findViewById(R.id.receiver_time_message_video);
            receiverVideoDuration = itemView.findViewById(R.id.receiver_video_duration);
            receiverPlayVideoIcon = itemView.findViewById(R.id.receiver_video_icon);
            receiverPlayVideoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVideoClick.onVideoClick(getAdapterPosition());
                }
            });
        }
    }




    /*
    showing the time of every video after formatting it
     */

    /**
     * @param video        video recourse id
     * @param durationText duration of the video
     */
    // set video duration on message
    public void setRecordDuration(int video, @NonNull TextView durationText) {
        // create instance of every video to get duration
        MediaPlayer mp = MediaPlayer.create(context, video);

        // set duration of the video
        durationText.setText(reformatTime(mp.getDuration()));

        //release media from memory
        mp.release();
        mp = null;
    }

    /**
     * @param time time of record in milliSeconds
     * @return formatted time (min:sec)
     */
    //update format of duration of the video
    public String reformatTime(int time) {
        //get duration in mile seconds
        //extract minutes and seconds
        /*
         * example:
         * we have 90 000 mili seconds
         * sec = 90 000 / 1000 = 90 sec
         * min = 90/60 = 1.5
         * while min is integer then value will (1)
         * seconds after that will be :
         * 90 - 1min (1 * 60) = 30 sec
         * then :
         * min = 1
         * sec = 30
         */
        int sec = time / 1000;
        int min = sec / 60;
        sec = sec - (min * 60);

        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }


    public interface OnWordClick {
        void onWordClick(int position);
    }

    public interface OnVideoClick {
        void onVideoClick(int position);
    }




}
