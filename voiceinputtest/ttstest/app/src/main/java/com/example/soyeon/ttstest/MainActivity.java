package com.example.soyeon.ttstest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.speech.tts.TextToSpeech;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},5);
        }

        LinearLayout layout=new LinearLayout(this);
        layout.setOrientation(1);
        final TextView txt=new TextView(this);
        txt.setText("\n");
        txt.setTextSize(18);
        layout.addView(txt);
        Button input=new Button(this);
        input.setText("input");
        input.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                inputVoice(txt);
            }
        });
        layout.addView(input);
        ScrollView scroll=new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
    }

    public void inputVoice(final TextView txt) {
        try{
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
            final SpeechRecognizer stt=SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("start talking");
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
                    toast("end");

                }

                @Override
                public void onError(int error) {
                    toast("error: "+error);
                    stt.destroy();

                }

                //음성결과 넘어오는부분
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result=(ArrayList<String>)results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    txt.append(result.get(0)+"\n");
                    replyAnswer(result.get(0),txt);
                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        }catch (Exception e){
            toast(e.toString());
        }
    }

    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void replyAnswer(String input,TextView txt){
        if(input.equals("안녕하세요")){
            txt.append("work properly\n");
        }
        else{
            txt.append(input+"\n");
        }

    }
}
