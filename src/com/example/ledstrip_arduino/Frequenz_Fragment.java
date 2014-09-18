package com.example.ledstrip_arduino;




import android.app.Activity;

import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Frequenz_Fragment extends Fragment{

	private Activity Main_Activity;//=getActivity();
	
	public void setActivity(Activity a)	{Main_Activity=a;}

	private static final String TAG = MainActivity.class.getSimpleName();	
	
	public Frequenz_Fragment(){
		
	}

	public void onCreateView(){
		Main_Activity.setContentView(R.layout.frequenz_fragment);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState)
	{
	View rootView = inflater.inflate(R.layout.frequenz_fragment,
	container, false);
	return rootView;

	}	

	@Override
	public void onStart() {
	super.onStart();
	
	}

	@Override
	public void onPause() {
	super.onPause();
	
	}
	}