/*<저작권>
본 프로그램 및 소스코드는 (주)칩센의 저작물입니다.
본 프로그램 및 소스코드는 (주)칩센의 블루투스 제품을 구입한 고객에게 제공되는 것 입니다.
당사의 블루투스 제품을 활용할 목적 이외의 용도로 사용하는 것을 금지합니다.

<License>
The program or internal source codes was created by CHIPSEN Co.,Ltd.
In order to use the program or internal source codes, you must buy CHIPSEN's Bluetooth products.
You are not allowed to use it for purposes other than CHIPSEN's Bluetooth Products

Copyright 2015. CHIPSEN all rights reserved.*/


package com.chipsen.cle110_test_kit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.chipsen.bleservice.BluetoothLeService;

import java.util.List;


//처음 실행시 나타나는 엑티비티
public class NavigationActivity extends Activity {
    private final static String TAG = NavigationActivity.class.getSimpleName();


	public static boolean bWriteEcho = false;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    static final int PICK_DEVICE_REQUEST = 0;

	private static final String LOG_TAG = null;
    private String mDeviceName;
    private String mDeviceAddress;
    
    public static  boolean isConnected = false;
    
    public static LinearLayout title_bar;
    
    public static Context Mcontext;
    
	ImageView imageView_data_mode;
	ImageView imageView_service_mode;
	ImageView imageView_search;


   // Menu m_menu;
    
    private BluetoothLeService mBluetoothLeService;
        
    public static  final String[] fragments ={
    		"com.chipsen.cle110_test_kit.LedContorlFragment",
    		"com.chipsen.cle110_test_kit.ble_terminal",
            };
    public static InputMethodManager mInputManager;
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            // 서비스에 연결됨...
            Log.d(TAG, "Navig_Service Connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Navig_Unable to initialize Bluetooth");
                finish();
            }

            //
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // 브로드캐스트 리시버 서비스에 연결되면 알림...
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "Dbg_Navig14");

            final String action = intent.getAction();
            Log.e(TAG, "Dbg_Navig_mGattUpdateReceiver:" + action);
            
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            	isConnected = true;
                // 이미지 설정...
            	imageView_search.setImageResource(R.drawable.search_f);
            
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            	isConnected = false;
                // 이미지 설정...
            	imageView_search.setImageResource(R.drawable.search);
            	
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.

            	//서비스를 제공하고 있는지를 보여준다.
                discoveredGattServices(mBluetoothLeService.getSupportedGattServices());
                discoveredGattServices2(mBluetoothLeService.getSupportedGattServices());
            }
            
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 Log.d(TAG, "park= onCreate0");

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Log.d(TAG, "park= onCreate1");
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	    // 레이아웃 구성
		setContentView(R.layout.activity_navigation);
		Log.d(TAG, "park= onCreate2");

	     mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	     Log.d(TAG, "park= onCreate3");


        Log.e(LOG_TAG, "parkNavigationActivity =+++ ON CREATE +++1_1");
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,	R.layout.custom_title);
        Mcontext = this;

        Display_loop();

        // 처음 실핼될때...
        if (savedInstanceState == null) {
        	Log.e(LOG_TAG, "parkNavigationActivity =+++ ON CREATE +++2");
            selectDemo(0);
            Log.e(LOG_TAG, "parkNavigationActivity =+++ ON CREATE +++2_1");
            
        }
        Log.e(LOG_TAG, "parkNavigationActivity =+++ ON CREATE +++2_2");

    	// Start service
        // 블루투스 서비스가 시작된다.(폰에서 자동으로 블루투스를 켜고 시작한다.)
        // 없으면 앱이 실행되지 않는다.
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Log.e(LOG_TAG, "parkNavigationActivity =+++ ON CREATE +++3");

        // 서비스에 바인딩하기...
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.e(LOG_TAG, "parkNavigationActivity =+++ ON CREATE +++4");

//
	}

	//
	private void Display_loop() { // 
		 title_bar =(LinearLayout) findViewById(R.id.title_bar);

		 imageView_data_mode=(ImageView)findViewById(R.id.imageView_data_mode); 
		 imageView_service_mode=(ImageView)findViewById(R.id.imageView_service_mode); 
		 imageView_search=(ImageView)findViewById(R.id.imageView_search); 


		 imageView_data_mode.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					selectDemo(1);	
				}
			});
		 imageView_service_mode.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectDemo(0);
				}
			});
		 imageView_search.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 검색 메뉴 선택 : 블루투스 검색하기...
					bluetoothButton();
				}
			});
		 
		 
		 
		 
	}
	
	@SuppressWarnings("unused")
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @SuppressWarnings("rawtypes")
		@Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectDemo(position);
	    }
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }
	
	/** Swaps fragments in the main content view */
	private void selectDemo(int position) {

        // 데모 선택
		if(position==0){
			imageView_data_mode.setImageResource(R.drawable.top_terminal_off);
			imageView_service_mode.setImageResource(R.drawable.top_io_on);
		}
		if(position==1){
			imageView_data_mode.setImageResource(R.drawable.top_terminal_on);
			imageView_service_mode.setImageResource(R.drawable.top_io_off);
		}
		
		
			
	    // Create a new fragment and specify the planet to show based on position
		Fragment fragment = Fragment.instantiate(NavigationActivity.this, fragments[position]);

	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_DEVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
            	mDeviceName = data.getStringExtra(EXTRAS_DEVICE_NAME);
            	mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            	Toast.makeText(this, "디바이스 이름" + ": "+mDeviceName +" - "+ mDeviceAddress + "\n연결되었습니다.", Toast.LENGTH_LONG).show();
            }
        }
	}
	
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
	private void discoveredGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices)
        {
            uuid = gattService.getUuid().toString();

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                uuid = gattCharacteristic.getUuid().toString();
            }
        }
    }
	
	private void discoveredGattServices2(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices)
        {
            uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                uuid = gattCharacteristic.getUuid().toString();
            }
        }
    }

    // 검색 버튼 핸들러
	private void bluetoothButton() {
		if (isConnected) {
			mBluetoothLeService.disconnect();
		}
		else {
			// 스캔 액티비티로 이동...
			Intent intent = new Intent(this, DeviceScanActivity.class);
		    startActivityForResult(intent,PICK_DEVICE_REQUEST);
		}
	}

		
    @Override
    protected void onResume() {
        super.onResume();

        // 브로드캐스트 리시버 등록하기
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        // 시작할 때는 널이고 선택하고 오면 널이 아님...
        if (mBluetoothLeService != null) {

            // 선택된 디바이스로 연결...
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 브로드캐스트 리시버 삭제
        unregisterReceiver(mGattUpdateReceiver);
      
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 서비스 연결 종료.
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

        // 프로세스를 완전히..??
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // 브로드캐스트 리시버를 위한 필터...
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }
}

