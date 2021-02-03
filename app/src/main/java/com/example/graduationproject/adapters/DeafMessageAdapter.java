package com.example.graduationproject.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;

public class DeafMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_SENDER_TEXT = 0;
    public static final int MSG_TYPE_RECEIVER_TEXT = 1;
    public static final int MSG_TYPE_RECEIVER_VIDEO = 2;
    public static final int MSG_TYPE_SENDER_VIDEO = 3;

    private ArrayList<Chat> chats;
    private Context context;
    private VideoView videoView;

    public DeafMessageAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;

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
            return new ReceiverViewHolderText(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_video_left, parent, false);
            return new ReceiverViewHolderVideo(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // get instance from Chat class with current position
        Chat msg = chats.get(position);

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

            //set duration of full video
            setRecordDuration(msg.getMediaMsg(), ((SenderViewHolderVideo) holder).senderVideoDuration);


            //playing Video
            ((SenderViewHolderVideo) holder).senderPlayVideoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open window to display video
                    PopupWindow popupWindow = openPopVideoView();
                    //play video
                    playVideo(msg.getMediaMsg());

                    //after finishing the video
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            closePopVideoView(mp, popupWindow);
                        }
                    });
                }

            });

        }

        //message is video for receiver
        else if (holder.getClass() == ReceiverViewHolderVideo.class) {
            //display time of the message
            ((ReceiverViewHolderVideo) holder).receiverTimeMessageVideo.setText(msg.getTime());

            //set duration of full video
            setRecordDuration(msg.getMediaMsg(), ((ReceiverViewHolderVideo) holder).receiverVideoDuration);


            //playing Video
            ((ReceiverViewHolderVideo) holder).receiverPlayVideoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open window to display video
                    PopupWindow popupWindow = openPopVideoView();
                    //play video
                    playVideo(msg.getMediaMsg());

                    //after finishing the video
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            closePopVideoView(mp, popupWindow);
                        }
                    });
                }

            });
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        //only for test view type
        if (position == 0 || position == 3 || position == 6 || position == 9 || position == 12 || position == 15)
            return MSG_TYPE_RECEIVER_VIDEO;
        else if (position == 1 || position == 4 || position == 7 || position == 10 || position == 13 || position == 16)
            return MSG_TYPE_SENDER_VIDEO;
        else if (position == 2 || position == 5 || position == 8)
            return MSG_TYPE_SENDER_TEXT;
        else
            return MSG_TYPE_RECEIVER_TEXT;
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

        public ReceiverViewHolderText(@NonNull View itemView) {
            super(itemView);
            receiverShowMessage = itemView.findViewById(R.id.left_show_message);
            receiverTimeMessage = itemView.findViewById(R.id.left_time_message);
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

        public ReceiverViewHolderVideo(@NonNull View itemView) {
            super(itemView);
            receiverTimeMessageVideo = itemView.findViewById(R.id.receiver_time_message_video);
            receiverVideoDuration = itemView.findViewById(R.id.receiver_video_duration);
            receiverPlayVideoIcon = itemView.findViewById(R.id.receiver_video_icon);
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

        return String.format("%02d",min) + ":" + String.format("%02d",sec);
    }


    /*
    controlling the pop window which will display video
     */
    //make popup window for video
    public PopupWindow openPopVideoView() {
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

        return popupWindow;
    }

    /**
     * @param mp          MediaPlayer which will release it from memory
     * @param popupWindow window which will close
     */
    //close the popWindow
    public void closePopVideoView(MediaPlayer mp, PopupWindow popupWindow) {
        mp.release();
        popupWindow.dismiss();
    }

    /**
     * @param videoResource video recourse of any message
     */
    //playing specific video by it's id
    public void playVideo(int videoResource) {
        //create the path of video
        String videoPath = "android.resource://" + context.getPackageName() + "/" + videoResource;

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
