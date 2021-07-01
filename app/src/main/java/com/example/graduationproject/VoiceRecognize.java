package com.example.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognize {
    private Context context;
    private final SpeechRecognizer mSpeechRecognizer;
    private final Intent mSpeechRecognizerIntent;
    private Result result;

    public VoiceRecognize(Context context, Result result) {
        this.context = context;
        this.result = result;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                //for long record
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {


                //getting all the matches
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    result.result(matches.get(0));

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    public void startRecognize() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    public void stopRecognize() {
        mSpeechRecognizer.stopListening();
    }

    public interface Result {
        void result(String msg);
    }
}
