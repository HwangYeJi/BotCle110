/*<?????>
?? ???α?? ?? ??????? (??)????? ?????????.
?? ???α?? ?? ??????? (??)????? ??????? ????? ?????? ?????? ?????? ?? ????.
????? ??????? ????? ????? ???? ????? ???? ?????? ???? ????????.

<License>
The program or internal source codes was created by CHIPSEN Co.,Ltd.
In order to use the program or internal source codes, you must buy CHIPSEN's Bluetooth products.
You are not allowed to use it for purposes other than CHIPSEN's Bluetooth Products

Copyright 2015. CHIPSEN all rights reserved.*/


package com.chipsen.cle110_test_kit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.chipsen.bleservice.BluetoothLeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 * BLE
 */
public class DeviceScanActivity extends ListActivity implements TextToSpeech.OnInitListener {

	private final static String TAG = BluetoothLeService.class.getSimpleName();
	
    private LeDeviceListAdapter mLeDeviceListAdapter; //

    private BluetoothAdapter mBluetoothAdapter;//

    // 스캐닝중인가?
    private boolean mScanning;//BLE

    // 스캔을 위한 핸들러
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    // 스캔 주기 10초
    private static final long SCAN_PERIOD = 10000;

    int user_data_count = 0;

    boolean mScanning_run = false; //

    BLE_Device mBLE_Device;

	ImageView imageView_search;
	ImageView imageView_search_infinity;
	private GifView gf1;

    // 검색 상태를 검색으로 설정
	boolean Search_state = true;


