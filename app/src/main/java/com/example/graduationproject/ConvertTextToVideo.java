package com.example.graduationproject;

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

        int videoNumber = 0;

        for (String currentWord : msgArray) {
            String word = currentWord.toLowerCase();


            Log.v(TAG, "current Word is : " + word);
            DatabaseQueries.downloadFramesOfWord(new DatabaseQueries.DownloadFramesOfWord() {
                @Override
                public void afterDownloadFramesOfWord(boolean isFound, String framesFolderPath, int videoNumber) {
                    if (isFound) {
                        videosPath.add(videoNumber, framesFolderPath);
                        if (currentWord.equals(msgArray[msgArray.length - 1])) {
                            getVideosPaths.afterGetVideosPaths(videosPath);
                        }
                    } else {
                        char[] wordCharArray = word.toCharArray();
                        for (char currentChar : wordCharArray) {
                            String character = currentChar + "";
                            DatabaseQueries.downloadFramesOfWord(new DatabaseQueries.DownloadFramesOfWord() {
                                @Override
                                public void afterDownloadFramesOfWord(boolean isFound, String framesFolderPath, int videoNumber) {
                                    if (isFound)
                                        videosPath.add(videoNumber, framesFolderPath);
                                    else
                                        Log.v(TAG, "not recognized word");

                                    if (currentWord.equals(msgArray[msgArray.length - 1]) && currentChar == wordCharArray[wordCharArray.length - 1]) {
                                        getVideosPaths.afterGetVideosPaths(videosPath);
                                    }

                                }
                            }, character, videoNumber);
                        }
                    }

                }
            }, word, videoNumber);

            videoNumber++;
        }
    }


    public void convert(Converted converted) {

        AnimationDrawable animationDrawable = new AnimationDrawable();

        downloadVideos(new GetVideosPaths() {
            @Override
            public void afterGetVideosPaths(ArrayList<String> videosPath) {
                Log.v(TAG, "videosPath: " + videosPath.toString());

                for (String currentFolderPath : videosPath) {
                    File directory = new File(currentFolderPath);
                    File[] files = directory.listFiles();
                    Log.d("Files", "Size: " + files.length);
                    for (File currentFile : files) {
                        Log.d("Files", "FileName:" + currentFile.getName());
                        animationDrawable.addFrame(Drawable.createFromPath(currentFile.getPath()), 50);
                    }
                }
                converted.afterConverted(animationDrawable);
            }
        });

    }


    public interface GetVideosPaths {
        void afterGetVideosPaths(ArrayList<String> videosPath);
    }

    public interface Converted {
        void afterConverted(AnimationDrawable animationDrawable);
    }
}
