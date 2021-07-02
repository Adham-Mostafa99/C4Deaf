package com.example.graduationproject.speech_to_text;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;


public class ConvertSpeechToText {
    private String filePath;
    private static final String API_KEY = "CMXKZ4Pfg-T9TBBl2VriMpdXlGfQtvc9n5J2rq1ragUG";
    private static final String URL = "https://api.eu-gb.speech-to-text.watson.cloud.ibm.com";

    SpeechRecognitionResults transcript;
    Context context;
    String statement = null;


    public ConvertSpeechToText(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;
    }


    public void convert(OnConvert onConvert) throws FileNotFoundException {
        Log.v("Converter: ", "enter");


        try {
            Convert convert = new Convert(onConvert);
            convert.execute("");


        } catch (RuntimeException e) {
            e.printStackTrace();
        }


    }

    class Convert extends AsyncTask<String, Void, String> {
        OnConvert onConvert;

        public Convert(OnConvert onConvert) {
            this.onConvert = onConvert;
        }

        @Override
        protected String doInBackground(String... strings) {

            String convertedFileName = context.getExternalCacheDir().getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".mp3";

            int rc = FFmpeg.execute(String.format("-i %s -c:v copy -c:a libmp3lame -q:a 4 %s", filePath, convertedFileName));
            if (rc == RETURN_CODE_SUCCESS) {
                File audioFile = new File(convertedFileName);


                IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
                SpeechToText speechToText = new SpeechToText(authenticator);
                speechToText.setServiceUrl(URL);


                RecognizeOptions options = null;
                try {
                    options = new RecognizeOptions.Builder()
                            .audio(audioFile)
                            .contentType(HttpMediaType.AUDIO_MP3)
                            .model("en-AU_NarrowbandModel")
                            .build();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                transcript = speechToText.recognize(options).execute().getResult();

                try {
                    JSONObject jsonObject = new JSONObject(transcript.toString());
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONArray alternativesArray = resultsArray.getJSONObject(i).getJSONArray("alternatives");
                        for (int j = 0; j < alternativesArray.length(); j++) {
                            JSONObject resultObject = alternativesArray.getJSONObject(j);

                            statement = resultObject.getString("transcript");

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return statement;
            } else if (rc == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "Command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                Config.printLastCommandOutput(Log.INFO);
            }
            return "rc is error";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("statement: " + s);
            onConvert.afterConvert(s);
        }
    }



    public interface OnConvert {
        void afterConvert(String msgText);
    }
}
