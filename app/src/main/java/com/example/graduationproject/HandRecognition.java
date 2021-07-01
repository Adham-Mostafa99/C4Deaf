package com.example.graduationproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static org.opencv.core.Core.inRange;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

public class HandRecognition {

    private static String TAG = "HandRecognition";
    private Interpreter interpreter;
    private Context context;
    private GpuDelegate gpuDelegate = null;
    StringBuilder statementString;
    private int INPUT_SIZE;
    boolean isFree = true;
    Activity activity;
    Statement statementInterface;
    Button button;

    public HandRecognition(Button button, Statement statementInterface, AssetManager assetManager, Context context, Activity activity, String modelPath, int input_size) throws IOException {
        INPUT_SIZE = input_size;
        this.context = context;
        this.activity = activity;
        this.button = button;
        this.statementInterface = statementInterface;

        statementString = new StringBuilder();

        initModel(assetManager, modelPath);

    }

    public void initModel(AssetManager assetManager, String modelPath) throws IOException {
        Interpreter.Options options = new Interpreter.Options();


        gpuDelegate = new GpuDelegate();

        options.setNumThreads(4);
        interpreter = new Interpreter(loadModel(assetManager, modelPath), options);

        Log.d(TAG, "model is loaded");
    }

    private MappedByteBuffer loadModel(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(modelPath);
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOfSet = assetFileDescriptor.getStartOffset();
        long declaredLength = assetFileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOfSet, declaredLength);
    }

    public Mat recognizeImage(Mat mat_image) throws IOException {

//        Core.flip(mat_image.t(), mat_image, 1);

        Mat grayScaleImage = new Mat();

        Imgproc.cvtColor(mat_image, grayScaleImage, Imgproc.COLOR_RGBA2BGR);

        int point1X = 1000;
        int point1Y = 450;
        int point2X = 350;
        int point2Y = 50;
        int width = point1X - point2X;
        int height = point1Y - point2Y;


        //draw rect
        Imgproc.rectangle(mat_image,
                new Point(point1X, point1Y),
                new Point(point2X, point2Y),
                new Scalar(0, 255, 0), 2);

        //for clipping
        Rect roi = new Rect(point2X, point2Y
                , width, height);

        Mat cropped_rgb = new Mat(mat_image, roi);

        Mat finalMat = mask(cropped_rgb);


        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(finalMat, bitmap);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);



        ByteBuffer byteBuffer = convertBitmapToBytebuffer(scaledBitmap);

        if (isFree)
            runModel2(byteBuffer, mat_image);

