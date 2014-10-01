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
					deviceName = msg.obj.toString();
					break;
				case BluetoothViewerService.MSG_CONNECTING:
					connected = false;
					btnledeinschalten.setText("Verbindung wird aufgebaut");
					break;
				case BluetoothViewerService.MSG_NOT_CONNECTED:
					connected = false;
					btnledeinschalten.setText("LED einschalten");
					break;
				case BluetoothViewerService.MSG_CONNECTION_FAILED:
					connected = false;
					btnledeinschalten.setText("LED einschalten");
					break;
				case BluetoothViewerService.MSG_CONNECTION_LOST:
					connected = false;
					btnledeinschalten.setText("LED einschalten");
					break;
				case BluetoothViewerService.MSG_BYTES_WRITTEN:
					String written = new String((byte[]) msg.obj);
					Log.i(TAG, "written = '" + written + "'");
					break;
				case BluetoothViewerService.MSG_LINE_READ:
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
					}
					catch(Exception e)
					{
						Log.e("error", "ManuelL_Fragment: Verbindung nicht aufbaubar");
					}
				}
				else
				{					
					mBluetoothService.stop();
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
//        new Thread(){
//        	public void run(){
//        		try {
//					Thread.sleep(6000);
//				} catch (InterruptedException e) {}
//				Log.d(TAG, "update colors");
//        		updateAllColors();
//        	}
//        }.start();
        
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
		if(connected){
			
			String h = "#red_"+red+";";
			mBluetoothService.write(h.getBytes());
		}
		else
			Toast.makeText(Main_Activity,"nicht verbunden -> senden nicht möglich",
	                 Toast.LENGTH_LONG).show();      
	}
	
	private void updateGreen(){
		if(connected){
			
			String h = "#green_"+green+";";
			mBluetoothService.write(h.getBytes());
		}
		else
			Toast.makeText(Main_Activity,"nicht verbunden -> senden nicht möglich",
	                 Toast.LENGTH_LONG).show();      
	}
	
	private void updateBlue(){
		if(connected){
			
			String h = "#blue_"+blue+";";
			mBluetoothService.write(h.getBytes());
		}
		else
			Toast.makeText(Main_Activity,"nicht verbunden -> senden nicht möglich",
	                 Toast.LENGTH_LONG).show();      
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
