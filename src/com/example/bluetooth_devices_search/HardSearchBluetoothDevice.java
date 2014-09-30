package com.example.bluetooth_devices_search;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

public class HardSearchBluetoothDevice implements FindBluetoothDevices{
	private BluetoothAdapter blAdapter;
	private ArrayList<BluetoothDevice> devices;
	private ArrayList<String> devicesNames;
	private ArrayAdapter<String> adapter;
	private MainActivity context;
	private final static String TAG = "HardSearchBluetoothDevices";
	private boolean closeThread;
	private Handler hMessage;
	private ListView list;
	
	public HardSearchBluetoothDevice(MainActivity context, ListView list, 
			Handler hMessage) {
		blAdapter = BluetoothAdapter.getDefaultAdapter();
		devices = new ArrayList<BluetoothDevice>();
		devicesNames = new ArrayList<String>();
		this.context = context;
		adapter =  new ArrayAdapter<String>(context,
    			android.R.layout.simple_list_item_1, devicesNames);
		this.list = list;
		this.hMessage = hMessage;
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	if(!devices.contains(device)){
		        	devicesNames.add(device.getName());
		        	adapter.notifyDataSetChanged();		        	
		            devices.add(device);
	        	}
	        }
	    }
	};
	
	// Receiver finished searching after closing searching
	private BroadcastReceiver discoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	context.unregisterReceiver(mReceiver);
            context.unregisterReceiver(discoveryFinishedReceiver); 
            //Start search from bonded list(stage2)
            (new SearchiddenDevices(new Handler(){
    			public void handleMessage(android.os.Message msg) {
    				adapter.notifyDataSetChanged();
    			}    			
            }, //Bad decision for click on button from not main thread
            new Handler(){
    			public void handleMessage(android.os.Message msg) {
    				HardSearchBluetoothDevice.this.context.onClickHardSearch(null);
    			}
            })).start();
        }
    };
	
    //Search stage 1
	public boolean find(){
		if(list.getAdapter() == null || !list.getAdapter().equals(adapter))
			list.setAdapter(adapter);   
		closeThread = false;
		devices.clear();
		devicesNames.clear();
		adapter.notifyDataSetChanged();
		if (!blAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    context.startActivityForResult(enableBtIntent, 2);
		}
		// Register receivers
		context.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		context.registerReceiver(discoveryFinishedReceiver, 
				new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		// Don't forget to unregister during onDestroy
		return blAdapter.startDiscovery(); 
	}
	

	public void close() {
		closeThread = true;
		if(blAdapter.isDiscovering()){
			context.unregisterReceiver(mReceiver);
	        context.unregisterReceiver(discoveryFinishedReceiver);
			blAdapter.cancelDiscovery();
		}
		
	}
	
	class SearchiddenDevices extends Thread {
		private Handler hAdapter, hClose;
		
		public SearchiddenDevices(Handler hAdapter, Handler hClose) {
			// TODO Auto-generated constructor stub
			this.hAdapter = hAdapter;
			this.hClose = hClose;
		}
		//Begin search from state2 and 3
		public void run(){ //Stage2
			for(BluetoothDevice device : blAdapter.getBondedDevices()){
				if(closeThread)
					return;
				if(!devices.contains(device) && isActive(device)){
					devices.add(device);
					devicesNames.add(device.getName());
					hAdapter.sendEmptyMessage(0);
				}
			}
			if(((CheckBox) context.findViewById(R.id.checkBox1)).isChecked()){
				// 50331368,728822305451518934665002 years
				getBrutAllDevices((byte) 6, "");
				if(!closeThread)
					hClose.sendEmptyMessage(0);
			}else hClose.sendEmptyMessage(0);
		}
		
		//Stage 3
		private void getBrutAllDevices(byte rec, String macPart) {
			String numb, mac;
			rec--;
			for(char i = 0; i < 256 && !closeThread; i++){
				numb = Integer.toHexString(i).toUpperCase(Locale.getDefault());
				mac = macPart + (numb.length() == 1 ? "0" + numb : numb);
				if(rec == 0){
					Log.d(TAG, mac);		
					Message msg = new Message();
					msg.obj = mac;
					hMessage.sendMessage(msg);
					BluetoothDevice device = blAdapter.getRemoteDevice(mac);
					if(!devices.contains(device) && isActive(device)){
						devices.add(device);
						devicesNames.add(device.getName());
						hAdapter.sendEmptyMessage(0);
					}
				}
				else{
					getBrutAllDevices(rec, mac + ":");
				}
			}
		}
		
		//  Check device on Bluetooth module activation
		private boolean isActive(BluetoothDevice device) {
			boolean connected = false;
	        try {
	            try {
	                Method m = device.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
	                try {
	                    BluetoothSocket bs = (BluetoothSocket) m.invoke(device,Integer.valueOf(1));
	                    bs.connect();
	                    connected = true;
	                    Log.d(TAG, device.getName() + " - connected");
	                } catch (IOException e) {
	                    Log.e(TAG, "IOException: "+e.getLocalizedMessage());
	                    Log.d(TAG, device.getName() + " - not connected");
	                }
	            } catch (IllegalArgumentException e) {
	                Log.e(TAG, "IllegalArgumentException: "+e.getLocalizedMessage());
	            } catch (IllegalAccessException e) {
	                Log.e(TAG, "IllegalAccessException: "+e.getLocalizedMessage());
	            } catch (InvocationTargetException e) {
	                Log.e(TAG, "InvocationTargetException: "+e.getLocalizedMessage());
	            }
	        } catch (SecurityException e) {
	            Log.e(TAG, "SecurityException: "+e.getLocalizedMessage());
	        } catch (NoSuchMethodException e) {
	            Log.e(TAG, "NoSuchMethodException: "+e.getLocalizedMessage());
	        }
	        return connected;
		}
	}

	@Override
	public ArrayList<BluetoothDevice> getFindDvices() {
		// TODO Auto-generated method stub
		return devices;
	}

}
