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

import com.example.vclab.moodlightshare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTheme extends Fragment implements View.OnClickListener{

    TabHost tabHost1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_theme,null);

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
                Toast.makeText(getContext(),"Theme01", Toast.LENGTH_SHORT).show();
                break;
            case R.id.theme02:
                Toast.makeText(getContext(),"Theme02", Toast.LENGTH_SHORT).show();
                break;
            case R.id.theme03:
                Toast.makeText(getContext(),"Theme03", Toast.LENGTH_SHORT).show();
                break;
            case R.id.theme04:
                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                break;
            case R.id.AnimationTheme01:
                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                break;
            case R.id.AnimationTheme02:
                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                break;
            case R.id.AnimationTheme03:
                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                break;
            case R.id.AnimationTheme04:
                Toast.makeText(getContext(),"Theme04", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
