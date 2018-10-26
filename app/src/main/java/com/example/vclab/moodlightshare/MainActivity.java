package com.example.vclab.moodlightshare;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.vclab.moodlightshare.Fragment.FragmentColorPicker;
import com.example.vclab.moodlightshare.Fragment.FragmentCustomize;
import com.example.vclab.moodlightshare.Fragment.FragmentShare;
import com.example.vclab.moodlightshare.Fragment.FragmentTheme;
import com.example.vclab.moodlightshare.Fragment.FragmentUser;

public class MainActivity extends AppCompatActivity {

    ImageView mainButton, themeButton, customizeButton, shareButton, userButton;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    int Current_Fragment_Index = 1;
    int Select_Fragment_Index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fragment를 불러오는 소스코드.
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new FragmentColorPicker());
        fragmentTransaction.commit();

        mainButton = (ImageView)findViewById(R.id.mainButton);
        themeButton = (ImageView)findViewById(R.id.themeButton);
        customizeButton = (ImageView)findViewById(R.id.customizeButton);
        shareButton = (ImageView)findViewById(R.id.shareButton);
        userButton = (ImageView)findViewById(R.id.userButton);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 1;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(new FragmentColorPicker());
            }
        });
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 2;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(new FragmentTheme());
            }
        });
        customizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 3;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(new FragmentCustomize());
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 4;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(new FragmentShare());
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 5;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(new FragmentUser());
            }
        });

    }

    private void Button_Image_Change(int current_Fragment_Index, int select_Fragment_Index){
        switch (current_Fragment_Index){
            case 1:
                mainButton.setImageResource(R.drawable.icon01non);
                break;
            case 2:
                themeButton.setImageResource(R.drawable.icon01non);
                break;
            case 3:
                customizeButton.setImageResource(R.drawable.icon01non);
                break;
            case 4:
                shareButton.setImageResource(R.drawable.icon01non);
                break;
            case 5:
                shareButton.setImageResource(R.drawable.icon01non);
                break;
        }
        switch (select_Fragment_Index){
            case 1:
                mainButton.setImageResource(R.drawable.icon01non);
                break;
            case 2:
                themeButton.setImageResource(R.drawable.icon01non);
                break;
            case 3:
                customizeButton.setImageResource(R.drawable.icon01non);
                break;
            case 4:
                shareButton.setImageResource(R.drawable.icon01non);
                break;
            case 5:
                shareButton.setImageResource(R.drawable.icon01non);
                break;
        }
    }



    private void Fragment_Change(Fragment changeFragment){
        fragmentTransaction = fragmentManager.beginTransaction();

        if(Current_Fragment_Index > Select_Fragment_Index){
            fragmentTransaction.setCustomAnimations(R.anim.fromleft, R.anim.toright);
        }else if(Current_Fragment_Index < Select_Fragment_Index){
            fragmentTransaction.setCustomAnimations(R.anim.fromright, R.anim.toleft);
        }
        Current_Fragment_Index = Select_Fragment_Index;
        fragmentTransaction.replace(R.id.content, changeFragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
