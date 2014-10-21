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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Manuell_Fragment extends Fragment implements OnSeekBarChangeListener{
	
	private MainActivity Main_Activity;
//	public void setActivity(MainActivity a)	{Main_Activity=a;}
//	
	private static final String TAG = "Manuell_Fragment";	
	public static final String EXTRA_MESSAGE = null;
	private static final String MANUELL_ACTIVE = "m_a";

	//protected static final Menu devices = null;

	//List list = null;
	
	final int DELAY = 150;
	private SeekBar redSB;
	private SeekBar greenSB;
	private SeekBar blueSB;
	private View colorIndicator;
	private Button btn_manuell_onoff;
	private TextView tv_manuell_bluetoothstatus;
	
	private Intent intentactive = new Intent();
	
	int red, green, blue;
	long lastChange;
	private boolean manuell_onoff = false;
	public Manuell_Fragment(){}
	
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
	    
	    intentactive.setAction(Main_Activity.BROADCASTACTION);
    	intentactive.putExtra("extra", Main_Activity.BROADCASTEXTRA_ACTIVE);

    	
        redSB = (SeekBar) rootView.findViewById(R.id.SeekBarRed);
        greenSB = (SeekBar) rootView.findViewById(R.id.SeekBarGreen);
        blueSB = (SeekBar) rootView.findViewById(R.id.SeekBarBlue);
        colorIndicator = rootView.findViewById(R.id.ColorIndicator);
        tv_manuell_bluetoothstatus = (TextView) rootView.findViewById(R.id.textview_manuell_bluetoothstatus);
        btn_manuell_onoff=(Button)rootView.findViewById(R.id.button_manuell_onoff);
        btn_manuell_onoff.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {	
				if(!manuell_onoff)
					Main_Activity.active="manuell";
				else
					Main_Activity.active="";
				
				Main_Activity.sendBroadcast(intentactive);	
			}        	
        });

        // register listeners
        redSB.setOnSeekBarChangeListener(this);
        greenSB.setOnSeekBarChangeListener(this);
        blueSB.setOnSeekBarChangeListener(this);
        
        Main_Activity.registerReceiver(bReceiver, new IntentFilter(Main_Activity.BROADCASTACTION)); 
       
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
	
	 final BroadcastReceiver bReceiver = new BroadcastReceiver() {
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            String extra = intent.getStringExtra("extra");
	            if(action.startsWith(Main_Activity.BROADCASTACTION)){
			            if(extra.startsWith(Main_Activity.BROADCASTEXTRA_ACTIVE)){
			            	if(Main_Activity.active=="manuell"){
			            		manuell_onoff=true;
			            		btn_manuell_onoff.setText("manuelle Steuerung deaktivieren");
			            	}
			            	else{
			            		manuell_onoff=false;
			            		btn_manuell_onoff.setText("manuelle Steuerung aktivieren");
			            	}
			            }
			            else if(extra.startsWith(Main_Activity.BROADCASTEXTRA_BTSTATUS)){
			            	tv_manuell_bluetoothstatus.setText(Main_Activity.bluetoothstatus);
			            }
			        }
	        }
    };
    
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
		if(manuell_onoff){
			if(Main_Activity.connected){
				
				String h = "#red_"+red+";";
				Main_Activity.mBluetoothService.write(h.getBytes());
			}
			else
				Toast.makeText(Main_Activity,"nicht verbunden -> senden nicht möglich",
		                 Toast.LENGTH_LONG).show();     
		}
		else
			Toast.makeText(Main_Activity,"manuelle Steuerung ist deaktiviert",
	                 Toast.LENGTH_LONG).show(); 
	}
	
	private void updateGreen(){
		if(manuell_onoff){
			if(Main_Activity.connected){
				
				String h = "#green_"+green+";";
				Main_Activity.mBluetoothService.write(h.getBytes());
			}
			else
				Toast.makeText(Main_Activity,"nicht verbunden -> senden nicht möglich",
	                 Toast.LENGTH_LONG).show();  
		}
		else
			Toast.makeText(Main_Activity,"manuelle Steuerung ist deaktiviert",
	                 Toast.LENGTH_LONG).show(); 
	}
	
	private void updateBlue(){
		if(manuell_onoff){
			if(Main_Activity.connected){
				
				String h = "#blue_"+blue+";";
				Main_Activity.mBluetoothService.write(h.getBytes());
			}
			else
				Toast.makeText(Main_Activity,"nicht verbunden -> senden nicht möglich",
		                 Toast.LENGTH_LONG).show();      
	
		}
		else
			Toast.makeText(Main_Activity,"manuelle Steuerung ist deaktiviert",
                 Toast.LENGTH_LONG).show(); 
	}
}
