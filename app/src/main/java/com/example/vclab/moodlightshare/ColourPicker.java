package com.example.vclab.moodlightshare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
public class ColourPicker extends Activity {

    public TextView hexValue,rgbValue;
    public ImageView selectedImage,color_display;
    public String rgbcolor,hexcolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour_picker);

        hexValue=findViewById(R.id.hexcolor);
        rgbValue=findViewById(R.id.rgbcolor);
        color_display=findViewById(R.id.color_display);
        selectedImage=findViewById(R.id.seleted_img);

        selectedImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {
                    final int action = event.getAction();
                    final int evX = (int) event.getX();
                    final int evY = (int) event.getY();
                    //색받아오기
                    int touchColor = getColor(selectedImage, evX, evY);
                    //r,g,b 값 받아오기
                    int r = (touchColor >> 16) & 0xFF;
                    int g = (touchColor >> 8) & 0xFF;
                    int b = (touchColor >> 0) & 0xFF;
                    rgbcolor = String.valueOf(r) + "," + String.valueOf(g) + "," + String.valueOf(b);
                    rgbValue.setText("RGB:     " + rgbcolor);

                    //hex값 받아오기
                    hexcolor = Integer.toHexString(touchColor);
                    if (hexcolor.length() > 2) {
                        hexcolor = hexcolor.substring(2, hexcolor.length()); //alfa제거
                    }
                    if (action == event.ACTION_UP) {
                        //터치이벤트 설정
                        color_display.setBackgroundColor(touchColor);
                        hexValue.setText("HEX:   " + hexcolor);
                    }

                }catch(Exception e) {

                }
                return true;
            }
        });
    }

    private int getColor(ImageView selectedImage,int evX, int evY){
        selectedImage.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(selectedImage.getDrawingCache());
        selectedImage.setDrawingCacheEnabled(false);
        return bitmap.getPixel(evX,evY);
    }
}