package com.example.vclab.moodlightshare;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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

import com.example.vclab.moodlightshare.Bluno.BLUNOActivity;
import com.example.vclab.moodlightshare.Bluno.BleCmd;
import com.example.vclab.moodlightshare.Bluno.BlunoLibrary;
import com.example.vclab.moodlightshare.Bluno.PlainProtocol;
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

    BLUNOActivity blunoActivity;
    BlunoLibrary blunoLibrary;

    private static final int REQUEST_ENABLE_BT = 3;


    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;


    //bluno instance

    private boolean isColorChange = false;

    private PlainProtocol mPlainProtocol = new PlainProtocol();

    public static final int LEDMode = 0;
    public static final int RockerMode = 1;
    public static final int KnobMode = 2;
    private byte Modestates = LEDMode;

    private static Handler receivedHandler = new Handler();
    private Runnable PotentiometerRunnable = new Runnable() {
        @Override
        public void run() {
            if (Modestates == KnobMode) {

                blunoLibrary.serialSend(mPlainProtocol.write(BleCmd.Knob));
                System.out.println("update BleCmdReadPotentiometer");
            }
            receivedHandler.postDelayed(PotentiometerRunnable, 50);
        }
    };


    private boolean isLastSwitchOn = false;
    private Runnable colorRunnable = new Runnable() {

        @Override
        public void run() {
            if (Modestates == LEDMode) {
                if (true) {
                    if (isColorChange || (isLastSwitchOn == false)) {
                        blunoLibrary.serialSend(mPlainProtocol.write(BleCmd.RGBLed,0,255,0));
                    }
                    isColorChange = false;
                    isLastSwitchOn = true;
                } else {
                    if (isLastSwitchOn) {
                        blunoLibrary.serialSend(mPlainProtocol.write(BleCmd.RGBLed, 0, 0, 0));
                    }
                    isLastSwitchOn = false;
                }
            }
//			System.out.println("update color");
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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        blunoActivity = new BLUNOActivity();
        blunoLibrary = new BlunoLibrary() {
            @Override
            public void onConectionStateChange(connectionStateEnum theconnectionStateEnum) {
                mConnectionState = theconnectionStateEnum;
                switch (mConnectionState) {
                    case isScanning:
                        break;
                    case isConnected:
                        switch (Modestates) {
                            case LEDMode:
                                receivedHandler.post(colorRunnable);
                                break;
                            case RockerMode:
                                break;
                            case KnobMode:
                                receivedHandler.post(PotentiometerRunnable);
                                break;
                            default:
                                break;
                        }
                        break;
                    case isConnecting:
                        break;
                    case isToScan:
                        receivedHandler.removeCallbacks(colorRunnable);
                        receivedHandler.removeCallbacks(PotentiometerRunnable);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSerialReceived(String theString) {
                System.out.println("displayData " + theString);

                mPlainProtocol.mReceivedframe.append(theString);
                System.out.print("mPlainProtocol.mReceivedframe:");
                System.out.println(mPlainProtocol.mReceivedframe.toString());

                while (mPlainProtocol.available()) {
                    System.out.print("receivedCommand:");
                    System.out.println(mPlainProtocol.receivedCommand);

                    if (mPlainProtocol.receivedCommand.equals(BleCmd.Rocker)) {
                        if (Modestates == RockerMode) {
                            System.out.println("received Rocker");
                        }
                    } else if (mPlainProtocol.receivedCommand.equals(BleCmd.Knob)) {
                        System.out.println("received Knob");
                        float pgPos = mPlainProtocol.receivedContent[0] / 3.75f;//Adjust display value to the angle
                    }
                }
            }


        };

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
        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.secure_connect_scan) {
            //startActivity(new Intent(this, SettingsActivity.class));

            //blunoLibrary.mScanDeviceDialog.show();
            AlertDialog mUserListDialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select User");
            mUserListDialog = builder.create();
            mUserListDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
