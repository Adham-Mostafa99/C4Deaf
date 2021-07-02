package com.example.graduationproject.text_to_video;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.example.graduationproject.models.DatabaseQueries;

import java.io.File;
import java.util.ArrayList;

public class ConvertTextToVideo {
    public final static String TAG = "ConvertTextToVideo";
    private String[] msgArray;


    public ConvertTextToVideo(String[] msgArray) {
        this.msgArray = msgArray;
    }

    private void downloadVideos(GetVideosPaths getVideosPaths) {
        ArrayList<String> videosPath = new ArrayList<>();


        for (String currentWord : msgArray) {
            String word = currentWord.toLowerCase();

            Log.v(TAG, "current Word is : " + word);


            DatabaseQueries.downloadFramesOfWord((isFound, framesFolderPath) -> {
                if (isFound) {

                    videosPath.add(framesFolderPath);

                    if (currentWord.equals(msgArray[msgArray.length - 1])) {
                        getVideosPaths.afterGetVideosPaths(videosPath);
                    }

                } else {

                    char[] wordCharArray = word.toCharArray();

                    for (char currentChar : wordCharArray) {

                        String character = currentChar + "";

                        DatabaseQueries.downloadFramesOfWord((isFound1, framesFolderPath1) -> {

                            if (isFound1)
                                videosPath.add(framesFolderPath1);
                            else
                                Log.v(TAG, "not recognized word");


                            if (currentWord.equals(msgArray[msgArray.length - 1]) && currentChar == wordCharArray[wordCharArray.length - 1]) {
                                getVideosPaths.afterGetVideosPaths(videosPath);
                            }

                        }, character);
                    }
                }

            }, word);

        }
    }


    public void convert(Converted converted) {

        AnimationDrawable animationDrawable = new AnimationDrawable();

        downloadVideos(videosPath -> {


            for (String currentFolderPath : videosPath) {

                File directory = new File(currentFolderPath);
                File[] files = directory.listFiles();

                Log.d("Files", "Size: " + files.length);
                for (File currentFile : files) {
                    Log.d("Files", "FileName:" + currentFile.getName());
                    animationDrawable.addFrame(Drawable.createFromPath(currentFile.getPath()), 30);
                }
            }
            converted.afterConverted(animationDrawable);
        });

    }


    public interface GetVideosPaths {
        void afterGetVideosPaths(ArrayList<String> videosPath);
    }

    public interface Converted {
        void afterConverted(AnimationDrawable animationDrawable);
    }
}
