package com.example.ledstrip_arduino;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
//import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
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
	
	private Activity Main_Activity;
	public void setActivity(Activity a)	{Main_Activity=a;}
	
	private static final String TAG = "MultiColorLamp";	
	
	// change this to your Bluetooth device address 
	private static final String DEVICE_ADDRESS =  "00:14:02:26:01:10";
	
	private OutputStream outputStream;
	private InputStream inStream;
	private boolean verbunden;	
	//testkommentar

	public static final String EXTRA_MESSAGE = null;
	
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
        redSB = (SeekBar) rootView.findViewById(R.id.SeekBarRed);
        greenSB = (SeekBar) rootView.findViewById(R.id.SeekBarGreen);
        blueSB = (SeekBar) rootView.findViewById(R.id.SeekBarBlue);
        colorIndicator = rootView.findViewById(R.id.ColorIndicator);
        btnledeinschalten=(Button)rootView.findViewById(R.id.button_LED_einschalten);
        btnledeinschalten.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!verbunden)
				{
					try{
						init();
						btnledeinschalten.setText("LED ausschalten");
						verbunden=false;
					}
					catch(Exception e)
					{
						Log.e("error", "ManuelL_Fragment: Verbindung nicht aufbaubar");
					}
				}
				else
				{
					btnledeinschalten.setText("LED einschalten");
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
		this.Main_Activity = activity;
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
	
	private void init(){
		verbunden=false;
		  BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		  if (blueAdapter != null) {
		      if (blueAdapter.isEnabled()) {
		          //Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
		
		         // if(bondedDevices.size() > 0){
		              //BluetoothDevice[] devices = (BluetoothDevice[]) bondedDevices.toArray();
		    	  try{
		    		  if(((MainActivity)Main_Activity).mac!="")
		    		  {
			              BluetoothDevice device = blueAdapter.getRemoteDevice(((MainActivity)Main_Activity).mac);
			              ParcelUuid[] uuids = device.getUuids();
			              BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
			              socket.connect();
			              outputStream = socket.getOutputStream();
			              inStream = socket.getInputStream();
			              verbunden=true;
		    		  }
		    		  else
		    			  Toast.makeText(Main_Activity,"Kein Device ausgewählt",
					                 Toast.LENGTH_LONG).show();
		    	  }
		    	  catch(Exception e)
		    	  {
		    		  Log.e("error", "Funktion init hat Fehler geworfen: "+e.getMessage());
		    	  }
		         // }
		
		          //Log.e("error", "No appropriate paired devices.");
		      }
		      else
		      {
		    	  Toast.makeText(Main_Activity,"Bluetooth is disabled",
			                 Toast.LENGTH_LONG).show();
		          Log.e("error", "Bluetooth is disabled.");
		      }
		  }
	}
	
	
	public void write(String s){
	    try {
			outputStream.write(s.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("error", "Funktion write hat Fehler geworfen: "+e.getMessage());
		}
	}
	

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
