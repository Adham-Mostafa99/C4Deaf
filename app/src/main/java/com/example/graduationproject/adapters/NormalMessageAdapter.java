package com.example.graduationproject.adapters;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;

public class NormalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_RECEIVER_TEXT = 0;
    public static final int MSG_TYPE_SENDER_TEXT = 1;
    public static final int MSG_TYPE_RECEIVER_RECORD = 2;
    public static final int MSG_TYPE_SENDER_RECORD = 3;


    private ArrayList<Chat> chats;
    private Context context;
    private MediaPlayer mp = null;
    private AudioManager audioManager;
    private int audioRequest;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;

    private int lastPosition = -1;


    public NormalMessageAdapter(ArrayList<Chat> chats, Context context) {
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
        } else if (viewType == MSG_TYPE_SENDER_RECORD) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_record_right, parent, false);
            return new SenderViewHolderRecord(view);
        } else if (viewType == MSG_TYPE_RECEIVER_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ReceiverViewHolderText(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_record_left, parent, false);
            return new ReceiverViewHolderRecord(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat msg = chats.get(position);

        // get the audio system service for
        // the audioManger instance
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

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

        //message is record for sender
        else if (holder.getClass() == SenderViewHolderRecord.class) {
            // media player is handled according to the
            // change in the focus which Android system grants for
            onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            resumeRecord(((SenderViewHolderRecord) holder).senderPlayRecord);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            stopRecord(((SenderViewHolderRecord) holder).senderPlayRecord);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            pauseRecord(((SenderViewHolderRecord) holder).senderPlayRecord);
                            break;
                    }
                }
            };

            // Request audio focus for playback
            audioRequest = audioManager.requestAudioFocus(
                    onAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            ((SenderViewHolderRecord) holder).senderTimeMessageRecord.setText(msg.getTime());

            //playing record
            ((SenderViewHolderRecord) holder).senderPlayRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /**
                     * when check play icon of record
                     * if the record is not playing then play it
                     * else if it playing then pause it
                     * else if click in new record then stop the old and play new
                     */

                    // check position to know which record is click
                    // if last equal current then user click same record
                    // then should pause record or resume not stopping
                    if (lastPosition == position) {
                        // if record is playing pause it
                        if (mp.isPlaying()) {
                            pauseRecord(((SenderViewHolderRecord) holder).senderPlayRecord);
                        }
                        // if it paused then resume it
                        else {
                            resumeRecord(((SenderViewHolderRecord) holder).senderPlayRecord);
                        }
                    }
                    // if last different from current
                    // then user click new record
                    // stop old and play new one
                    else {
                        playRecord(msg.getRecordMsg(), ((SenderViewHolderRecord) holder).senderPlayRecord, position);
                    }

                    //when the record finished
                    //should stop media and release it
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopRecord(((SenderViewHolderRecord) holder).senderPlayRecord);
                        }
                    });
                }

            });

        }

        //message is record for receiver
        else if (holder.getClass() == ReceiverViewHolderRecord.class) {
            // media player is handled according to the
            // change in the focus which Android system grants for
            onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            resumeRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecord);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            stopRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecord);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            pauseRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecord);
                            break;
                    }
                }
            };

            // Request audio focus for playback
            audioRequest = audioManager.requestAudioFocus(
                    onAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);


            ((ReceiverViewHolderRecord) holder).receiverTimeMessageRecord.setText(msg.getTime());
            ((ReceiverViewHolderRecord) holder).receiverPlayRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /**
                     * when check play icon of record
                     * if the record is not playing then play it
                     * else if it playing then pause it
                     * else if click in new record then stop the old and play new
                     */

                    // check position to know which record is click
                    // if last equal current then user click same record
                    // then should pause record or resume not stopping
                    if (lastPosition == position) {
                        // if record is playing pause it
                        if (mp.isPlaying()) {
                            pauseRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecord);
                        }
                        // if it paused then resume it
                        else {
                            resumeRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecord);
                        }
                    }
                    // if last different from current
                    // then user click new record
                    // stop old and play new one
                    else {
                        playRecord(msg.getRecordMsg(), ((ReceiverViewHolderRecord) holder).receiverPlayRecord, position);
                    }

                    //when the record finished
                    //should stop media and release it
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecord);
                        }
                    });
                }
            });
        }

    }

    // stop record and release the media
    public void stopRecord(ImageView recordIcon) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        lastPosition = -1;
        //loss or release audio focus
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        recordIcon.setImageResource(R.drawable.right_play_record_icon);
    }

    // pause the record
    public void pauseRecord(ImageView recordIcon) {
        mp.pause();
        recordIcon.setImageResource(R.drawable.right_play_record_icon);
    }

    // play record which new record will play
    // release any media and play new one
    public void playRecord(int recordRes, ImageView recordIcon, int currentPosition) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }

        // check the audio request
        if (audioRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mp = MediaPlayer.create(context, recordRes); // create instance from media with specific resource
            mp.start();
            recordIcon.setImageResource(R.drawable.right_pause_record_icon);
            lastPosition = currentPosition;
        }
    }

    // resume current record which is paused
    public void resumeRecord(ImageView recordIcon) {
        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
                recordIcon.setImageResource(R.drawable.right_pause_record_icon);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     * (4 types of messages)
     * - sender text
     * - receiver text
     * - sender record
     * - receiver record
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
    static class SenderViewHolderRecord extends RecyclerView.ViewHolder {
        TextView senderTimeMessageRecord;
        ImageView senderPlayRecord;

        public SenderViewHolderRecord(@NonNull View itemView) {
            super(itemView);
            senderTimeMessageRecord = itemView.findViewById(R.id.right_time_message_record);
            senderPlayRecord = itemView.findViewById(R.id.right_play_record);
        }
    }

    //receiver record
    static class ReceiverViewHolderRecord extends RecyclerView.ViewHolder {
        TextView receiverTimeMessageRecord;
        ImageView receiverPlayRecord;

        public ReceiverViewHolderRecord(@NonNull View itemView) {
            super(itemView);
            receiverTimeMessageRecord = itemView.findViewById(R.id.left_time_message_record);
            receiverPlayRecord = itemView.findViewById(R.id.left_play_record);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //only for test view type
        if (position == 0 || position == 3 || position == 6 || position == 9 || position == 12 || position == 15)
            return MSG_TYPE_RECEIVER_RECORD;
        else if (position == 1 || position == 4 || position == 7 || position == 10 || position == 13 || position == 16)
            return MSG_TYPE_SENDER_RECORD;
        else if (position == 2 || position == 5 || position == 8)
            return MSG_TYPE_RECEIVER_TEXT;
        else
            return MSG_TYPE_SENDER_TEXT;
    }

}

