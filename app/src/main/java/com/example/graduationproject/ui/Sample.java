package com.example.graduationproject.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.example.graduationproject.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Sample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
//        File rootPath = new File(Environment.getExternalStorageDirectory() + "/" + "DeafChat/record.acc");
//
//        try {
//            run(rootPath.getPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void run(String path) throws IOException {
//        // Instantiates a client
//        try (SpeechClient speechClient = SpeechClient.create()) {
//
//            // The path to the audio file to transcribe
//            String gcsUri = path;
//
//            // Builds the sync recognize request
//            RecognitionConfig config =
//                    RecognitionConfig.newBuilder()
//                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
//                            .setSampleRateHertz(16000)
//                            .setLanguageCode("en-US")
//                            .build();
//            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();
//
//            // Performs speech recognition on the audio file
//            RecognizeResponse response = speechClient.recognize(config, audio);
//            List<SpeechRecognitionResult> results = response.getResultsList();
//
//            for (SpeechRecognitionResult result : results) {
//                // There can be several alternative transcripts for a given chunk of speech. Just use the
//                // first (most likely) one here.
//                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//                System.out.printf("Transcription: %s%n", alternative.getTranscript());
//            }
//        }
    }
}