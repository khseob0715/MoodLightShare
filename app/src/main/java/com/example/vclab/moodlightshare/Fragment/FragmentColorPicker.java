package com.example.vclab.moodlightshare.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vclab.moodlightshare.Activity.MainActivity;
import com.example.vclab.moodlightshare.Bluno.BleCmd;
import com.example.vclab.moodlightshare.Bluno.BlunoLibrary;
import com.example.vclab.moodlightshare.Dialog.CustomFlag;
import com.example.vclab.moodlightshare.R;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class FragmentColorPicker extends Fragment {

    public TextView hexValue,rgbValue;
    public ImageView selectedImage,color_display;
    public String rgbcolor,hexcolor;

    private ColorPickerView colorPickerView;

    // 플래그 처음에 안뜨게
    private boolean FLAG_PALETTE = false;
    private boolean FLAG_SELECTOR = false;

    private TextView textView;
    private AlphaTileView alphaTileView;

    TextToSpeech tts;

    public static int SPEECH_ON = 0;

    private CircleImageView CircleImageView_Button01;
    private CircleImageView CircleImageView_Button02;
    private CircleImageView CircleImageView_Button03;
    private CircleImageView CircleImageView_Button04;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},5);
        }

        tts=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_color_picker,null);

        CircleImageView_Button01 = (CircleImageView)view.findViewById(R.id.circlebutton);
        CircleImageView_Button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // OnOff;
               MainActivity.isLastSwitchOn = !MainActivity.isLastSwitchOn;
               Toast.makeText(getContext(),"click " + MainActivity.isLastSwitchOn, Toast.LENGTH_SHORT).show();

               if(MainActivity.isLastSwitchOn){
                   CircleImageView_Button01.setImageResource(R.drawable.bulb);
                   MainActivity.Modestates = 0; // Ledmode;
                   MainActivity.isMusicOn = false;
                   MainActivity.isSpeechOn = false;
                   MainActivity.isSleepOn = false;
                   CircleImageView_Button02.setImageResource(R.drawable.music_black);
                   CircleImageView_Button03.setImageResource(R.drawable.sleep_black);
                   CircleImageView_Button04.setImageResource(R.drawable.speeh_black);
               }else{
                   CircleImageView_Button01.setImageResource(R.drawable.bulb_black);
               }

            }
        });

        CircleImageView_Button02 = (CircleImageView)view.findViewById(R.id.circlebutton2);
        CircleImageView_Button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.isMusicOn = !MainActivity.isMusicOn;
                if(MainActivity.isMusicOn){
                    CircleImageView_Button02.setImageResource(R.drawable.music);
                    MainActivity.Modestates = 1; // RockerMode
                    MainActivity.isLastSwitchOn = false;
                    MainActivity.isSleepOn = false;
                    MainActivity.isSpeechOn = false;
                    CircleImageView_Button01.setImageResource(R.drawable.bulb_black);
                    CircleImageView_Button03.setImageResource(R.drawable.sleep_black);
                    CircleImageView_Button04.setImageResource(R.drawable.speeh_black);
                }else{
                    CircleImageView_Button02.setImageResource(R.drawable.music_black);
                }

            }
        });

        CircleImageView_Button03 = (CircleImageView)view.findViewById(R.id.circlebutton3);
        CircleImageView_Button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                MainActivity.isSleepOn = !MainActivity.isSleepOn;
                if(MainActivity.isSleepOn){
                    CircleImageView_Button03.setImageResource(R.drawable.sleep);
                    MainActivity.Modestates = 4; // Sleep
                    MainActivity.isLastSwitchOn = false;
                    MainActivity.isMusicOn = false;
                    MainActivity.isSpeechOn = false;
                    CircleImageView_Button01.setImageResource(R.drawable.bulb_black);
                    CircleImageView_Button02.setImageResource(R.drawable.music_black);
                    CircleImageView_Button04.setImageResource(R.drawable.speeh_black);
                }else{
                    CircleImageView_Button03.setImageResource(R.drawable.sleep_black);
                }

            }
        });

        CircleImageView_Button04 = (CircleImageView)view.findViewById(R.id.circlebutton4);
        CircleImageView_Button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.isSpeechOn = !MainActivity.isSpeechOn;
                if(MainActivity.isSpeechOn){
                    CircleImageView_Button04.setImageResource(R.drawable.speech);
                    MainActivity.Modestates = 5; // Speech
                    MainActivity.isLastSwitchOn = false;
                    MainActivity.isMusicOn = false;
                    MainActivity.isSleepOn = false;
                    CircleImageView_Button01.setImageResource(R.drawable.bulb_black);
                    CircleImageView_Button02.setImageResource(R.drawable.music_black);
                    CircleImageView_Button03.setImageResource(R.drawable.sleep_black);
                    inputVoice();

                }else{
                    CircleImageView_Button04.setImageResource(R.drawable.speeh_black);
                }

            }
        });


        textView = view.findViewById(R.id.textView);
        alphaTileView = view.findViewById(R.id.alphaTileView);

        colorPickerView = view.findViewById(R.id.colorPickerView);
        colorPickerView.setFlagView(new CustomFlag(getContext(), R.layout.layout_flag));
        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                setLayoutColor(envelope);

                int[] argb = envelope.getArgb();

                MainActivity.color_r =argb[1];
                MainActivity.color_g =argb[2];
                MainActivity.color_b =argb[3];
                MainActivity.colorChange();
            }
        });

        // 슬라이드바
        final AlphaSlideBar alphaSlideBar = view.findViewById(R.id.alphaSlideBar);
        colorPickerView.attachAlphaSlider(alphaSlideBar);

        final BrightnessSlideBar brightnessSlideBar = view.findViewById(R.id.brightnessSlide);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);

        return view;
    }


    public void inputVoice() {
        try{
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getContext().getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
            final SpeechRecognizer stt=SpeechRecognizer.createSpeechRecognizer(getContext());
            stt.setRecognitionListener(new RecognitionListener() {
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

                }

                @Override
                public void onError(int error) {
                    stt.destroy();
                }

                //음성결과 넘어오는부분
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result=(ArrayList<String>)results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                  //  txt.append(result.get(0)+"\n");
                    replyAnswer(result.get(0));
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
            Log.e("AI","exception");
        }
    }

    private void replyAnswer(String input){
        if(input.equals("켜줘") || input.equals("켜 줘") || input.equals("켜")){
            SPEECH_ON = 1;
        }
        else if(input.equals("노래")){
            Toast.makeText(getContext(),input.toString(),Toast.LENGTH_SHORT).show();
            SPEECH_ON = 2;
        }else if(input.equals("꺼줘") || input.equals("꺼 줘") || input.equals("꺼")){
            SPEECH_ON = 0;
        }else if(input.equals("수면모드")){
            SPEECH_ON = 3;
        }


    }

    private void setLayoutColor(ColorEnvelope envelope) {
        textView.setText("#" + envelope.getHexCode());
        alphaTileView.setPaintColor(envelope.getColor());
    }


    // 스펙트럼 위에 그림

    public void palette() {
        if (FLAG_PALETTE)
            colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(getContext(), R.drawable.palette));
        else
            colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(getContext(), R.drawable.palettebar));
        FLAG_PALETTE = !FLAG_PALETTE;
    }

    // 슬라이드바 변경
    public void selector() {
        if (FLAG_SELECTOR)
            colorPickerView.setSelectorDrawable(ContextCompat.getDrawable(getContext(), R.drawable.wheel));
        else
            colorPickerView.setSelectorDrawable(ContextCompat.getDrawable(getContext(), R.drawable.wheel_dark));
        FLAG_SELECTOR = !FLAG_SELECTOR;
    }
}
