package com.example.bluetooth_devices_search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private BluetoothDevice device;
	private SearchBluetoothDevices normalSearch;
	private HardSearchBluetoothDevice hardSearch;
	private boolean normalSearchState, hardSearchState;
	private FindBluetoothDevices findDevices;
	
    @SuppressLint("HandlerLeak") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
    	hardSearch = new HardSearchBluetoothDevice(this, (ListView) findViewById(R.id.listView1), 
    			new Handler(){
			public void handleMessage(android.os.Message msg){
				((TextView) findViewById(R.id.textView2)).setText((String) msg.obj);
			}
		});
    	normalSearch = new SearchBluetoothDevices(this, ((ListView) findViewById(R.id.listView1)));
        ((ListView) findViewById(R.id.listView1)).setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				device = findDevices.getFindDvices().get(position);					
				((TextView) findViewById(R.id.textView1)).setText("Name = " + device.getName() 
						+ " Address = " + device.getAddress() + " Bonded sate = " + 
						getBondSateName(device.getBondState()) + " Major Device Class = " +
						getMajorDeviceClassName(device.getBluetoothClass().getMajorDeviceClass()) +
						" Device Class = " + getDeviceClassName(device.getBluetoothClass().getDeviceClass()));
			}
		});
    }
    
    public void onChecked(View v){
    	if(((CheckBox) findViewById(R.id.checkBox1)).isChecked())
    		Toast.makeText(this, "Prepare to wait 50331368 years", Toast.LENGTH_SHORT).show();
    	else Toast.makeText(this, "Right decision", Toast.LENGTH_SHORT).show();
    }
    
    public void onClickNormalSearch(View v){
    	if(!normalSearchState){
    		if(normalSearch.find()){
    	        ((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		    	((Button) findViewById(R.id.button1)).setEnabled(false);
		    	normalSearchState = true;	    	
		    	findDevices = normalSearch;
		    	((Button) findViewById(R.id.button2)).setText(R.string.butt_stop_normal_search);
    		}
    	}else{
            ((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
    		((Button) findViewById(R.id.button1)).setEnabled(true);
	    	normalSearchState = false;
	    	normalSearch.stop();
	    	((Button) findViewById(R.id.button2)).setText(R.string.butt_start_normal_search);
    	}
    }
    
    public void onClickHardSearch(View v){    	
    	if(!hardSearchState && hardSearch.find()){ 	
	        ((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
	    	((Button) findViewById(R.id.button2)).setEnabled(false);
	    	hardSearchState = true;	    	
	    	findDevices = hardSearch;
	    	((Button) findViewById(R.id.button1)).setText(R.string.butt_stop_hard_search);
    	}else{
            ((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
    		((Button) findViewById(R.id.button2)).setEnabled(true);
	    	hardSearchState = false;
	    	hardSearch.close();
	    	((Button) findViewById(R.id.button1)).setText(R.string.butt_start_hard_search);
    	}
    }
    
    private String getMajorDeviceClassName(int majorDeviceClass){
    	switch (majorDeviceClass){
    		case BluetoothClass.Device.Major.AUDIO_VIDEO:{
    			return new String("AUDIO_VIDEO");
    		}
    		case BluetoothClass.Device.Major.COMPUTER:{
    			return new String("COMPUTER");
    		}
    		case BluetoothClass.Device.Major.HEALTH:{
    			return new String("HEALTH");
    		}
    		case BluetoothClass.Device.Major.IMAGING:{
    			return new String("IMAGING");
    		}
    		case BluetoothClass.Device.Major.MISC:{
    			return new String("MISC");
    		}
    		case BluetoothClass.Device.Major.NETWORKING:{
    			return new String("NETWORKING");
    		}
    		case BluetoothClass.Device.Major.PERIPHERAL:{
    			return new String("PERIPHERAL");
    		}
    		case BluetoothClass.Device.Major.PHONE:{
    			return new String("PHONE");
    		}
    		case BluetoothClass.Device.Major.TOY:{
    			return new String("TOY");
    		}
    		case BluetoothClass.Device.Major.UNCATEGORIZED:{
    			return new String("UNCATEGORIZED");
    		}
    		case BluetoothClass.Device.Major.WEARABLE:{
    			return new String("WEARABLE");
    		}
    		default:{
    			return new String("?");
    		}
    	}
    }
    
    private String getBondSateName(int bondState){
    	String bondStateName = new String("?");
		switch (bondState){
		case BluetoothDevice.BOND_BONDED:{
			bondStateName = "Bonded";
			break;
		}
		case BluetoothDevice.BOND_BONDING:{
			bondStateName = "Bonding";
			break;
		}
		case BluetoothDevice.BOND_NONE:{
			bondStateName = "None";
			break;
		}
		}
		return bondStateName;
    }
    
    private String getDeviceClassName(int deviceClass){
    	String deviceClassName = new String("?");
		switch (deviceClass){
		case BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER:{
			deviceClassName = "AUDIO_VIDEO_CAMCORDER";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:{
			deviceClassName = ".AUDIO_VIDEO_CAR_AUDIO";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE:{
			deviceClassName = ".AUDIO_VIDEO_CAR_AUDIO";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:{
			deviceClassName = "AUDIO_VIDEO_HEADPHONES";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:{
			deviceClassName = "AUDIO_VIDEO_HIFI_AUDIO";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER:{
			deviceClassName = "AUDIO_VIDEO_LOUDSPEAKER";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE:{
			deviceClassName = "AUDIO_VIDEO_MICROPHONE";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO:{
			deviceClassName = "AUDIO_VIDEO_PORTABLE_AUDIOO";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX:{
			deviceClassName = "AUDIO_VIDEO_SET_TOP_BOX";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED:{
			deviceClassName = "AUDIO_VIDEO_UNCATEGORIZED";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_VCR:{
			deviceClassName = "AUDIO_VIDEO_VCR";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA:{
			deviceClassName = "AUDIO_VIDEO_VIDEO_CAMERA";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING:{
			deviceClassName = "AUDIO_VIDEO_VIDEO_CONFERENCING";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER:{
			deviceClassName = "AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY:{
			deviceClassName = "AUDIO_VIDEO_VIDEO_GAMING_TOY";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR:{
			deviceClassName = "AUDIO_VIDEO_VIDEO_MONITOR";
			break;
		}
		case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:{
			deviceClassName = "AUDIO_VIDEO_WEARABLE_HEADSET";
			break;
		}case BluetoothClass.Device.COMPUTER_DESKTOP:{
			deviceClassName = "COMPUTER_DESKTOP";
			break;
		}
		case BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA:{
			deviceClassName = "COMPUTER_HANDHELD_PC_PDA";
			break;
		}
		case BluetoothClass.Device.COMPUTER_LAPTOP:{
			deviceClassName = "COMPUTER_LAPTOP";
			break;
		}
		case BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA:{
			deviceClassName = "COMPUTER_PALM_SIZE_PC_PDA";
			break;
		}
		case BluetoothClass.Device.COMPUTER_SERVER:{
			deviceClassName = "COMPUTER_SERVER";
			break;
		}
		case BluetoothClass.Device.COMPUTER_UNCATEGORIZED:{
			deviceClassName = "COMPUTER_UNCATEGORIZED";
			break;
		}
		case BluetoothClass.Device.COMPUTER_WEARABLE:{
			deviceClassName = "COMPUTER_WEARABLE";
			break;
		}
		case BluetoothClass.Device.HEALTH_BLOOD_PRESSURE:{
			deviceClassName = "HEALTH_BLOOD_PRESSURE";
			break;
		}
		case BluetoothClass.Device.HEALTH_DATA_DISPLAY:{
			deviceClassName = "HEALTH_DATA_DISPLAY";
			break;
		}
		case BluetoothClass.Device.HEALTH_GLUCOSE:{
			deviceClassName = "HEALTH_GLUCOSE";
			break;
		}
		case BluetoothClass.Device.HEALTH_PULSE_OXIMETER:{
			deviceClassName = "HEALTH_PULSE_OXIMETER";
			break;
		}
		case BluetoothClass.Device.HEALTH_PULSE_RATE:{
			deviceClassName = "HEALTH_PULSE_RATE";
			break;
		}
		case BluetoothClass.Device.HEALTH_THERMOMETER:{
			deviceClassName = "HEALTH_THERMOMETER";
			break;
		}
		case BluetoothClass.Device.HEALTH_UNCATEGORIZED:{
			deviceClassName = "HEALTH_UNCATEGORIZED";
			break;
		}
		case BluetoothClass.Device.HEALTH_WEIGHING:{
			deviceClassName = "HEALTH_WEIGHING";
			break;
		}
		case BluetoothClass.Device.PHONE_CELLULAR:{
			deviceClassName = "PHONE_CELLULAR";
			break;
		}
		case BluetoothClass.Device.PHONE_CORDLESS:{
			deviceClassName = "PHONE_CORDLESS";
			break;
		}		
		case BluetoothClass.Device.PHONE_ISDN:{
			deviceClassName = "PHONE_ISDN";
			break;
		}
		case BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY:{
			deviceClassName = "PHONE_MODEM_OR_GATEWAY";
			break;
		}
		case BluetoothClass.Device.PHONE_SMART:{
			deviceClassName = "PHONE_SMART";
			break;
		}
		case BluetoothClass.Device.PHONE_UNCATEGORIZED:{
			deviceClassName = "PHONE_UNCATEGORIZED";
			break;
		}
		case BluetoothClass.Device.TOY_CONTROLLER:{
			deviceClassName = "TOY_CONTROLLER";
			break;
		}
		case BluetoothClass.Device.TOY_GAME:{
			deviceClassName = "TOY_GAME";
			break;
		}
		case BluetoothClass.Device.TOY_ROBOT:{
			deviceClassName = "TOY_ROBOT";
			break;
		}
		case BluetoothClass.Device.TOY_UNCATEGORIZED:{
			deviceClassName = "TOY_UNCATEGORIZED";
			break;
		}
		case BluetoothClass.Device.TOY_VEHICLE:{
			deviceClassName = "TOY_VEHICLE";
			break;
		}
		case BluetoothClass.Device.WEARABLE_GLASSES:{
			deviceClassName = "WEARABLE_GLASSES";
			break;
		}
		case BluetoothClass.Device.WEARABLE_HELMET:{
			deviceClassName = "WEARABLE_HELMET";
			break;
		}
		case BluetoothClass.Device.WEARABLE_JACKET:{
			deviceClassName = "WEARABLE_JACKET";
			break;
		}
		case BluetoothClass.Device.WEARABLE_PAGER:{
			deviceClassName = "WEARABLE_PAGER";
			break;
		}
		case BluetoothClass.Device.WEARABLE_UNCATEGORIZED:{
			deviceClassName = "WEARABLE_UNCATEGORIZED";
			break;
		}
		case BluetoothClass.Device.WEARABLE_WRIST_WATCH:{
			deviceClassName = "WEARABLE_WRIST_WATCH";
			break;
		}
		}
		return deviceClassName;
    }
}
