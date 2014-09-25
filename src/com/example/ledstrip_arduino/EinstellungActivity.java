package com.example.ledstrip_arduino;


import java.util.Collection;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LauncherActivity.ListItem;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EinstellungActivity extends ListActivity{
	 
	   private static final int REQUEST_ENABLE_BT = 1;
	   private static final int RESULT_SETTING_BT = 3;
	   private Button findBtn;
	   private TextView text;
	   private BluetoothAdapter myBluetoothAdapter;
	   private ArrayAdapter<String> BTArrayAdapter;
	   AlertDialog.Builder builder;
	   AlertDialog alert;
	   
	   String[] item_array=null;
	   Intent intent=new Intent();
	   boolean receiverregisted =false;
		
	   
	   
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.einstellung_activity);
	       
	      // take an instance of BluetoothAdapter - Bluetooth radio
	      myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	      text = (TextView) findViewById(R.id.text);
	      if(myBluetoothAdapter == null) 
	      {
	          findBtn.setEnabled(false);
	          text.setText("Status: not supported");
	           
	          Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
	                 Toast.LENGTH_LONG).show();
	      } 
	      else 
	      {
	    	  if(myBluetoothAdapter.isEnabled())
	    		  text.setText("Status: Enabled");
	    	  else
	    		  text.setText("Status: Disabled");
	    	  
	          findBtn = (Button)findViewById(R.id.search);
	          findBtn.setOnClickListener(new OnClickListener() {
	             
	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                find(v);
	                
	            }
	          });
	         
	          BTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);//new CustomAdapter(this,R.id.textview_b_item);
	          setListAdapter(BTArrayAdapter);
	          
				ListView lv = getListView();
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				    item_array=BTArrayAdapter.getItem(arg2).split("\n");
					//Toast.makeText(getBaseContext(), "Name:" + item_array[0]+ " Adresse: "+item_array[1] , Toast.LENGTH_SHORT).show();

						new AlertDialog.Builder(EinstellungActivity.this)
						   .setMessage("Möchten Sie eine Bluetoothverbingung zu "+item_array[0]+" mit Adresse:"+item_array[1]+"aufbauen?")
						   .setTitle("Gerät verwenden")
						   .setCancelable(false)
						   .setPositiveButton("JA", new DialogInterface.OnClickListener() {
							   @Override
							     public void onClick(DialogInterface dialog, int which) {								     
								     intent.putExtra("com.example.ledstrip_arduino.mac", item_array[1]);
								     EinstellungActivity.this.setResult(RESULT_SETTING_BT,intent);
								     dialog.dismiss();
								     EinstellungActivity.this.finish();
								     
							     }	 
						    })
						    .setNegativeButton("NEIN", new DialogInterface.OnClickListener() {	    
						     @Override
						     public void onClick(DialogInterface dialog, int which) {
							    
							     dialog.dismiss();
						     }
						    
						    })
						   .show();
					
					}
				});
	      }
	   }
	   
	    
	   @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       // TODO Auto-generated method stub
	       if(requestCode == REQUEST_ENABLE_BT){
	           if(myBluetoothAdapter.isEnabled()) {
	               text.setText("Status: Enabled");
	           } else {  
	               text.setText("Status: Disabled");
	           }
	       }
	   }
	   
	   @Override
		public void onStart() {
			super.onStart();

			// load last state
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EinstellungActivity.this);
	       Map<String, String> m =(Map<String, String>) prefs.getAll();
	       Collection<String> c =m.values();
	       int i =0;
	        while(prefs.contains("device"+i))
	        {	        	
        		BTArrayAdapter.add((String)prefs.getString("device"+i, ""));
        		i++;      	        	
	        }
		}
	   
	   @Override
		public void onStop() {
			super.onStop();
			PreferenceManager.getDefaultSharedPreferences(EinstellungActivity.this).edit().clear().commit();
			int anz =BTArrayAdapter.getCount();
			String[] devices=new String[anz];
			for(int i=0;i<anz;i++)
			{
				devices[i]=BTArrayAdapter.getItem(i);
				PreferenceManager.getDefaultSharedPreferences(EinstellungActivity.this).edit().putString("device"+i, devices[i]).commit();
			}
		}
	    
	   final BroadcastReceiver bReceiver = new BroadcastReceiver() {
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            // When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	                 // Get the BluetoothDevice object from the Intent
	                 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                 // add the name and the MAC address of the object to the arrayAdapter
	                 BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	                 BTArrayAdapter.notifyDataSetChanged();
	            }
	        }
	    };
  
	   public void find(View view) {
		   int counter=0;
		   if (!myBluetoothAdapter.isEnabled()) {
		         Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		         startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);	
		         counter=0;
		         while (!myBluetoothAdapter.isEnabled()&&counter<=60)
		            {
		                try
		                {
		                    Thread.sleep(1000);
		                    counter++;
		                }
		                catch (InterruptedException ie)
		                {
		                    // unexpected interruption while enabling bluetooth
		                    Thread.currentThread().interrupt(); // restore interrupted flag
		                    return;
		                }
		            }
		         
		 
		         
		      }
		   if(counter<=60)
		   {
			   Toast.makeText(getApplicationContext(),"searching for devices" ,
		                 Toast.LENGTH_LONG).show();
		       if (myBluetoothAdapter.isDiscovering()) {
		           // the button is pressed when it discovers, so cancel the discovery
		           myBluetoothAdapter.cancelDiscovery();
		       }
		       else {
		            BTArrayAdapter.clear();
		            myBluetoothAdapter.startDiscovery();
		             
		            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));   
		            receiverregisted=true;
		        }   
		   }
	   }
	    
	   @Override
	   protected void onDestroy() {
	       // TODO Auto-generated method stub
	       super.onDestroy();
	       if(receiverregisted)
	    	   unregisterReceiver(bReceiver);
	   }

	}