    private TextToSpeech mTts;
    private boolean sayingLocation = false;


    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.KOREAN);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.


                // Greet the user.
                //sayLocation();
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void sayLocation() {
        String location = "칩센의 비콘에 접근했습니다.";
;        mTts.speak(location,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 커스텀 타이틀 속성으로 구성
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        // 콘텐츠 레이아웃...
    	setContentView(R.layout.activity_device_logo);

        // 타이틀 레이아웃 설정.
    	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,	R.layout.custom_title1);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //

        // 검색 버튼에 대한 설정.
        Display_loop();

        mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
        );

        // 핸들러
        mHandler = new Handler();

        // 디바이스 개체를 저장할 자료
        mBLE_Device = new BLE_Device();
    
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.

        // 불루투스 어댑터...
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    // 버튼 기능 설정...
    private void Display_loop() { // TCP
		
		gf1 = (GifView)findViewById(R.id.gif1);
		gf1.setGifImage(R.drawable.beacon);

        // gif 이미지의 커버를 보여줌.
		gf1.showCover();
		

        // 검색을 시도한 후 일정 시간후에 멈춘다.
		 imageView_search=(ImageView)findViewById(R.id.imageView_search); 
		 imageView_search_infinity=(ImageView)findViewById(R.id.imageView_search_infinity);

		 imageView_search.setOnClickListener(new OnClickListener() { // search
				
				@Override
				public void onClick(View v) {
					//
					if(Search_state){
                        // 검색중...
						Search_state = false;

                        mBLE_Device.setclear();
                        mLeDeviceListAdapter.clear();

                        // 스캔을 시작함...
                        scanLeDevice(true);

                        imageView_search.setImageResource(R.drawable.search_b_ing1);
                        mLeDeviceListAdapter.notifyDataSetChanged();

					}else{
						Search_state=true;
						 scanLeDevice(false);
						 mLeDeviceListAdapter.clear();
						 
						 imageView_search.setImageResource(R.drawable.search_b);
						
					}
					
				}
			});

	     // 검색을 계속함...
		 imageView_search_infinity.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 스캐닝중인가? 아니면...
					if(!mScanning_run){
	            		
	            		scanLeDevice(false);
	            		gf1.showAnimation();

                        // 스캐닝을 켠다
	            		mScanning_run = true;

                        // 핸들러에게 메세지를 보냄...0으로 스캔을 시작하도록함...
	            		mscaneHandler.sendEmptyMessageDelayed(0, 100);

	            		imageView_search_infinity.setImageResource(R.drawable.search_l_ing);

	            	} else {
	            	    // 커버로 이미지 변경
	            		gf1.showCover();

                        // 스캐닝을 끈다
	                    mScanning_run = false;

                        // 스캔 메세지를 제거함...
	            		mscaneHandler.sendEmptyMessageDelayed(2, 100);

	            		imageView_search_infinity.setImageResource(R.drawable.search_l);
	            	}
				}
			});

	}
    
    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            // 블루투스 사용 가능한지 체크. 가능하도록 설정.
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        
     
    }
    public void onDestroy() {

        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
		super.onDestroy();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mScanning_run = false;
        scanLeDevice(false);

        mHandler.removeMessages(0);

        mLeDeviceListAdapter.clear();

        // 핸들러를 정지함. 모든 메세지 제거...
        mscaneHandler.removeMessages(0);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);

        if (device == null) return;
        
		Intent resultIntent = new Intent();

        // 디바이스의 이름과 주소를 인텐트에 담아 넘겨줌...
		resultIntent.putExtra(NavigationActivity.EXTRAS_DEVICE_NAME, device.getName());
		resultIntent.putExtra(NavigationActivity.EXTRAS_DEVICE_ADDRESS,device.getAddress());
		setResult(Activity.RESULT_OK, resultIntent);
		
		finish();
		

    }
    
    // 스캔을 수행함...
    private void scanLeDevice(final boolean enable) {
        //
        user_data_count = 0;

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            // 일정 시간 후에는 스캔을 멈춘다...딜레이로 수행함...
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 스캔중이 아님. 끈다.
                    mScanning = false;

                    // 스캔을 멈춘다.
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    // 애니메이션을 멈추고 커버이미지로 수정
                    gf1.showCover();

                    // 버튼 이미지를 수정
                    imageView_search.setImageResource(R.drawable.search_b);

                    // 검색 가능으로 설정...
                    Search_state = true;
                }
            }, SCAN_PERIOD); //

            // 애니메이션을 시작한다.
            gf1.showAnimation();

            // 스캔중으로 설정
            mScanning = true;

            // 불루투스 어댑터에서 스캔을 시작함...
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
        	gf1.showCover();

        	imageView_search.setImageResource(R.drawable.search_b);

            // 검색 가능으로 설정
        	Search_state = true;

            // 스캔중은 끈다
            mScanning = false;

            // 스캔을 종료함
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
       
        
    }
    
    // 지속적으로 메세지를 주고 받기위한 핸들러
    @SuppressLint("HandlerLeak")
    private final Handler mscaneHandler = new Handler() { //
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case 0:
    			user_data_count = 0;

    			mBLE_Device.setclear();
    			mLeDeviceListAdapter.clear();

                // 스캐닝중으로 설정
    			 mScanning = true;

                // 스캔 시작...
    	         mBluetoothAdapter.startLeScan(mLeScanCallback);

                // 리스트 업데이트...
    	         mLeDeviceListAdapter.notifyDataSetChanged();

                // 8초후에 정지 메세지를 보냄.
    	         if(mScanning_run){
                     // 핸들러에게 메세지를 8초후에 메세지를 보냄.
                     sayingLocation = false;
    	        	 mscaneHandler.sendEmptyMessageDelayed(1, 5000);
                 }

    		break;

    		case 1:
                // 스캔 종료로 설정
    		    mScanning = false;

                // 스캔을 종료함.
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                // 2초후에 다시 스캔 메세지를 보냄...
               if(mScanning_run) {
                   mscaneHandler.sendEmptyMessageDelayed(0, 200);
               }
              
    		break;

    		case 2:

                // 스캔을 종료함.
    		    mScanning = false;

                // 스캔을 멈추고...
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                // 모든 메세지를 제거함.
                mscaneHandler.removeMessages(0);
                
    		break;
    		}
    		
    	}
    };

    

    // Adapter for holding devices found through scanning.
    // 장치들 리스트 어댑터...
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();

            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        // 장치가 발견되면 아이템 추가...
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        
// ?
        @SuppressLint("InflateParams")
		@Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
           try{
            // General ListView optimization code.
            if (view == null) {

                // 아이템을 표시할 레이아웃...
                view = mInflator.inflate(R.layout.activity_device_scan, null);
            
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.textView_user_data = (TextView) view.findViewById(R.id.textView_user_data);
                viewHolder.textView_user_value = (TextView) view.findViewById(R.id.textView_user_value);
                viewHolder.textView_rssi = (TextView) view.findViewById(R.id.textView_rssi);
                viewHolder.device_tx_level= (TextView) view.findViewById(R.id.device_tx_level);
                viewHolder.progressBar1 =(ProgressBar) view.findViewById(R.id.progressBar1);
                viewHolder.progressBar_user_value =(ProgressBar) view.findViewById(R.id.progressBar_user_value);
                viewHolder.imageView_pio10 =(ImageView) view.findViewById(R.id.imageView_pio10);
                viewHolder.imageView_pio11 =(ImageView) view.findViewById(R.id.imageView_pio11);
                viewHolder.linearLayout_user_value =(LinearLayout) view.findViewById(R.id.linearLayout_user_value);

           
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
          
            BluetoothDevice device = mLeDevices.get(i);
     
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0){
                viewHolder.deviceName.setText(deviceName);				//?????? ???
            }
            else{
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            Log.d(TAG, "Dbg__ DeviceScan =1 ");
            	viewHolder.deviceAddress.setText(device.getAddress());	//?????? ???
            	byte[] array_byte=mBLE_Device.user_alldata[i].getBytes();
            	
            	String user_TX_level="";
            	switch(array_byte[5]){
            	case (byte)0x08:
            		user_TX_level="8";
            		
            		break;
            	case (byte)0x06:
            		user_TX_level="6";
            		break;
            	case (byte)0x02:
            		user_TX_level="2";
            		break;
            	case (byte) 0xef:
            		switch(array_byte[7]){
            		case (byte) 0xbe:
            			user_TX_level="-2";	
            		break;
            		case (byte) 0xba:
            			user_TX_level="-6";	
            		break;
            		case (byte) 0xb6:
            			user_TX_level="-10";	
            		break;
            		case (byte) 0xb2:
            			user_TX_level="-14";	
            		break;
            		case (byte) 0xae:
            			user_TX_level="-18";	
            		break;

            		}
            		
            		break;
            	}
            	
                Log.d(TAG, "Dbg__ DeviceScan =2 ");
                  byte[] inputBytes1 = mBLE_Device.user_alldata[i].getBytes(); //????? ?????? ??? ???  
                  int start_0=0; 
                  boolean start_bool=false;
                  for (byte b : inputBytes1) {  
                     if(inputBytes1[start_0]==(byte)0x00){
                    	 
                    	 if(start_bool){
                    		 break;	 
                    	 }
                    	 start_bool=true;
                    	  
                     }
                     start_0++;
                  } 
                  
                
                  Log.d(TAG, "Dbg__ DeviceScan =3 ");
            	mBLE_Device.user_data[i] = new String(Arrays.copyOfRange(array_byte, start_0+1,start_0+12 )); //????? ??????
            	mBLE_Device.user_TX_level[i]=user_TX_level;
            	
                Log.d(TAG, "Dbg__ DeviceScan =4 ");
            
            	//int rssi_buff = mBLE_Device.rssi[i]+99;
            	viewHolder.textView_rssi.setText( "Rssi : "+Integer.toString(mBLE_Device.rssi[i])+"dBm"); //RSSI ??? ???
            	viewHolder.device_tx_level.setText("Tx Level : " + user_TX_level + "dBm");


                if(mBLE_Device.rssi[i]+99 > 50 && sayingLocation == false) {
                    sayingLocation = true;
                    sayLocation();
                    Toast.makeText(DeviceScanActivity.this, "접근", Toast.LENGTH_SHORT).show();
                }



            	viewHolder.progressBar1.setProgress( mBLE_Device.rssi[i]+99);

           		viewHolder.textView_user_data.setText("DATA: "+mBLE_Device.user_data[i]);
            	viewHolder.textView_user_value.setText("");
            	viewHolder.imageView_pio10.setImageResource(R.drawable.pio10_off);	
            	viewHolder.imageView_pio11.setImageResource(R.drawable.pio11_off);
            	viewHolder.linearLayout_user_value.setVisibility(view.GONE);
            	 try{
            	String data = mBLE_Device.user_data[i];
            	
            	String set_data="";
            	 Log.d(TAG, "Dbg__ DeviceScan =72 ="+data);
            	if(data.length()>=5){
// user data?? ??? ?? ?о? ???? ?з? ???            	
               	if((data.substring(0, 1).equals("0"))|(data.substring(0, 1).equals("1"))|(data.substring(0, 1).equals("2"))|(data.substring(0, 1).equals("N"))){
               		viewHolder.linearLayout_user_value.setVisibility(view.VISIBLE);
               		if((data.substring(0, 1).equals("0"))|(data.substring(0, 1).equals("1"))|(data.substring(0, 1).equals("2"))){ //AIO ?з?
               			if(!(data.substring(1, 5).equals("0000"))){
               			byte[] bytes = new java.math.BigInteger(data.substring(1, 5), 16).toByteArray();
               			
               			String aio_vl=String.format("%.3f" , byte2Int(bytes)*0.001);
               	
               			
               			set_data= "AIO "+data.substring(0, 1)+": "+aio_vl+"V";
               			
               			viewHolder.progressBar_user_value.setProgress((byte2Int(bytes)));
               			}else{
               				set_data= "AIO "+data.substring(0, 1)+": 0.000V";
               			}
               		}
                   
               		               		
               		if((data.substring(5, 6).equals("H"))|(data.substring(5, 6).equals("L"))){ // PIO?з?
               			if(data.substring(5, 6).equals("H")){
               				viewHolder.imageView_pio10.setImageResource(R.drawable.pio10_on);	
               			}else{
               				viewHolder.imageView_pio10.setImageResource(R.drawable.pio10_off);
               			}
               			if(data.substring(6, 7).equals("H")){
               				viewHolder.imageView_pio11.setImageResource(R.drawable.pio11_on);	
               			}else{
               				viewHolder.imageView_pio11.setImageResource(R.drawable.pio11_off);
               			}
               		               			
               		}
               		viewHolder.textView_user_value.setText(set_data);
               	}
                   
             }	
            }catch(Exception e){
            	
            }
            	 
    	}catch(Exception e){
    		  Log.d(TAG, "Dbg__DeviceScan err =2 ");
    	}
         
            return view;
        }
        
        public int byte2Int(byte[] src) { //byte -> int ?????? ????
        	int s1 = src[0] & 0xFF;
        	int s2 = 0;
        	
        	if(src.length>=2){
        		s2 = src[1] & 0xFF;
        		return ((s1 << 8) + (s2 << 0));
        	}else{
        		return (s1 << 0);
        	}
        }
    }



    // Device scan callback. 스캐닝 콜백
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device,
                             final int rssi,
                             final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Juno
                    if (device.getName().contains("BoT")) {
                        Log.d(TAG, "Dbg__DeviceScan st = 00 ");
                        mLeDeviceListAdapter.addDevice(device);

                        Log.d(TAG, "Dbg__DeviceScan st = 01 " + user_data_count);
                        mLeDeviceListAdapter.notifyDataSetChanged();


                        Log.d(TAG, "Dbg__DeviceScan st = 02 " + rssi);
                        mBLE_Device.rssi[user_data_count] = rssi;

                        Log.d(TAG, "Dbg__DeviceScan st = 03 ");

                        parseAdvertisementPacket(scanRecord);

                        Log.w(ble_terminal.LOG_TAG, String.format("Dbg__hex = ") + rssi);
                    }
                }
