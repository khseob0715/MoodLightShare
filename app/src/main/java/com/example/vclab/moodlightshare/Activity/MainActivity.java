package com.example.vclab.moodlightshare.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.vclab.moodlightshare.Bluno.BleCmd;
import com.example.vclab.moodlightshare.Bluno.BluetoothLeService;
import com.example.vclab.moodlightshare.Bluno.BlunoLibrary;
import com.example.vclab.moodlightshare.Bluno.PlainProtocol;
import com.example.vclab.moodlightshare.Fragment.FragmentColorPicker;
import com.example.vclab.moodlightshare.Fragment.FragmentCustomize;
import com.example.vclab.moodlightshare.Fragment.FragmentShare;
import com.example.vclab.moodlightshare.Fragment.FragmentTheme;
import com.example.vclab.moodlightshare.Fragment.FragmentUser;
import com.example.vclab.moodlightshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BlunoLibrary{

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1001;
    ImageView mainButton, themeButton, customizeButton, shareButton, userButton;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    int Current_Fragment_Index = 1;
    int Select_Fragment_Index;

    Fragment Fragment_picker, Fragment_theme, Fragment_share, Fragment_user, Fragment_custom;

    public static Uri profileUri;

    String UserUid;

    private boolean log_print = true;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothLeService mChatService = null;

    //bluno instance

    public static boolean isColorChange = false;
    public static boolean isLastSwitchOn = false;
    public static boolean isMusicOn = true;
    public static boolean isSleepOn = false;
    public static boolean isSpeechOn = false;

    private boolean oneTimeCall = true;
    private boolean LightOneTime = true;

    private PlainProtocol mPlainProtocol = new PlainProtocol();

    public static final int LEDMode = 0;
    public static final int RockerMode = 1;
    public static final int Theme = 2;
    public static final int Custom = 3;
    public static final int Sleep = 4;
    public static final int Speech = 5;

    public static int Modestates = RockerMode;

    public static int color_r = 255, color_b = 255, color_g = 255;

    private static Handler receivedHandler = new Handler();

    private Runnable colorRunnable = new Runnable() {

        @Override
        public void run() {
            Log.e("Modestates","Modestates : " + Modestates);
            switch (Modestates) {
                case LEDMode:
                    Log.e("Modestates","LEDMode");
                    if(isLastSwitchOn){      // 전원이 켜지면, LED가 들어옴
                        Log.e("TAG","isLastSwitchOn");

                        if(isColorChange){   // 색상을 선택하면 그 색이 나옴.
                            Log.e("TAG","isColorChange");
                            serialSend(mPlainProtocol.write(BleCmd.RGBLed, color_r, color_g, color_b));
                            isColorChange = false;
                        }else{
                            // serialSend(mPlainProtocol.write(BleCmd.RGBLed, 125, 125, 125));
                        }
                    }else{
                        // 전원 버튼을 누르면 전원이 꺼짐.
                        serialSend(mPlainProtocol.write(BleCmd.RGBLed, 0, 0, 0));
                    }
                    break;
                case RockerMode:
                    Log.e("Modestates","RockerMode");
                    if(isMusicOn){
                        serialSend(mPlainProtocol.write(BleCmd.Rocker));
                    }else{
                        serialSend(mPlainProtocol.write(BleCmd.RGBLed, 0, 0, 0));
                    }
                    break;
                case Theme:
                    Log.e("Modestates","ThemeMode");
                    serialSend(mPlainProtocol.write(BleCmd.Theme,FragmentTheme.selected_theme));
                    break;
                case Custom:
                    Log.e("Modestates","CustomMode");
                    break;
                case Sleep:
                    Log.e("Modestates","SleepMode");
                    serialSend(mPlainProtocol.write(BleCmd.Sleep));
                    break;
                case Speech:
                    Log.e("Modestates","SpeechMode");
                    serialSend(mPlainProtocol.write(BleCmd.Speech, FragmentColorPicker.SPEECH_ON));
                    break;
                default:
                    break;
            }
            receivedHandler.postDelayed(colorRunnable, 50);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.  // 블루투스 권한 허용
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_frame_activity);

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+ Permission APIs
            MarshMallow();
        }
        Fragment_picker = new FragmentColorPicker();
        Fragment_theme = new FragmentTheme();
        Fragment_share = new FragmentShare();
        Fragment_custom = new FragmentCustomize();
        Fragment_user = new FragmentUser();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        serialBegin(115200);

        onCreateProcess();

        // fragment를 불러오는 소스코드.
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, Fragment_picker);
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
                Fragment_Change(Fragment_picker);
            }
        });
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 2;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(Fragment_theme);
            }
        });
        customizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 3;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(Fragment_custom);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 4;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(Fragment_share);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select_Fragment_Index = 5;
                Button_Image_Change(Current_Fragment_Index,Select_Fragment_Index);
                Fragment_Change(Fragment_user);
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
                        Log.e("MainActivity","have not data");
                    }
                    // 아래는 기본 프로필 이미지,
                    profileUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/moodlightshare.appspot.com/o/images%2Ficon01non.png?alt=media&token=db74f55c-ef39-482f-84c0-63cb8ef81496");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void colorChange(){
        isColorChange = true;
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
        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.secure_connect_scan) {

            buttonScanOnClickProcess();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {

        mConnectionState=theConnectionState;

        switch (mConnectionState) {
            case isScanning:
                break;
            case isConnected:
                receivedHandler.post(colorRunnable);
                break;
            case isConnecting:
                Toast.makeText(this, "무드등과 블루투스 연결이 완료되었습니다", Toast.LENGTH_SHORT).show();
                break;
            case isToScan:
              //  Toast.makeText(this, "isToScan", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {
        System.out.println("displayData "+theString);

        mPlainProtocol.mReceivedframe.append(theString) ;
        System.out.print("mPlainProtocol.mReceivedframe:");
        System.out.println(mPlainProtocol.mReceivedframe.toString());

        while(mPlainProtocol.available())
        {
            System.out.print("receivedCommand:");
            System.out.println(mPlainProtocol.receivedCommand);

            if(mPlainProtocol.receivedCommand.equals(BleCmd.Rocker))
            {
                if(Modestates == RockerMode)
                {
                    System.out.println("received Rocker");

                    Log.e(getLocalClassName(), "Unkown joystick state: " + mPlainProtocol.receivedContent[0]);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);


                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        ) {
                    // All Permissions Granted

                    // Permission Denied
                    Toast.makeText(this, "All Permission GRANTED !! Thank You :)", Toast.LENGTH_SHORT)
                            .show();


                } else {
                    // Permission Denied
                    Toast.makeText(this, "One or More Permissions are DENIED Exiting App :(", Toast.LENGTH_SHORT)
                            .show();

                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void MarshMallow() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {

                // Need Rationale
                String message = "App need access to " + permissionsNeeded.get(0);

                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        onActivityResultProcess(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("BLUNOActivity onPause");
        receivedHandler.removeCallbacks(colorRunnable);
        onPauseProcess();
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();
    }


}
