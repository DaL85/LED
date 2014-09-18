package com.example.ledstrip_arduino;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private int textViewResourceId;

    public CustomAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId=textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bluetooth_item, null);
        }

        String value = getItem(position);
        if (value!= null) {
            // My layout has only one TextView
            TextView itemView = (TextView) view.findViewById(R.id.textview_b_item);
            if (itemView != null) {
                // do whatever you want with your string and long
                itemView.setText(value);
                itemView.setClickable(true);
//                itemView.setOnClickListener(new OnClickListener() {  
//                	
//    	            @Override
//    	            public void onClick(View v) {
//    	                // TODO Auto-generated method stub
//    	             AlertDialog.Builder builder = new AlertDialog.Builder(context);		    
//    	   		     builder.setTitle("Verbinden");
//    	   		     builder.setMessage("Wollen Sie eine Verbindung mit "+itemView.getText()+" aufbauen?");		    
//    	   		     builder.setPositiveButton("JA", new DialogInterface.OnClickListener() {	    
//    	   			     @Override
//    	   			     public void onClick(DialogInterface dialog, int which) {			    
//    	   				     System.exit(0);				    
//    	   				     dialog.dismiss();
//    	   			     }	    
//    	   		     });
    	   		    
//    	   		     builder.setNegativeButton("NEIN", new DialogInterface.OnClickListener() {	    
//    	   			     @Override
//    	   			     public void onClick(DialogInterface dialog, int which) {
//    	   				    
//    	   				     dialog.dismiss();
//    	   			     }
//    	   			    
//    	   		     });
//    	   		    
//    	   		     AlertDialog alert = builder.create();
//    	   		     alert.show();
//    	            }
//    	          });

                
            }
         }

        return view;
    }
}