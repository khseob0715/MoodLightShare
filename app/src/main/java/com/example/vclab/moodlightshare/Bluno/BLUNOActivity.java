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

public class BLUNOActivity extends BlunoLibrary {

	public final static String TAG = DeviceControlActivity.class.getSimpleName();

	private connectionStateEnum mConnectionState = connectionStateEnum.isNull;
	private PlainProtocol mPlainProtocol = new PlainProtocol();
	private Typeface txtotf;
	private boolean isColorChange = false;
	private ImageView titleImageView;
	private ImageView ledImage = null;
	private ImageView joystickImage = null;
	private ImageView PotentiometerImage = null;
	private ImageView arduinoinputdispArea = null;
	private EditText oledSubmitEditText = null;
	private ImageView oledSubmitButton, oledClearButton;
	private TextView analogTextDisp;
	private TextView txtTemp;
	private TextView txtHumidity;

	public static final int LEDMode = 0;
	public static final int RockerMode = 1;
	public static final int KnobMode = 2;
	private byte Modestates = LEDMode;

	private static Handler receivedHandler = new Handler();
	private Runnable PotentiometerRunnable = new Runnable() {
		@Override
		public void run() {
			if (Modestates == KnobMode) {

				serialSend(mPlainProtocol.write(BleCmd.Knob));
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
						serialSend(mPlainProtocol.write(BleCmd.RGBLed,0,255,0));
					}
					isColorChange = false;
					isLastSwitchOn = true;
				} else {
					if (isLastSwitchOn) {
						serialSend(mPlainProtocol.write(BleCmd.RGBLed, 0, 0, 0));
					}
					isLastSwitchOn = false;
				}
			}
//			System.out.println("update color");
			receivedHandler.postDelayed(colorRunnable, 50);
		}
	};


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

					switch (mPlainProtocol.receivedContent[0]) {
						case 0:    //None input

							break;
						case 1:    //center button pressed

							break;
						case 2:    //up button pressed

							break;
						case 3:    //left button pressed

							break;
						case 4:    //down button pressed

							break;
						case 5:    //right button pressed

							break;
						default:
							Log.e(getLocalClassName(), "Unkown joystick state: " + mPlainProtocol.receivedContent[0]);
							break;
					}
				}
			} else if (mPlainProtocol.receivedCommand.equals(BleCmd.Knob)) {
				System.out.println("received Knob");
				float pgPos = mPlainProtocol.receivedContent[0] / 3.75f;//Adjust display value to the angle

				analogTextDisp.setText(String.valueOf(mPlainProtocol.receivedContent[0]));
			}
		}

	}





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("BLUNOActivity onCreate");
		//setContentView(R.layout.activity_bluno);

		//set the Baudrate of the Serial port
		serialBegin(115200);

		onCreateProcess();
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();
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
		receivedHandler.removeCallbacks(PotentiometerRunnable);
		onPauseProcess();
	}

	protected void onStop() {
		super.onStop();
		onStopProcess();
		System.out.println("MiUnoActivity onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("MiUnoActivity onDestroy");
		onDestroyProcess();

	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {

		mConnectionState = theConnectionState;
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
}