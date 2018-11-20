package com.example.vclab.moodlightshare.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private CircleImageView CircleImageView_Button01;
    private CircleImageView CircleImageView_Button02;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
               }else{
                   CircleImageView_Button01.setImageResource(R.drawable.bulb_black);
               }

            }
        });

        CircleImageView_Button02 = (CircleImageView)view.findViewById(R.id.circlebutton2);
        CircleImageView_Button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.Modestates = 1; // RockerMode
                if(MainActivity.isMusicOn){
                    CircleImageView_Button02.setImageResource(R.drawable.music);
                }else{
                    CircleImageView_Button02.setImageResource(R.drawable.music_black);
                }
                MainActivity.isMusicOn = !MainActivity.isMusicOn;
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

//    private int getColor(ImageView selectedImage,int evX, int evY){
//        selectedImage.setDrawingCacheEnabled(true);
//        Bitmap bitmap=Bitmap.createBitmap(selectedImage.getDrawingCache());
//        selectedImage.setDrawingCacheEnabled(false);
//        return bitmap.getPixel(evX,evY);
//    }

    //
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
