//package com.example.graduationproject;
//
//import android.Manifest;
//import android.graphics.drawable.AnimationDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.example.graduationproject.models.DatabaseQueries;
//
//import java.io.File;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class Test extends AppCompatActivity {
//
//    @BindView(R.id.play)
//    Button play;
//    @BindView(R.id.gif_view)
//    ImageView gifView;
//    @BindView(R.id.gif_seek)
//    SeekBar gifSeek;
//
//    private final static String PREFIX_VIDEO_HANDLER = "vide";
//    private final static String PREFIX_AUDIO_HANDLER = "soun";
//    int x = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//        ButterKnife.bind(this);
//
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                0);
//
//        AnimationDrawable animationDrawable = new AnimationDrawable();
//        DatabaseQueries.downloadFramesOfWord(new DatabaseQueries.DownloadFramesOfWord() {
//            @Override
//            public void afterDownloadFramesOfWord(boolean isFound, String framesFolderPath) {
//                if (isFound) {
//                    Log.d("Files", "Path: " + framesFolderPath);
//                    File directory = new File(framesFolderPath);
//                    File[] files = directory.listFiles();
//                    Log.d("Files", "Size: " + files.length);
//                    for (File currentFile : files) {
//                        Log.d("Files", "FileName:" + currentFile.getName());
//                        animationDrawable.addFrame(Drawable.createFromPath(currentFile.getPath()), 50);
//                    }
//                    gifView.setBackgroundDrawable(animationDrawable);
//                    animationDrawable.start();
//
//                } else {
//                    Log.v("State", "Not Found Word");
//                }
//
//            }
//        }, "best");
//
////
////        String file1 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "stay.avi";
////        String file2 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "study.avi";
////        String file3 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "this.avi";
////        String file4 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "r.mp4";
////        String file5 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "o.mp4";
////
////        String f1 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "o.mp4";
////        String f2 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "r.mp4";
////        String f3 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "out1.mp4";
////        String f4 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "out2.mp4";
////        String f5 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "out3.mp4";
////        String f6 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "b.mp4";
////        String outPut = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "outPut.mp4";
////
////
////        MergeMP4Videos mergeMP4Videos = new MergeMP4Videos();
////
////        try {
////            mergeMP4Videos.appendTwoVideos(f5, f6, outPut);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
//        //        ArrayList<String> videosPaths = new ArrayList<>();
////        videosPaths.add(file1);
////        videosPaths.add(file2);
////        videosPaths.add(file3);
//////        videosPaths.add(file4);
//////        videosPaths.add(file5);
//
//
////        try {
////            MergeMP4Videos mergeMP4Videos=new MergeMP4Videos();
////            mergeMP4Videos.merge(this,videosPaths, outPut);
////        } catch (Throwable e) {
////            e.printStackTrace();
////        }
//
//
////            path = MergeMP4Videos.merge(videosPaths,outPut);
//
////        TestMerge testMerge = new TestMerge();
////        testMerge.execute();
//
////        String file1 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "great.gif";
////        String file2 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "great.gif";
////        String file3 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "great.gif";
////        String file4 = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "great.gif";
////        String out = Environment.getExternalStorageDirectory() + "/" + "DeafChat" + "/" + "convertedVideos" + "/" + "out.gif";
//
//
////
////        merg(file1, file2, out);
////
////        Glide.with(this)
////                .load(out)
////                .into(gifView);
//
//
////        ArrayList<String> gifs = new ArrayList<>();
////        gifs.add(file1);
////        gifs.add(file2);
////        gifs.add(file3);
////        gifs.add(file4);
////
////
////        try {
////            appendMultiGifs(gifs,out);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
////        Drawable resFile = Drawable.createFromPath(file1);
//
//
////        Glide.with(this)
////                .load(resFile)
////                .into(gifView);
//
////        AnimationDrawable animationDrawable = new AnimationDrawable();
////        animationDrawable.addFrame(resFile, 2000);
////
////        gifView.setBackground(animationDrawable);
////
////        gifView.setBackgroundDrawable(animationDrawable);
////        File file=new File(file1);
//////        gifView.setImageResource(file);
////
////        final MediaController mc = new MediaController(this);
////        mc.setMediaPlayer((GifDrawable) gifView.getDrawable());
////        mc.setAnchorView(gifView);
//
////        Glide.with(this)
////                .load(file1)
////                .listener(new RequestListener<Drawable>() {
////                    @Override
////                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
////                        return false;
////                    }
////
////                    @Override
////                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
////
////                        return false;
////                    }
////                })
////                .into(gifView);
//
//
////        ArrayList<Bitmap> bitmaps = spiltGif(file1);
////
////
////        AnimationDrawable animationDrawable = new AnimationDrawable();
////
////        Log.v("TAG", bitmaps.size() + " ");
////
////        for (Bitmap bitmap : bitmaps) {
////            Log.v("TAG", "current" + bitmap);
////            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
////            animationDrawable.addFrame(drawable, 50);
////        }
////
////
////        Glide.with(this)
////                .load(animationDrawable)
////                .into(gifView);
//
//
////        ArrayList<String> gifs = new ArrayList<>();
////        gifs.add(file1);
////        gifs.add(file2);
////        gifs.add(file3);
////        gifs.add(file4);
////
////
////        GifDrawable gifDrawable = (GifDrawable) gifView.getDrawable();
////        gifDrawable.start();
////
////
////        gifDrawable.addAnimationListener(new AnimationListener() {
////            @Override
////            public void onAnimationCompleted(int i) {
////                if (x <= 3) {
////                    x++;
////                    gifView.setBackground(Drawable.createFromPath(gifs.get(x)));
////                }
////            }
////        });
//
//
////        play.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                x = 0;
////                gifDrawable.start();
//////                mc.show();
//////                animationDrawable.start();
//////                Log.v("TAG", animationDrawable.getDuration(0) + "");
//////                Log.v("TAG", animationDrawable.getNumberOfFrames() + "");
////            }
////        });
//
//
//    }
//
////    public void playVideo(String gifPath) {
////
////
////    }
//
//
////    @NonNull
////    public void appendMultiGifs(@NonNull List<String> gifs, String outputPath) throws IOException {
////        List<Movie> inputMovies = new ArrayList<>();
////        for (String input : gifs) {
////            inputMovies.add(MovieCreator.build(input));
////        }
////
////        List<Track> videoTracks = new LinkedList<>();
////        List<Track> audioTracks = new LinkedList<>();
////
////        for (Movie m : inputMovies) {
////            for (Track t : m.getTracks()) {
////                if (PREFIX_AUDIO_HANDLER.equals(t.getHandler())) {
////                    audioTracks.add(t);
////                }
////                if (PREFIX_VIDEO_HANDLER.equals(t.getHandler())) {
////                    videoTracks.add(t);
////                }
////            }
////        }
////
////        Movie outputMovie = new Movie();
////        if (audioTracks.size() > 0) {
////            outputMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
////        }
////        if (videoTracks.size() > 0) {
////            outputMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
////        }
////
////        Container out = new DefaultMp4Builder().build(outputMovie);
////
////        FileChannel fc = new RandomAccessFile(outputPath, "rw").getChannel();
////        out.writeContainer(fc);
////        fc.close();
////    }
//
////    public void merg(String gif1, String gif2, String output) {
////
////        try {
////            FFmpeg fFmpeg = FFmpeg.getInstance(this);
////            fFmpeg.loadBinary(new LoadBinaryResponseHandler());
////
////            String commandStr = "-i concat:" + gif1 + "|" + gif2 + " " + output;
////            String[] command = commandStr.split(" ");
////
////            fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
////
////                @Override
////                public void onStart() {
////                }
////
////                @Override
////                public void onProgress(String message) {
////                    //   Toast.makeText(Main.this,"Progress: "+message,Toast.LENGTH_SHORT).show();
////                    Log.e("Progress TAG: ", message);
////                }
////
////                @Override
////                public void onFailure(String message) {
////                    Toast.makeText(getApplicationContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
////                    Log.e("Failure TAG: ", message);
////                }
////
////                @Override
////                public void onSuccess(String message) {
////                    Toast.makeText(getApplicationContext(), "Success: " + message, Toast.LENGTH_SHORT).show();
////                    Log.e("Success TAG: ", message);
////                }
////
////                @Override
////                public void onFinish() {
////                }
////            });
////        } catch (
////                FFmpegCommandAlreadyRunningException | FFmpegNotSupportedException e) {
////            e.printStackTrace();
////            // Handle if FFmpeg is already running
////        }
////    }
////
////
////    public ArrayList<Bitmap> spiltGif(String gifPath) {
////
////        ArrayList<Bitmap> bitmaps = new ArrayList<>();
////        Glide.with(this)
////                .asGif()
////                .load(gifPath)
////                .into(new SimpleTarget<com.bumptech.glide.load.resource.gif.GifDrawable>() {
////                    @Override
////                    public void onResourceReady(@NonNull com.bumptech.glide.load.resource.gif.GifDrawable resource, @Nullable Transition<? super com.bumptech.glide.load.resource.gif.GifDrawable> transition) {
////                        try {
////                            Object GifState = resource.getConstantState();
////                            Field frameLoader = GifState.getClass().getDeclaredField("frameLoader");
////                            frameLoader.setAccessible(true);
////                            Object gifFrameLoader = frameLoader.get(GifState);
////
////                            Field gifDecoder = gifFrameLoader.getClass().getDeclaredField("gifDecoder");
////                            gifDecoder.setAccessible(true);
////                            StandardGifDecoder standardGifDecoder = (StandardGifDecoder) gifDecoder.get(gifFrameLoader);
////                            for (int i = 0; i < standardGifDecoder.getFrameCount(); i++) {
////                                standardGifDecoder.advance();
////                                bitmaps.add(standardGifDecoder.getNextFrame());
////                            }
////                        } catch (Exception ex) {
////                            ex.printStackTrace();
////                        }
////                    }
////                });
////        return bitmaps;
////
////    }
//
//}