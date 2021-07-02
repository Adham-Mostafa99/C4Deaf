package com.example.graduationproject.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.video_to_text.HandRecognition;
import com.example.graduationproject.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpenCvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2,
        HandRecognition.Statement {

    private static final String TAG = "OpenCvActivity";
    private Activity activity = this;

    @BindView(R.id.camera_view)
    JavaCameraView javaCameraView;
    @BindView(R.id.finish)
    Button finish;

    Mat mRGBA, mGray;

    HandRecognition handRecognition;
    String statement;
    HandRecognition.Statement statementInterface = this;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {

            switch (status) {
                case BaseLoaderCallback.SUCCESS:

                    try {
                        int size = 128;
                        handRecognition = new HandRecognition(
                                finish,
                                statementInterface,
                                getAssets(),
                                getApplicationContext(),
                                activity,
                                "mask_model.tflite",
                                size);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "model is not loaded");
                    }

                    javaCameraView.enableView();
                    Log.d(TAG, "opencv is loaded");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_open_cv);
        ButterKnife.bind(this);
        hideSystemUI();

        javaCameraView = (JavaCameraView) findViewById(R.id.camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        finish.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("msg", statement);
            setResult(RESULT_OK, intent);
            finish();
        });


    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) throws IOException {
        mRGBA = inputFrame.rgba();
        mGray = inputFrame.gray();

        mRGBA = handRecognition.recognizeImage(mRGBA);

        return mRGBA;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "opencv successfully");
            try {
                baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "opencv not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }
    }

    @Override
    public void statement(String msg) {
        statement = msg;
    }

    private void hideSystemUI() {
        Window window = this.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30
            window.setDecorFitsSystemWindows(false);
        } else {

            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }


    }
}