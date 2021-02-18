package com.example.graduationproject.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.NormalChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;


public class NormalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //specify type of view
    public static final int MSG_TYPE_RECEIVER_TEXT = 0;
    public static final int MSG_TYPE_SENDER_TEXT = 1;
    public static final int MSG_TYPE_RECEIVER_RECORD = 2;
    public static final int MSG_TYPE_SENDER_RECORD = 3;
    public static final int NULL_VALUE = -1;


    private ArrayList<NormalChat> normalChats;
    private Context context;
    private MediaPlayer mp = null;
    private AudioManager audioManager;
    private int audioRequest;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private OldRecord oldRecord;
    private Runnable runnable;
    private Handler handler;
    private FirebaseUser currentUser;


    public NormalMessageAdapter(ArrayList<NormalChat> normalChats, Context context) {
        this.normalChats = normalChats;
        this.context = context;
        init();
    }

    public void init() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //initialize the Handler
        handler = new Handler();
        //initialize the old Record
        oldRecord = new OldRecord(NULL_VALUE);
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
        // get instance from Chat class with current position
        NormalChat msg = normalChats.get(position);

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
            ((SenderViewHolderRecord) holder).senderTimeMessageRecord.setText(msg.getTime());

//            //set duration of full record
//            setRecordDuration(msg.getMediaMsgPath(), ((SenderViewHolderRecord) holder).senderRecordDuration);
            ((SenderViewHolderRecord) holder).senderRecordDuration.setText(msg.getMediaMsgTime());

            //change the record position by seekBar
            ((SenderViewHolderRecord) holder).senderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (oldRecord.getOldPosition() == position) {
                        if (fromUser) {
                            seekBar.setMax(mp.getDuration());
                            mp.seekTo(progress);
                            seekBar.setProgress(progress);
                        }
                    } else {
                        seekBar.setProgress(0);
                    }
                }


                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            // media player is handled according to the
            // change in the focus which Android system grants for
            handlingFocusRecord(((SenderViewHolderRecord) holder).senderPlayRecordIcon,
                    ((SenderViewHolderRecord) holder).senderSeekBar,
                    MSG_TYPE_SENDER_RECORD);


            //playing record
            ((SenderViewHolderRecord) holder).senderPlayRecordIcon.setOnClickListener(new View.OnClickListener() {
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

                    Log.v("check", oldRecord.getOldPosition() + "    " + position);
                    if (oldRecord.getOldPosition() == position) {
                        // if record is playing pause it
                        if (mp.isPlaying()) {
                            pauseRecord(((SenderViewHolderRecord) holder).senderPlayRecordIcon,
                                    MSG_TYPE_SENDER_RECORD);
                        }
                        // if it paused then resume it
                        else {
                            resumeRecord(((SenderViewHolderRecord) holder).senderPlayRecordIcon,
                                    MSG_TYPE_SENDER_RECORD);
                        }
                    }

                    // if last different from current
                    // then user click new record
                    // stop old and play new one
                    else {
                        playRecord(msg.getMediaMsgPath(), ((SenderViewHolderRecord) holder).senderPlayRecordIcon,
                                ((SenderViewHolderRecord) holder).senderSeekBar,
                                ((SenderViewHolderRecord) holder).senderRecordDuration,
                                position,
                                MSG_TYPE_SENDER_RECORD);

                    }

                    //when the record finished
                    //should stop media and release it
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopRecord(((SenderViewHolderRecord) holder).senderPlayRecordIcon,
                                    ((SenderViewHolderRecord) holder).senderSeekBar,
                                    MSG_TYPE_SENDER_RECORD);
                        }
                    });
                }

            });

        }

        //message is record for receiver
        else if (holder.getClass() == ReceiverViewHolderRecord.class) {
            ((ReceiverViewHolderRecord) holder).receiverTimeMessageRecord.setText(msg.getTime());

//            //set duration of full record
//            setRecordDuration(msg.getMediaMsgTime(), ((ReceiverViewHolderRecord) holder).receiverRecordDuration);

            ((ReceiverViewHolderRecord) holder).receiverRecordDuration.setText(msg.getMediaMsgTime());

            //change the record position by seekBar
            ((ReceiverViewHolderRecord) holder).receiverSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (oldRecord.getOldPosition() == position) {
                        if (fromUser) {
                            seekBar.setMax(mp.getDuration());
                            mp.seekTo(progress);
                            seekBar.setProgress(progress);
                        }
                    } else {
                        seekBar.setProgress(0);
                    }
                }


                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            // media player is handled according to the
            // change in the focus which Android system grants for
            handlingFocusRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecordIcon,
                    ((ReceiverViewHolderRecord) holder).receiverSeekBar,
                    MSG_TYPE_RECEIVER_RECORD);


            //playing record
            ((ReceiverViewHolderRecord) holder).receiverPlayRecordIcon.setOnClickListener(new View.OnClickListener() {
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
                    if (oldRecord.getOldPosition() == position) {
                        // if record is playing pause it
                        if (mp.isPlaying()) {
                            pauseRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecordIcon,
                                    MSG_TYPE_RECEIVER_RECORD);
                        }
                        // if it paused then resume it
                        else {
                            resumeRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecordIcon,
                                    MSG_TYPE_RECEIVER_RECORD);
                        }
                    }
                    // if last different from current
                    // then user click new record
                    // stop old and play new one
                    else {
                        playRecord(msg.getMediaMsgPath(),
                                ((ReceiverViewHolderRecord) holder).receiverPlayRecordIcon,
                                ((ReceiverViewHolderRecord) holder).receiverSeekBar,
                                ((ReceiverViewHolderRecord) holder).receiverRecordDuration,
                                position, MSG_TYPE_RECEIVER_RECORD);
                    }

                    //when the record finished
                    //should stop media and release it
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopRecord(((ReceiverViewHolderRecord) holder).receiverPlayRecordIcon,
                                    ((ReceiverViewHolderRecord) holder).receiverSeekBar,
                                    MSG_TYPE_RECEIVER_RECORD);
                        }
                    });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return normalChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (normalChats.get(position).getMsgType() == NormalChat.MSG_TEXT_TYPE) {

            if (normalChats.get(position).getSender().equals(currentUser.getUid())) {
                return MSG_TYPE_SENDER_TEXT;
            } else
                return MSG_TYPE_RECEIVER_TEXT;
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

        public ReceiverViewHolderText(@NonNull View itemView) {
            super(itemView);
            receiverShowMessage = itemView.findViewById(R.id.left_show_message);
            receiverTimeMessage = itemView.findViewById(R.id.left_time_message);
        }
    }

    //sender record
    static class SenderViewHolderRecord extends RecyclerView.ViewHolder {
        TextView senderTimeMessageRecord;
        TextView senderRecordDuration;
        ImageView senderPlayRecordIcon;
        SeekBar senderSeekBar;

        public SenderViewHolderRecord(@NonNull View itemView) {
            super(itemView);
            senderTimeMessageRecord = itemView.findViewById(R.id.right_time_message_record);
            senderRecordDuration = itemView.findViewById(R.id.sender_record_duration);
            senderPlayRecordIcon = itemView.findViewById(R.id.right_play_record);
            senderSeekBar = itemView.findViewById(R.id.right_seek_bar_record);
        }
    }

    //receiver record
    static class ReceiverViewHolderRecord extends RecyclerView.ViewHolder {
        TextView receiverTimeMessageRecord;
        TextView receiverRecordDuration;
        ImageView receiverPlayRecordIcon;
        SeekBar receiverSeekBar;

        public ReceiverViewHolderRecord(@NonNull View itemView) {
            super(itemView);
            receiverTimeMessageRecord = itemView.findViewById(R.id.left_time_message_record);
            receiverRecordDuration = itemView.findViewById(R.id.receiver_record_duration);
            receiverPlayRecordIcon = itemView.findViewById(R.id.left_play_record);
            receiverSeekBar = itemView.findViewById(R.id.left_seek_bar_record);
        }
    }



    /*
    controlling Media [play-stop-pause-resume] and focusing
     */

    /**
     * @param recordRes             record recourse id
     * @param recordIcon            image of state the record [play-pause]
     * @param seekBar               seekBar which display record
     * @param timeText              displaying duration of record
     * @param currentRecordPosition position of the record in recyclerView
     * @param type                  type of the message which will be one of 4-types
     */
    // play record which new record will play
    // release any media and play new one
    public void playRecord(String recordRes, ImageView recordIcon, SeekBar seekBar, TextView timeText, int currentRecordPosition, int type) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            //remove handler for old record
            handler.removeCallbacks(runnable);
        }


        // check the audio request
        if (audioRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // create instance from media with specific resource
            mp = new MediaPlayer();
            try {
                mp.setDataSource(recordRes);
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            syncMediaWithSeekBar(seekBar, timeText);
            playRecordIcon(recordIcon, type);

            //check if no old record then create one from current record
            //else if there one release it from memory
            if (oldRecord.isEmpty)
                oldRecord = new OldRecord(recordIcon, seekBar, timeText, reformatTime(mp.getDuration()), currentRecordPosition, type);
            else
                releaseOldRecord(recordIcon, seekBar, timeText, reformatTime(mp.getDuration()), currentRecordPosition, type);
        }
    }

    /**
     * @param recordIcon image of state the record [play-pause]
     * @param seekBar    seekBar which display record
     * @param type       type of the message which will be one of 4-types
     */
    // stop record and release the media
    public void stopRecord(ImageView recordIcon, SeekBar seekBar, int type) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            //return seek to start
            seekBar.setProgress(0);
            //remove handler
            handler.removeCallbacks(runnable);
            //loss or release audio focus
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
        //return the value of position of old record to null value
        //which mean there no record in past
        oldRecord = new OldRecord(NULL_VALUE);
        //return the icon of record to default
        pauseRecordIcon(recordIcon, type);
    }

    /**
     * @param recordIcon image of state the record [play-pause]
     * @param type       type of the message which will be one of 4-types
     */
    // pause the record
    public void pauseRecord(ImageView recordIcon, int type) {
        if (mp != null)
            mp.pause();
        pauseRecordIcon(recordIcon, type);
    }

    /**
     * @param recordIcon image of state the record [play-pause]
     * @param type       type of the message which will be one of 4-types
     */
    // resume current record which is paused
    public void resumeRecord(ImageView recordIcon, int type) {
        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
                playRecordIcon(recordIcon, type);
            }
        }
    }

    /**
     * @param imageView image of state the record [play-pause]
     * @param seekBar   seekBar which display record
     * @param type      type of the message which will be one of 4-types
     */
    //handling focusing of record
    public void handlingFocusRecord(ImageView imageView, SeekBar seekBar, int type) {
        // media player is handled according to the
        // change in the focus which Android system grants for
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        resumeRecord(imageView, type);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        stopRecord(imageView, seekBar, type);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        pauseRecord(imageView, type);
                        break;
                }
            }
        };

        // Request audio focus for playback
        audioRequest = audioManager.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }


    /*
    controlling of record which have timeDuration ,formatting time and record icon and releasing the old record
     */
    //old record which will help to remove it and create current record
    static class OldRecord {
        private ImageView oldImage;
        private SeekBar oldSeekBar;
        private TextView oldTextView;
        private String oldDuration;
        private int oldPosition;
        private int oldType;
        private boolean isEmpty;

        public OldRecord(ImageView oldImage, SeekBar oldSeekBar, TextView oldTextView, String oldDuration, int oldPosition, int oldType) {
            this.oldImage = oldImage;
            this.oldSeekBar = oldSeekBar;
            this.oldTextView = oldTextView;
            this.oldDuration = oldDuration;
            this.oldPosition = oldPosition;
            this.oldType = oldType;
            this.isEmpty = false;
        }

        public OldRecord(int oldPosition) {
            this.oldPosition = oldPosition;
            this.isEmpty = true;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setOldType(int oldType) {
            this.oldType = oldType;
        }

        public void setOldImage(ImageView oldImage) {
            this.oldImage = oldImage;
        }

        public void setOldSeekBar(SeekBar oldSeekBar) {
            this.oldSeekBar = oldSeekBar;
        }

        public void setOldTextView(TextView oldTextView) {
            this.oldTextView = oldTextView;
        }

        public void setOldDuration(String oldDuration) {
            this.oldDuration = oldDuration;
        }

        public void setOldPosition(int oldPosition) {
            this.oldPosition = oldPosition;
        }

        public ImageView getOldImage() {
            return oldImage;
        }

        public SeekBar getOldSeekBar() {
            return oldSeekBar;
        }

        public TextView getOldTextView() {
            return oldTextView;
        }

        public String getOldDuration() {
            return oldDuration;
        }

        public int getOldPosition() {
            return oldPosition;
        }

        public int getOldType() {
            return oldType;
        }
    }

    /**
     * @param record       record recourse id
     * @param durationText duration of the record
     */
    // set record duration on message
    public void setRecordDuration(int record, @NonNull TextView durationText) {
        // create instance of every record to get duration
        mp = MediaPlayer.create(context, record);

        // set duration of the record
        durationText.setText(reformatTime(mp.getDuration()));

        //release media from memory
        mp.release();
        mp = null;
    }

    /**
     * @param time time of record in milliSeconds
     * @return formatted time (min:sec)
     */
    //update format of duration of the record
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

    /**
     * @param newImage    new imageView of the record in the current position to replace old one
     * @param newSeekBar  new seekBar of the record in the current position to replace old one
     * @param newTextView new textView holding the duration of the record in the current position to replace old one
     * @param newDuration new duration of the record in the current position to replace old one
     * @param newPosition position of new record to replace old one
     * @param type        type of new record to replace old one
     */
    //clean old record (image, seek, duration)
    // check if user click new button record
    // then change the icon of old one
    //shift seek bar to start for old record
    public void releaseOldRecord(ImageView newImage, SeekBar newSeekBar, TextView newTextView, String newDuration, int newPosition, int type) {
        pauseRecordIcon(oldRecord.getOldImage(), oldRecord.getOldType());
        oldRecord.getOldSeekBar().setProgress(0);
        oldRecord.getOldTextView().setText(oldRecord.getOldDuration());
        oldRecord = new OldRecord(newImage, newSeekBar, newTextView, newDuration, newPosition, type);
    }

    /**
     * @param imageView image of record which will be playIcon
     * @param type      type of the message which will be one of 4-types
     */
    //record will play then change the icon of record
    public void playRecordIcon(ImageView imageView, int type) {
        if (type == MSG_TYPE_SENDER_RECORD)
            imageView.setImageResource(R.drawable.right_pause_record_icon);
        else
            imageView.setImageResource(R.drawable.left_pause_record_icon);
    }

    /**
     * @param imageView image of record which will be stopIcon
     * @param type      type of the message which will be one of 4-types
     */
    //record will stop or pause then return icon to default
    public void pauseRecordIcon(ImageView imageView, int type) {
        if (type == MSG_TYPE_SENDER_RECORD)
            imageView.setImageResource(R.drawable.right_play_record_icon);
        else
            imageView.setImageResource(R.drawable.left_play_record_icon);
    }


    /*
    control seekBar with media play together
     */

    /**
     * @param seekBar     seekBar which will controlled by media
     * @param currentTime time will appear ni record for every 100milliSeconds
     */
    //control seekBar with record
    public void syncMediaWithSeekBar(@NonNull SeekBar seekBar, TextView currentTime) {
        seekBar.setMax(mp.getDuration());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    seekBar.setProgress(mp.getCurrentPosition());
                    currentTime.setText(reformatTime(mp.getCurrentPosition()));
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(runnable, 100);
    }


    /*
    handling adapter states
     */
    //when close app not permanently
    public void stopAdapter() {
        if (mp != null) {
            if (mp.isPlaying())
                mp.pause();
            //change the icon of record to default
            if (oldRecord.getOldType() == MSG_TYPE_SENDER_RECORD)
                oldRecord.getOldImage().setImageResource(R.drawable.right_play_record_icon);
            else
                oldRecord.getOldImage().setImageResource(R.drawable.left_play_record_icon);
        }
    }

}