//        Core.flip(mat_image.t(), mat_image, 0);

        return mat_image;
    }

    public Mat mask2(Mat mat) {

//        Mat gray = new Mat();
//
//        Imgproc.cvtColor(mat, gray, COLOR_BGR2GRAY);
//        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        Mat hsv = new Mat();
        Imgproc.cvtColor(mat, hsv, COLOR_BGR2HSV);


        Scalar low = new Scalar(0, 49, 0);
        Scalar high = new Scalar(255, 225, 105);

        Mat mask = new Mat(mat.rows(), mat.cols(), CvType.CV_8U, Scalar.all(0));

        inRange(mat, low, high, mask);
//
//        Mat kernel = new Mat(4, 4, CvType.CV_32F) {
//            {
//                put(0, 0, 1);
//                put(0, 1, 1);
//                put(0, 2, 1);
//                put(0, 3, 1);
//
//                put(1, 0, 1);
//                put(1, 1, 1);
//                put(1, 2, 1);
//                put(1, 3, 1);
//
//                put(2, 0, 1);
//                put(2, 1, 1);
//                put(2, 2, 1);
//                put(2, 3, 1);
//
//                put(3, 0, 1);
//                put(3, 1, 1);
//                put(3, 2, 1);
//                put(3, 3, 1);
//
//
//            }
//        };
//
//
//        Mat dilation = new Mat();
//        Imgproc.dilate(mask, dilation, kernel, new Point(-1, -1), 2);


//        Core.bitwise_and();

//                Imgproc.threshold(mat, mat, 25, 255, THRESH_BINARY);


        return mask;

    }

    public Mat mask(@NonNull Mat mat) {

        Mat gray = new Mat();
        mat.copyTo(gray);

        //Extract red color channel (because the hand color is more red than the background).
        List<Mat> planes = new ArrayList<>();
        planes.add(new Mat());
        planes.add(new Mat());
        planes.add(new Mat());
        Core.split(gray, planes);

        //Apply binary threshold using automatically selected threshold (using cv2.THRESH_OTSU parameter).
        Mat thresh_gray = new Mat();
        Imgproc.threshold(planes.get(2), thresh_gray, 0, 255, THRESH_BINARY + THRESH_OTSU);

        //Use "opening" morphological operation for clearing some small dots (noise)
        Imgproc.morphologyEx(thresh_gray, thresh_gray, MORPH_OPEN, getStructuringElement(MORPH_ELLIPSE, new Size(3, 3)));

        //Use "closing" morphological operation for closing small gaps
        Imgproc.morphologyEx(thresh_gray, thresh_gray, MORPH_CLOSE, getStructuringElement(MORPH_ELLIPSE, new Size(9, 9)));

        return thresh_gray;

    }


    private ByteBuffer convertBitmapToBytebuffer(Bitmap scaledBitmap) {
        ByteBuffer byteBuffer;

        int inputSize = INPUT_SIZE;

        byteBuffer = ByteBuffer.allocateDirect(4 * 1 * inputSize * inputSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[inputSize * inputSize];
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth()
                , scaledBitmap.getHeight());

        int pixels = 0;
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                final int val = intValues[pixels++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }

    public void runModel2(ByteBuffer byteBuffer, Mat mat_image) {
        float[][] hand_value = new float[1][26];

        interpreter.run(byteBuffer, hand_value);

        int index = getMax(hand_value[0]);

        String name = getHandName(index);

        if (name != "none") {
            isFree = false;
            statementString.append(name);

            Imgproc.putText(mat_image, name
                    , new Point(50, 250)
                    , 1, 1.5
                    , new Scalar(255, 255, 255, 150), 2);

            activity.runOnUiThread(new Runnable() {
                public void run() {

                    new CountDownTimer(4000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            isFree = true;
                        }

                    }.start();
                }
            });

        }


        Log.d(TAG, "out: " + Arrays.toString(hand_value[0]));
        Log.d(TAG, "max: " + index);
        Log.d(TAG, "name: " + name);
        Log.d(TAG, "state: " + statementString);
        statementInterface.statement(statementString.toString());
    }

    public int getMax(float[] arr) {
        int maxAt = 0;

        for (int i = 0; i < arr.length; i++) {
            maxAt = arr[i] > arr[maxAt] ? i : maxAt;
        }
        return maxAt;
    }

    private String getHandName(int read_hand_position) {
        String val = "";
        switch (read_hand_position) {
            case 0:
                val = "a";
                break;
            case 1:
                val = "b";
                break;
            case 2:
                val = "c";
                break;
            case 3:
                val = "d";
                break;
            case 4:
                val = "e";
                break;
            case 5:
                val = "f";
                break;
            case 6:
                val = "g";
                break;
            case 7:
                val = "h";
                break;
            case 8:
                val = "i";
                break;
            case 9:
                val = "j";
                break;
            case 10:
                val = "k";
                break;
            case 11:
                val = "l";
                break;
            case 12:
                val = "m";
                break;
            case 13:
                val = "n";
                break;
            case 14:
                val = "o";
                break;
            case 15:
                val = "p";
                break;
            case 16:
                val = "q";
                break;
            case 17:
                val = "r";
                break;
            case 18:
                val = "s";
                break;
            case 19:
                val = "t";
                break;
            case 20:
                val = "u";
                break;
            case 21:
                val = "v";
                break;
            case 22:
                val = "w";
                break;
            case 23:
                val = "x";
                break;
            case 24:
                val = "y";
                break;
            case 25:
                val = "z";
                break;
            default:
                val = "none";

        }

        return val;
    }

    public interface Statement {
        void statement(String msg);
    }

}
