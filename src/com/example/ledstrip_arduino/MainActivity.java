package com.example.ledstrip_arduino;

import java.util.Locale;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	
	private static final int RESULT_SETTING_BT = 3;
	private static final String TAG = "MainActivity";
	public static final String BROADCASTACTION = "com.example.ledstrip_arduino_bluetoothstatus";
	
	public String mac="";
	public String deviceName;
	public BluetoothAdapter blueAdapter;
	public BluetoothViewerService mBluetoothService;
	public boolean connected;
	public String bluetoothstatus="";
	private Intent intentbluetoothstatus = new Intent();
	

	
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    
 // The Handler that gets information back from the BluetoothService
 	private final Handler mHandler = new Handler() {
 		@Override
 		public void handleMessage(Message msg) {
 			switch (msg.what) {
 				case BluetoothViewerService.MSG_CONNECTED:  					
 					bluetoothstatus = "connected with "+mac;
 					connected = true;
 					deviceName = msg.obj.toString();
 					break;
 				case BluetoothViewerService.MSG_CONNECTING:
 					bluetoothstatus = "connecting with "+mac;
 					connected = false; 					
 					break;
 				case BluetoothViewerService.MSG_NOT_CONNECTED:
 					bluetoothstatus = "not connected";
 					connected = false; 					
 					break;
 				case BluetoothViewerService.MSG_CONNECTION_FAILED:
 					bluetoothstatus = "connection failed with "+mac;
 					connected = false; 					
 					break;
 				case BluetoothViewerService.MSG_CONNECTION_LOST:
 					bluetoothstatus = "connection lost with "+mac;
 					connected = false; 					
 					break;
 				case BluetoothViewerService.MSG_BYTES_WRITTEN:
 					//String written = new String((byte[]) msg.obj);
 					break;
 				case BluetoothViewerService.MSG_LINE_READ:
 					break;
 			} 			
			sendBroadcast(intentbluetoothstatus);
 		}
 	};
    
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.main_activity);
        blueAdapter=BluetoothAdapter.getDefaultAdapter();
        intentbluetoothstatus.setAction(BROADCASTACTION);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //mSectionsPagerAdapter.setActivity(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	
    	if(mac=="")
    		Toast.makeText(getApplicationContext(),"no bluetooth device is selected",
	                 Toast.LENGTH_LONG).show();        
        else	//Device vorhanden, Verbindung wird aufgebaut
        {
        	if(mBluetoothService==null)
        	{
            	mBluetoothService = new BluetoothViewerService(mHandler);
            }
        	if(!connected)
			{
        		Toast.makeText(getApplicationContext(),"trying to connect to Used Device: "+mac,
   	                 Toast.LENGTH_LONG).show();
				try{					
					BluetoothDevice device = blueAdapter.getRemoteDevice(mac);
					try{
						mBluetoothService.connect(device);
					}
					catch(Exception e){
						Log.e("error", "ConnectTread: "+e.getMessage());
					}						
				}
				catch(Exception e)
				{
					Log.e("error", "Manuell_Fragment: Verbindung nicht aufbaubar");
				}
			}        	
        }
    }
   
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {    	
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode==RESULT_SETTING_BT)
    	{
    		mac=data.getExtras().getString("com.example.ledstrip_arduino.mac"); 
//    		Toast.makeText(getApplicationContext(),"Used Device: "+mac,
//	                 Toast.LENGTH_LONG).show();
    	}
//    	else
//    	{
//    		super.onActivityResult(requestCode, resultCode, data);
//    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.optionsmenue, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.menue_bluetooth_setup:	    	
	    	Intent in = new Intent(MainActivity.this,EinstellungActivity.class);
	    	startActivityForResult(in, RESULT_SETTING_BT);
	    	return true;
	    	
	     case R.id.menue_beenden:	    
		     AlertDialog.Builder builder = new AlertDialog.Builder(this);		    
		     builder.setTitle("Beenden");
		     builder.setMessage("Wollen Sie LED Strip wirklich beenden?");		    
		     builder.setPositiveButton("JA", new DialogInterface.OnClickListener() {	    
			     @Override
			     public void onClick(DialogInterface dialog, int which) {	
			    	 if(connected)			    						
			 			mBluetoothService.stop();
			 			
				     System.exit(0);				    
				     dialog.dismiss();
			     }	    
		     });
		    
		     builder.setNegativeButton("NEIN", new DialogInterface.OnClickListener() {	    
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
				    
				     dialog.dismiss();
			     }
			    
		     });
		    
		     AlertDialog alert = builder.create();
		     alert.show();
		     return true;
		    
	     default:
	    	 return super.onOptionsItemSelected(item);
	     }
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }
    
  

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

//    	private Activity Main_Activity;
//    	public void setActivity(Activity a)	{Main_Activity=a;}
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment f=null;
            switch(position){
            case 0:
            	f =new Manuell_Fragment();
            	break;            	
            case 1:
            	f= new Automatik_Fragment();
            	break;
            case 2:
            	f =new Frequenz_Fragment();
            	break;
           
            }
            return f;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_selection1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_selection2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_selection3).toUpperCase(l);
               
            }
            return null;
        }
    }

    
}
