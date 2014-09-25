package com.example.ledstrip_arduino;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
//import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.preference.PreferenceManager;


import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Manuell_Fragment extends Fragment implements OnSeekBarChangeListener{
	
	private MainActivity Main_Activity;
	public void setActivity(MainActivity a)	{Main_Activity=a;}
	
	private static final String TAG = "MultiColorLamp";	
	
	// change this to your Bluetooth device address 
	private static final String DEVICE_ADDRESS =  "00:14:02:26:01:10";
	
	private OutputStream outputStream;
	private InputStream inStream;
	private boolean connected;	
	
	private String deviceName;
	
	private BluetoothAdapter blueAdapter;
	private BluetoothViewerService mBluetoothService;

	public static final String EXTRA_MESSAGE = null;

	protected static final Menu devices = null;

	List list = null;
	
	final int DELAY = 150;
	SeekBar redSB;
	SeekBar greenSB;
	SeekBar blueSB;
	View colorIndicator;
	Button btnledeinschalten;
	
	int red, green, blue;
	long lastChange;
	public Manuell_Fragment(){
		
	}
	// The Handler that gets information back from the BluetoothService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case BluetoothViewerService.MSG_CONNECTED:
					btnledeinschalten.setText("LED ausschalten");
					connected = true;
//					mStatusView.setText(formatStatusMessage(R.string.btstatus_connected_to_fmt, msg.obj));
//					onBluetoothStateChanged();
//					recording.setLength(0);
					deviceName = msg.obj.toString();
					break;
				case BluetoothViewerService.MSG_CONNECTING:
					connected = false;
					//mStatusView.setText(formatStatusMessage(R.string.btstatus_connecting_to_fmt, msg.obj));
					//onBluetoothStateChanged();
					break;
				case BluetoothViewerService.MSG_NOT_CONNECTED:
					connected = false;
					btnledeinschalten.setText("LED einschalten");
					//mStatusView.setText(R.string.btstatus_not_connected);
					//onBluetoothStateChanged();
					break;
				case BluetoothViewerService.MSG_CONNECTION_FAILED:
					connected = false;
					//mStatusView.setText(R.string.btstatus_not_connected);
					//onBluetoothStateChanged();
					break;
				case BluetoothViewerService.MSG_CONNECTION_LOST:
					connected = false;
					//mStatusView.setText(R.string.btstatus_not_connected);
					//onBluetoothStateChanged();
					break;
				case BluetoothViewerService.MSG_BYTES_WRITTEN:
					String written = new String((byte[]) msg.obj);
					//mConversationArrayAdapter.add(">>> " + written);
					Log.i(TAG, "written = '" + written + "'");
					break;
				case BluetoothViewerService.MSG_LINE_READ:
					//if (paused) break;
//					String line = (String) msg.obj;
//					if (D) Log.d(TAG, line);
//					mConversationArrayAdapter.add(line);
//					if (recordingEnabled) {
//						recording.append(line).append("\n");
//					}
					break;
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	}
	
    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState)
    {
    View rootView = inflater.inflate(R.layout.manuell_fragment,
    container, false);

		//Amarino.connect(Main_Activity, DEVICE_ADDRESS);
        
        // get references to views defined in our main.xml layout file
    
    	
    	blueAdapter=BluetoothAdapter.getDefaultAdapter();
        redSB = (SeekBar) rootView.findViewById(R.id.SeekBarRed);
        greenSB = (SeekBar) rootView.findViewById(R.id.SeekBarGreen);
        blueSB = (SeekBar) rootView.findViewById(R.id.SeekBarBlue);
        colorIndicator = rootView.findViewById(R.id.ColorIndicator);
        btnledeinschalten=(Button)rootView.findViewById(R.id.button_LED_einschalten);
        btnledeinschalten.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
//				blueAdapter.startDiscovery();
//				//Main_Activity.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//				Main_Activity.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
//				Main_Activity.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
//				Toast.makeText(Main_Activity,"Device: "+Main_Activity.mac,
//		                 Toast.LENGTH_LONG).show();
				if(!connected)
				{
					try{
						//init();
						BluetoothDevice device = blueAdapter.getRemoteDevice(((MainActivity)Main_Activity).mac);
						try{
						mBluetoothService.connect(device);
						
						}
						catch(Exception e){
							Log.e("error", "ConnectTread: "+e.getMessage());
						}
						
						if(connected)//ist hier zu schnell da, evtl zweite taste oder etwas warten oder senden über seekbarlistener
						{
						
						String h = "H";
						mBluetoothService.write(h.getBytes());
						//write("H");
						}
						
					}
					catch(Exception e)
					{
						Log.e("error", "ManuelL_Fragment: Verbindung nicht aufbaubar");
					}
				}
				else
				{
					
					connected=false;
				}
				
			}
        	
        });

        // register listeners
        redSB.setOnSeekBarChangeListener(this);
        greenSB.setOnSeekBarChangeListener(this);
        blueSB.setOnSeekBarChangeListener(this);
        
        return rootView;
    }
    
    

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.Main_Activity = (MainActivity) activity;
	}
	@Override
	public void onResume(){
		//RefreshData();
		super.onResume();
	}
    
	@Override
	public void onStart() {
		super.onStart();

		// load last state
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Main_Activity);
        red = prefs.getInt("red_m", 0);
        green = prefs.getInt("green_m", 0);
        blue = prefs.getInt("blue_m", 0);
        
        // set seekbars and feedback color according to last state
        redSB.setProgress(red);
        greenSB.setProgress(green);
        blueSB.setProgress(blue);
        colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
        new Thread(){
        	public void run(){
        		try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {}
				Log.d(TAG, "update colors");
        		updateAllColors();
        	}
        }.start();
        
        if(mBluetoothService==null){
        	mBluetoothService = new BluetoothViewerService(mHandler);
        }
        
        
	}

	@Override
	public void onStop() {
		super.onStop();
		// save state
		PreferenceManager.getDefaultSharedPreferences(Main_Activity)
			.edit()
				.putInt("red_m", red)
				.putInt("green_m", green)
				.putInt("blue_m", blue)
			.commit();
		
		// stop Amarino's background service, we don't need it any more 
		//Amarino.disconnect(Main_Activity, DEVICE_ADDRESS);
	}



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// do not send to many updates, Arduino can't handle so much
		if (System.currentTimeMillis() - lastChange > DELAY ){
			updateState(seekBar);
			lastChange = System.currentTimeMillis();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		lastChange = System.currentTimeMillis();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		updateState(seekBar);
	}
	