/*    	Advertising (Discoverable) Data format ?м?
BoT    / TX -2  /0123456789 ,       |  020106020a | efbfbe | 04    |  09   | 426f54    | 0eefbfbf0000 | 3031323334353637383930 |
test   / TX  2  /0123456789 ,       |  020106020a |    02  | 05    |  09   | 74657374  | 0eefbfbf0000 | 3031323334353637383900 |
???         /Txlevel  /????? ??????                        |             |Txlevel | ??? ???? |??? TYPE|   ???            |              |          ????? ??????              |
  */

                void parseAdvertisementPacket(final byte[] scanRecord) { //Advertising ??????

                    byte[] advertisedData = Arrays.copyOfRange(scanRecord, 0, scanRecord.length);
                    final StringBuilder stringBuilder = new StringBuilder(advertisedData.length);
                    Log.d(TAG, "Dbg__DeviceScan st = 1 ");

                    for (byte byteChar : advertisedData) {
                        stringBuilder.append((char) byteChar);
                    }

                    byte[] inputBytes = stringBuilder.toString().getBytes();
                    String hexString = "";

                    for (byte b : inputBytes) {
                        hexString += Integer.toString((b & 0xF0) >> 4, 16);
                        hexString += Integer.toString(b & 0x0F, 16);
                    }
                    Log.w(TAG, String.format("Dbg__hex = ") + hexString);

                    mBLE_Device.user_alldata[user_data_count] = stringBuilder.toString();
                    user_data_count++;


                }


            });
            
            
        }
    };
    
//
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView textView_user_data;
        TextView textView_user_value;
        TextView textView_rssi;
        TextView device_tx_level;
        ProgressBar progressBar1;
        ProgressBar progressBar_user_value;
        ImageView imageView_pio10;
        ImageView imageView_pio11;
        LinearLayout linearLayout_user_value;
        
      
    }

    // 디바이스 정보를 위한 클래스 정의
    static class BLE_Device {
    	String[] user_alldata=new String[500];
    	String[] user_data=new String[500];
    	String[] user_TX_level=new String[500];
    	int[] rssi = new int[500];
      
    	
    	 public void setclear() { 
    		 for (int i = 0; i < 500; i++) {
    			 user_alldata[i]="";
    			 user_data[i]="";
    			 user_TX_level[i]="";
    			 rssi[i]=0;
    		 }
    		 
         }
    }
    
}