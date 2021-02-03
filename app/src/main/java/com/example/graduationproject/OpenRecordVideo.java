package com.example.graduationproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.graduationproject.ui.ChatPageDeaf;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OpenRecordVideo extends AppCompatActivity implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener {
    private static final int PORTRAIT_CAMERA_ORIENTATION = 90;
    private static final int PORTRAIT_RECORD_BACK_ORIENTATION = 90;
    private static final int PORTRAIT_RECORD_FRONT_ORIENTATION = 270;
    private static final int VIDEO_MAX_DURATION = 50000; //50 sec
    private static final int VIDEO_MAX_FILE_SIZE = 5000000;//Approximately 5 megabytes
    private static final int NO_FRONT_CAMERA = -1;

    private static final int REQUEST_RECORD_VIDEO_PERMISSION = 100;//200 for microphone
    @BindView(R.id.surface_view)
    SurfaceView surfaceView;
    @BindView(R.id.btn_stop_video)
    ImageView stopVideoIcon;
    @BindView(R.id.btn_start_video)
    ImageView startVideoIcon;
    @BindView(R.id.btn_change_camera)
    ImageView changeCameraIcon;
    private boolean permissionToRecordAccepted = false;

    private int frontCameraNumber;
    private int backCameraNumber;
    private int currentCameraType;

    private String recordOutPutPath;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

    private MediaRecorder recorder;
    private boolean isRecording = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_recorder_layout);
        ButterKnife.bind(this);

        init();

        Intent intent = getIntent();
        recordOutPutPath = intent.getStringExtra("file name");
    }

    private void init() {
        //create instance of surfaceHolder
        surfaceHolder = surfaceView.getHolder();
        //set type of surfaceHolder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //add callbacks of surfaceHolder
        surfaceHolder.addCallback(this);

        //set number of front / back camera
        frontCameraNumber = getFrontCameraNumber();
        backCameraNumber = Camera.CameraInfo.CAMERA_FACING_BACK;

        // getRequest permission for camera to record video
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_RECORD_VIDEO_PERMISSION);


    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        prepareRecord(backCameraNumber, recordOutPutPath);

        startVideoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });


        stopVideoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    finishRecord();
                }
            }
        });

        changeCameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change the type of camera while camera is not started
                if (!isRecording) {
                    if (currentCameraType == frontCameraNumber) {
                        prepareRecord(backCameraNumber, recordOutPutPath);
                    } else {
                        prepareRecord(frontCameraNumber, recordOutPutPath);
                    }
                }
            }
        });
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {


    }



    /*
    this part for initialize camera,record and prepare record
     */

    /**
     * @param cameraType type of camera(front - back)
     * @return the the instance of camera that opened
     */
    //initialize camera to open it
    public Camera initializeCamera(int cameraType) {
        if (camera == null) {
            camera = Camera.open(cameraType);
            currentCameraType = cameraType;
            camera.setDisplayOrientation(PORTRAIT_CAMERA_ORIENTATION);
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

            camera.unlock();
            return camera;
        }
        return null;
    }

    /**
     * @param camera           the instance of camera that it was created
     * @param outPutRecordPath the path of the output record file in sdcard or phone cache
     * @param cameraType       type of camera(front - back)
     */
    //initialize videoRecord
    private void initializeRecord(Camera camera, String outPutRecordPath, int cameraType) {
        recorder = new MediaRecorder();
        recorder.setCamera(camera);
        recorder.setPreviewDisplay(surfaceHolder.getSurface());
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        if (cameraType == frontCameraNumber) {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            recorder.setOrientationHint(PORTRAIT_RECORD_FRONT_ORIENTATION);//270
        } else {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
            recorder.setOrientationHint(PORTRAIT_RECORD_BACK_ORIENTATION);//90
        }
        recorder.setOutputFile(outPutRecordPath);
        recorder.setMaxDuration(VIDEO_MAX_DURATION);
        recorder.setMaxFileSize(VIDEO_MAX_FILE_SIZE);
        recorder.setOnInfoListener(this);

    }

    /**
     * @param cameraType       type of camera(front - back)
     * @param recordOutPutPath the path of the output record file in sdcard or phone cache
     */
    public void prepareRecord(int cameraType, String recordOutPutPath) {
        //stop and release any old record
        stopRecord();
        //set currentCameraType
        currentCameraType = cameraType;
        //initialize camera
        Camera camera = initializeCamera(cameraType);
        //initialize Record
        initializeRecord(camera, recordOutPutPath, cameraType);

        //prepare record for start it
        if (recorder != null) {
            try {
                recorder.prepare();

            } catch (IllegalStateException | IOException e) {
                // This is thrown if the previous calls are not called with the
                // proper order
                e.printStackTrace();
            }

        }
    }


    /*
    controlling the start stop video record [Media Record]
    */

    //start record
    private void startRecord() {
        startVideoIcon.setVisibility(View.GONE);
        changeCameraIcon.setVisibility(View.GONE);
        stopVideoIcon.setVisibility(View.VISIBLE);

        if (!isRecording && recorder != null) {
            recorder.start();
            isRecording = true;
        }
    }

    //stop record
    private void stopRecord() {
        stopVideoIcon.setVisibility(View.GONE);
        startVideoIcon.setVisibility(View.VISIBLE);
        changeCameraIcon.setVisibility(View.VISIBLE);
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
            } catch (RuntimeException stopException) {
                stopException.printStackTrace();
            }
            recorder = null;
            isRecording = false;

            if (camera != null) {
                camera.lock();
                camera.release();
                camera = null;
            }
        }

    }

    //finishing the record and go back to previous activity
    private void finishRecord() {
        stopRecord();
        //get Duration of video
        String videoDuration = reformatTime(getRecordDuration(recordOutPutPath));

        VideoMsg videoMsg = new VideoMsg();
        String msg = videoMsg.setVideoData("per1", "per2", recordOutPutPath, videoDuration, getTimeNow());

        //for file name
        ChatPageDeaf.COUNTER++;

        //go back to chatActivity
        //result=Video msg
        Intent intent = new Intent();
        intent.putExtra("msg", msg);
        setResult(RESULT_OK, intent);
        finish();
    }

    /*
    part to get information [time - camera number]
     */
    //get current time for the message
    @NonNull
    private String getTimeNow() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }

    //get number of front camera if available
    private int getFrontCameraNumber() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo newInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, newInfo);
            if (newInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return NO_FRONT_CAMERA;
    }

    private int getRecordDuration(String recordOutPutPath) {
        int duration = 0;
        //create uri with specific path
        Uri uri = Uri.parse(recordOutPutPath);
        //create media player by path
        MediaPlayer mediaPlayer = MediaPlayer.create(this, uri);
        //getDuration of video
        duration = mediaPlayer.getDuration();
        // release media from memory
        mediaPlayer.release();
        return duration;
    }

    /**
     * @param time time of record in milliSeconds
     * @return formatted time (min:sec)
     */
    //update format of duration of the video
    private String reformatTime(int time) {
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


    //handling record permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_VIDEO_PERMISSION:
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    //handling if duration of reocord or file size is reached
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            finishRecord();
        }
    }

}