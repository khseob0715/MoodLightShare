package com.example.vclab.moodlightshare;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.vclab.moodlightshare.Fragment.FragmentColorPicker;
import com.example.vclab.moodlightshare.Fragment.FragmentCustomize;
import com.example.vclab.moodlightshare.Fragment.FragmentShare;
import com.example.vclab.moodlightshare.Fragment.FragmentTheme;
import com.example.vclab.moodlightshare.Fragment.FragmentUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView mainButton, themeButton, customizeButton, shareButton, userButton;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    int Current_Fragment_Index = 1;
    int Select_Fragment_Index;


    public static Uri profileUri;
    StorageReference mStoragedRef;

    String UserUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_frame_activity);

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



        UserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 처음 넘어오는 데이터 // ArrayList 값.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("uid").getValue(String.class).equals(UserUid)) {
                        Uri url = Uri.parse(snapshot.child("profileImageUrl").getValue(String.class).toString());
                        profileUri = url;
                        break;
                    }else{
                        Log.e("Tag","ssss");
                    }
                    profileUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/moodlightshare.appspot.com/o/images%2Ficon01non.png?alt=media&token=db74f55c-ef39-482f-84c0-63cb8ef81496");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Button_Image_Change(int current_Fragment_Index, int select_Fragment_Index){
        switch (current_Fragment_Index){
            case 1:
                mainButton.setImageResource(R.drawable.homeicon);
                break;
            case 2:
                themeButton.setImageResource(R.drawable.themeicon);
                break;
            case 3:
                customizeButton.setImageResource(R.drawable.customicon);
                break;
            case 4:
                shareButton.setImageResource(R.drawable.shareicon);
                break;
            case 5:
                userButton.setImageResource(R.drawable.usericon);
                break;
        }
        switch (select_Fragment_Index){
            case 1:
                mainButton.setImageResource(R.drawable.homeicon_on);
                break;
            case 2:
                themeButton.setImageResource(R.drawable.themeicon_on);
                break;
            case 3:
                customizeButton.setImageResource(R.drawable.customicon_on);
                break;
            case 4:
                shareButton.setImageResource(R.drawable.shareicon_on);
                break;
            case 5:
                userButton.setImageResource(R.drawable.usericon_on);
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
