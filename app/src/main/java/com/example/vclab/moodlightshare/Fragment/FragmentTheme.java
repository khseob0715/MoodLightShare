package com.example.vclab.moodlightshare.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vclab.moodlightshare.Activity.MainActivity;
import com.example.vclab.moodlightshare.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTheme extends Fragment implements View.OnClickListener{

    CircleImageView[] Theme;
    CircleImageView[] AnimeTheme;
    TabHost tabHost1;

    public static int selected_theme;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_theme,null);

        Theme = new CircleImageView[4];
        AnimeTheme = new CircleImageView[4];

        Theme[0] = (CircleImageView)view.findViewById(R.id.theme01);
        Theme[1] = (CircleImageView)view.findViewById(R.id.theme02);
        Theme[2] = (CircleImageView)view.findViewById(R.id.theme03);
        Theme[3] = (CircleImageView)view.findViewById(R.id.theme04);

        AnimeTheme[0] = (CircleImageView)view.findViewById(R.id.AnimationTheme01);
        AnimeTheme[1] = (CircleImageView)view.findViewById(R.id.AnimationTheme02);
        AnimeTheme[2] = (CircleImageView)view.findViewById(R.id.AnimationTheme03);
        AnimeTheme[3] = (CircleImageView)view.findViewById(R.id.AnimationTheme04);

        for(int i = 0 ; i < 4; i++) {
            Theme[i].setOnClickListener(this);
            AnimeTheme[i].setOnClickListener(this);
        }
        tabHost1 = (TabHost) view.findViewById(R.id.tabHost1) ;


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        tabHost1.setup() ;

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.MoodTheme) ;
        ts1.setIndicator("Mood Theme") ;

        tabHost1.addTab(ts1)  ;

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.AnimationTheme) ;
        ts2.setIndicator("Animation Theme") ;

        tabHost1.addTab(ts2) ;



        for(int i=0;i<tabHost1.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabHost1.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.theme01:
              //  Toast.makeText(getContext(),"Theme01", Toast.LENGTH_SHORT).show();
                selected_theme = 101;
                break;
            case R.id.theme02:
              //  Toast.makeText(getContext(),"Theme02", Toast.LENGTH_SHORT).show();
                selected_theme = 102;
                break;
            case R.id.theme03:
//                Toast.makeText(getContext(),"Theme03", Toast.LENGTH_SHORT).show();
                selected_theme = 103;
                break;
            case R.id.theme04:
//                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                selected_theme = 104;
                break;
            case R.id.AnimationTheme01:
//                Toast.makeText(getContext(),"AnimationTheme01", Toast.LENGTH_SHORT).show();
                selected_theme = 201;
                break;
            case R.id.AnimationTheme02:
//                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                selected_theme = 202;
                break;
            case R.id.AnimationTheme03:
//                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                selected_theme = 203;
                break;
            case R.id.AnimationTheme04:
//                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                selected_theme = 204;
                break;
        }
        MainActivity.Modestates = 2; // Theme;
    }
}
