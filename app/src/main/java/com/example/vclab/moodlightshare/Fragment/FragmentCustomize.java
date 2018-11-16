package com.example.vclab.moodlightshare.Fragment;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.vclab.moodlightshare.Dialog.ShareDialog;
import com.example.vclab.moodlightshare.R;
import com.example.vclab.moodlightshare.views.CanvasView;


import java.util.List;

public class FragmentCustomize extends Fragment {

    public ImageView customize_selectedImage;
    public String customize_rgbcolor, customize_hexcolor;


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public static int customize_touch_color = 0;

    private ViewGroup buttonlist;

    private boolean pickingColor = false;
    DrawFragment drawFragment;
    CanvasView canvasView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawFragment = new DrawFragment();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.customize_framelayout, drawFragment);
        fragmentTransaction.commit();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customize,null);

        buttonlist = view.findViewById(R.id.buttonlist);

        for (int i = 0; i < buttonlist.getChildCount(); i++)
            buttonlist.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(-2414079));




        customize_selectedImage =view.findViewById(R.id.customzie_spectrum_image);

        customize_selectedImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {
                    final int action = event.getAction();
                    final int evX = (int) event.getX();
                    final int evY = (int) event.getY();
                    // 색받아오기
                    customize_touch_color = getColor(customize_selectedImage, evX, evY);
                    //r,g,b 값 받아오기
                    int r = (customize_touch_color >> 16) & 0xFF;
                    int g = (customize_touch_color >> 8) & 0xFF;
                    int b = (customize_touch_color >> 0) & 0xFF;
                    customize_rgbcolor = String.valueOf(r) + "," + String.valueOf(g) + "," + String.valueOf(b);

                    //hex값 받아오기
                    customize_hexcolor = Integer.toHexString(customize_touch_color);
                    if (customize_hexcolor.length() > 2) {
                        customize_hexcolor = customize_hexcolor.substring(2, customize_hexcolor.length()); //alfa제거
                    }
                    if (action == event.ACTION_UP) {
                        //터치이벤트 설정
                        for (int i = 0; i < buttonlist.getChildCount(); i++)
                            buttonlist.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(customize_touch_color));

                        canvasView = drawFragment.getCanvas();
                        Log.e("customize_touch_color",""+customize_touch_color);
                        canvasView.color = customize_touch_color;

                    }

                }catch(Exception e) {

                }
                return true;
            }
        });


        // fill canvas on button click   // 색 복사?
        view.findViewById(R.id.pickerButton).setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickingColor = true;
                canvasView = drawFragment.getCanvas();
                canvasView.setOnTouchListener(new CanvasView.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (pickingColor) {
                            int tempBrushSize = canvasView.brushSize;
                            canvasView.brushSize = 1;
                            List<View> pixels = canvasView.findPixels(motionEvent);
                            canvasView.color = ((ColorDrawable) pixels.get(0).getBackground()).getColor();
                            for (int i = 0; i < buttonlist.getChildCount(); i++)
                                buttonlist.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(canvasView.color));

                            canvasView.brushSize = tempBrushSize;
                            pickingColor = false;
                            return true;
                        }
                        return false;
                    }
                });
                Toast.makeText(getActivity(), R.string.colorpickermessage, Toast.LENGTH_SHORT).show();
            }
        });

        // fill canvas on button click
        view.findViewById(R.id.fillButton).setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView = drawFragment.getCanvas();
                canvasView.fill();
            }
        });

        // reset canvas on button click
        view.findViewById(R.id.clearButton).setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView = drawFragment.getCanvas();
                canvasView.fill_white(0);
            }
        });

        // save button
        view.findViewById(R.id.saveButton).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

//                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//                dialog.setTitle(R.string.save);
//
//                final EditText input = new EditText(getContext());
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//                input.setHint(R.string.savename);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.setMarginStart(60);
//                params.setMarginEnd(60);
//                input.setLayoutParams(params);
//                dialog.setView(input);
//
//                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (!TextUtils.isEmpty(input.getText())) {
//                    //        Save save = new Save(input.getText().toString(), canvas.toString());
//            //                mListener.onCanvasSaved(save);
//                        } else {
//                            Toast.makeText(getActivity(), R.string.invalidsavename, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();

                ShareDialog customDialog = new ShareDialog(getContext());

                // 커스텀 다이얼로그를 호출한다.
//                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                customDialog.callFunction();

            }
        });


        view.findViewById(R.id.brushButton).setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(R.string.brushsize);

                final SeekBar input = new SeekBar(getContext());
                input.setMax(17);
                input.setProgress(drawFragment.getCanvas().brushSize-1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginStart(100);
                input.setLayoutParams(params);
                dialog.setView(input);

                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawFragment.getCanvas().brushSize = input.getProgress()+1;
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
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
