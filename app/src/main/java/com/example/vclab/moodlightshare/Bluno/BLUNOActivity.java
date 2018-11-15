package com.example.vclab.moodlightshare.Bluno;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class BLUNOActivity extends AppCompatActivity{
	
    public final static String TAG = DeviceControlActivity.class.getSimpleName();

	public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};

	private connectionStateEnum mConnectionState = connectionStateEnum.isNull;

	private PlainProtocol mPlainProtocol= new PlainProtocol();

	private int mBaudrate=115200;	//set the default baud rate to 115200
	private String mPassword="AT+PASSWOR=DFRobot\r\n";

	public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
	public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
	public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";

	private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";

	public void serialSend(String theString){
		if (mConnectionState == connectionStateEnum.isConnected) {
			mSCharacteristic.setValue(theString);
			mBluetoothLeService.writeCharacteristic(mSCharacteristic);
		}
	}

	public void serialBegin(int baud){
		mBaudrate=baud;
		mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
	}


	private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
	BluetoothLeService mBluetoothLeService;

	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
			new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

	private boolean isColorChange=false;

	private ImageView ledImage = null;
	private ImageView joystickImage = null;
	private ImageView PotentiometerImage = null;
	private ImageView arduinoinputdispArea = null;
	private EditText oledSubmitEditText = null;

	private ImageView oledSubmitButton,oledClearButton;


    
    public static final int LEDMode=0;
    public static final int RockerMode=1;
    public static final int KnobMode=2;
    private byte Modestates = LEDMode;

    public BLUNOActivity(){
		Log.e("BLUNOActivity Creator","serial communication start");

		//set the Baudrate of the Serial port
		serialBegin(115200);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		setContentView(R.layout.activity_bluno);



	}


	private static Handler receivedHandler = new Handler();
	private Runnable PotentiometerRunnable = new Runnable() {
		@Override
		public void run() {
			if(Modestates == KnobMode)
			{

				serialSend(mPlainProtocol.write(BleCmd.Knob));
				System.out.println("update BleCmdReadPotentiometer");
			}
			receivedHandler.postDelayed(PotentiometerRunnable, 50);
		}
	};


	private boolean isLastSwitchOn=false;
	private Runnable colorRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(Modestates == LEDMode)
			{
				//if(picker.mIsSwitchOn)
				if(true)
				{
					serialSend(mPlainProtocol.write(BleCmd.RGBLed, 000,000,000));
				}else{
					if(isLastSwitchOn)
					{
						serialSend(mPlainProtocol.write(BleCmd.RGBLed,0,0,0));
					}
					isLastSwitchOn=false;
				}
			}

			receivedHandler.postDelayed(colorRunnable, 50);
		}
	};
	

	
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

            	}
    		}

    	}
		
	}

	private void getGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		String uuid = null;
		mModelNumberCharacteristic=null;
		mSerialPortCharacteristic=null;
		mCommandCharacteristic=null;
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			uuid = gattService.getUuid().toString();
			System.out.println("displayGattServices + uuid="+uuid);

			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas =
					new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				uuid = gattCharacteristic.getUuid().toString();
				if(uuid.equals(ModelNumberStringUUID)){
					mModelNumberCharacteristic=gattCharacteristic;
					System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
				}
				else if(uuid.equals(SerialPortUUID)){
					mSerialPortCharacteristic = gattCharacteristic;
					System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
				}
				else if(uuid.equals(CommandUUID)){
					mCommandCharacteristic = gattCharacteristic;
					System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
				}
			}
			mGattCharacteristics.add(charas);
		}

		if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
			Toast.makeText(this, "Please select DFRobot devices", Toast.LENGTH_SHORT).show();
			mConnectionState = connectionStateEnum.isToScan;
			//onConectionStateChange(mConnectionState);
		}
		else {
			mSCharacteristic=mModelNumberCharacteristic;
			mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
			mBluetoothLeService.readCharacteristic(mSCharacteristic);
		}

	}

	
	

	

	
	
}
