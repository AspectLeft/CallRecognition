package com.example.administrator.callrecognition;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Administrator on 2016/8/20.
 */
public class Recognition extends Service {
    TelephonyManager telephonyManager;
    PhoneStateListener listener;
    private final static String TAG = "Secretary";
    private SpeechRecognizer sr;

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new secretary());
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingnumber)
            {
                String echo = "";
                switch (state)
                {
                    case TelephonyManager.CALL_STATE_IDLE:
                        echo = "IDLE";

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        echo = "OFFHOOK";
                        startSpeechToText();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        echo = "RINGING";

                        break;
                }
                Toast.makeText(Recognition.this, echo, Toast.LENGTH_SHORT).show();
            }
        };
        telephonyManager.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    class secretary implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);

            Toast.makeText(Recognition.this, "error " + error, Toast.LENGTH_SHORT).show();
        }
        public void onResults(Bundle results)
        {
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            Toast.makeText(Recognition.this, data.get(0), Toast.LENGTH_SHORT).show();
            startSpeechToText();
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void startSpeechToText()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "...");
        sr.startListening(intent);


    }
}
