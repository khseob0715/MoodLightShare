package com.example.vclab.moodlightshare.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vclab.moodlightshare.Dialog.ShareDialog;
import com.example.vclab.moodlightshare.R;

public class FragmentCustomize extends Fragment {

    public ImageView customize_selectedImage, customize_color_display;
    public String customize_rgbcolor, customize_hexcolor;

    public Button ShareButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customize,null);

        ShareButton = view.findViewById(R.id.CustomizeShareButton);
        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                ShareDialog customDialog = new ShareDialog(getContext());

                // 커스텀 다이얼로그를 호출한다.
//                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                 customDialog.callFunction();
            }
        });
        customize_color_display =view.findViewById(R.id.customize_color_display);
        customize_selectedImage =view.findViewById(R.id.customzie_spectrum_image);

        customize_selectedImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {
                    final int action = event.getAction();
                    final int evX = (int) event.getX();
                    final int evY = (int) event.getY();
                    //색받아오기
                    int touchColor = getColor(customize_selectedImage, evX, evY);
                    //r,g,b 값 받아오기
                    int r = (touchColor >> 16) & 0xFF;
                    int g = (touchColor >> 8) & 0xFF;
                    int b = (touchColor >> 0) & 0xFF;
                    customize_rgbcolor = String.valueOf(r) + "," + String.valueOf(g) + "," + String.valueOf(b);

                    //hex값 받아오기
                    customize_hexcolor = Integer.toHexString(touchColor);
                    if (customize_hexcolor.length() > 2) {
                        customize_hexcolor = customize_hexcolor.substring(2, customize_hexcolor.length()); //alfa제거
                    }
                    if (action == event.ACTION_UP) {
                        //터치이벤트 설정
                        customize_color_display.setBackgroundColor(touchColor);

                    }

                }catch(Exception e) {

                }
                return true;
            }
        });

        return view;
    }

    private int getColor(ImageView selectedImage, int evX, int evY){
        selectedImage.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(selectedImage.getDrawingCache());
        selectedImage.setDrawingCacheEnabled(false);
        return bitmap.getPixel(evX,evY);
    }
}