//	private void init(){
//		verbunden=false;
//		  BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
//		  if (blueAdapter != null) {
//		      if (blueAdapter.isEnabled()) {
////		          Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
////		
////		          if(bondedDevices.size() > 0){
////		              BluetoothDevice[] devices = (BluetoothDevice[]) bondedDevices.toArray();
////		          }
//		    	  try{
//		    		  if(((MainActivity)Main_Activity).mac!="")
//		    		  {
//			              BluetoothDevice device = blueAdapter.getRemoteDevice(((MainActivity)Main_Activity).mac);
//			              //ParcelUuid[] uuids = device.getUuids();
//			              
//			              BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
//			              socket.connect();
//			              outputStream = socket.getOutputStream();
//			              inStream = socket.getInputStream();
//			              verbunden=true;
//		    		  }
//		    		  else
//		    			  Toast.makeText(Main_Activity,"Kein Device ausgewählt",
//					                 Toast.LENGTH_LONG).show();
//		    	  }
//		    	  catch(Exception e)
//		    	  {
//		    		  Toast.makeText(Main_Activity, "Funktion init hat Fehler geworfen: "+e.getMessage(),
//				                 Toast.LENGTH_LONG).show();
//		    		  Log.e("error", "Funktion init hat Fehler geworfen: "+e.getMessage());
//		    	  }
//		         // }
//		
//		          //Log.e("error", "No appropriate paired devices.");
//		      }
//		      else
//		      {
//		    	  Toast.makeText(Main_Activity,"Bluetooth is disabled",
//			                 Toast.LENGTH_LONG).show();
//		          Log.e("error", "Bluetooth is disabled.");
//		      }
//		  }
//	}
	

//	public void write(String s){
//	    try {
//			outputStream.write(s.getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Log.e("error", "Funktion write hat Fehler geworfen: "+e.getMessage());
//		}
//	}
//	

	private void updateState(final SeekBar seekBar) {
		
		switch (seekBar.getId()){
			case R.id.SeekBarRed:
				red = seekBar.getProgress();
				updateRed();
				break;
			case R.id.SeekBarGreen:
				green = seekBar.getProgress();
				updateGreen();
				break;
			case R.id.SeekBarBlue:
				blue = seekBar.getProgress();
				updateBlue();
				break;
		}
		// provide user feedback
		colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
	}
	
	private void updateAllColors() {
		// send state to Arduino
        updateRed();
        updateGreen();
        updateBlue();
	}
	
	private void updateRed(){
		// I have chosen random small letters for the flag 'o' for red, 'p' for green and 'q' for blue
		// you could select any small letter you want
		// however be sure to match the character you register a function for your in Arduino sketch
		//Amarino.sendDataToArduino(Main_Activity, DEVICE_ADDRESS, 'o', red);
	}
	
	private void updateGreen(){
		//Amarino.sendDataToArduino(Main_Activity, DEVICE_ADDRESS, 'p', green);
	}
	
	private void updateBlue(){
		//Amarino.sendDataToArduino(Main_Activity, DEVICE_ADDRESS, 'q', blue);
	}
	
	public static String getVersion(Context context) {
		String version = "1.0"; 
		try { 
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0); 
		    version = pi.versionName; 
		} catch (PackageManager.NameNotFoundException e) { 
		    Log.e(TAG, "Package name not found", e); 
		} 
		return version;
	}
	
}
