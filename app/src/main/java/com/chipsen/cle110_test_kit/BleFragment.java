/*

<License>
The program or internal source codes was created by CHIPSEN Co.,Ltd.
In order to use the program or internal source codes, you must buy CHIPSEN's Bluetooth products.
You are not allowed to use it for purposes other than CHIPSEN's Bluetooth Products

Copyright 2015. CHIPSEN all rights reserved.*/


package com.chipsen.cle110_test_kit;

import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.chipsen.bleservice.BluetoothLeService;
import com.chipsen.bleservice.SampleGattAttributes;

import java.util.List;

public abstract class BleFragment extends Fragment{
	private final static String TAG = BleFragment.class.getSimpleName();

	private BluetoothLeService mBluetoothLeService;

	BluetoothGattCharacteristic UART_Read;
	BluetoothGattCharacteristic UART_Write;

    BluetoothGattCharacteristic PWM_Read_Write;

    BluetoothGattCharacteristic PIO_Read_Write;
    BluetoothGattCharacteristic PIO_State;
    BluetoothGattCharacteristic PIO_Direction;

    BluetoothGattCharacteristic AIO_Read;
    

    
    // Code to manage Service lifecycle. BLE���� �� 
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "BleFragment_Service Connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                getActivity().finish();
            }
           
            findCharacteristic();
            setBLEstate();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    
    //BLE ���񽺸� �ڵ鸵 �� �� �ִ� �̺�Ʈ 
    // ACTION_GATT_CONNECTED: GATT���� ����
    // ACTION_GATT_DISCONNECTED: GATT���� ����ȵ�.
    // ACTION_GATT_SERVICES_DISCOVERED: GATT���� ã��
    // ACTION_DATA_AVAILABLE:  �����͸� �аų� BLE �۵��� �˸��� BLE ��ġ���� ������ ���� �� �ִ�. 
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW); 		//�����Ͱ� ������ �б�
            	String data_string = intent.getStringExtra(BluetoothLeService.EXTRA_DATA_STRING);	//�����Ͱ� ��Ʈ������ ��ȯ�ؼ� �б�
            	String uudi_data = intent.getStringExtra(BluetoothLeService.UUID_STRING);	//UUID�� ��Ʈ������ ��ȯ�ؼ� �б�
            	
            	dataReceived(uudi_data,data_string,data);
            	dataReceived1(uudi_data,data_string,data);
            	 
            	
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	//���񽺰� ������ �������̽��� �����Ѵ�.
            	findCharacteristic();
            	setBLEstate();
            	
            }
            
        }
    };
   
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// Start service BLE ���� ����
        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        
        
    }

	
    
	public void findCharacteristic() {
    	// Find BLE112 service for writing to
        List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
        
        if (gattServices == null) return;
        String uuid = null;
       
        // Loops through available GATT Services.
        //������ GATT ���񽺸� ���� UUID�� ã�´�. 
        for (BluetoothGattService gattService : gattServices)
        {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                uuid = gattCharacteristic.getUuid().toString();
                if (SampleGattAttributes.UART_READ_UUID.equals(uuid)){UART_Read = gattCharacteristic;
                	mBluetoothLeService.setCharacteristicNotification( UART_Read, true);// Notification on ����
                }
                if (SampleGattAttributes.UART_WRITE_UUID.equals(uuid)) UART_Write = gattCharacteristic;
                if (SampleGattAttributes.PIO_READ_WRITE_UUID.equals(uuid))	PIO_Read_Write = gattCharacteristic;
                if (SampleGattAttributes.PWM_READ_WRITE_UUID.equals(uuid))	PWM_Read_Write = gattCharacteristic;
                if (SampleGattAttributes.PIO_DIRECTION_UUID.equals(uuid))	PIO_Direction = gattCharacteristic;
                if (SampleGattAttributes.PIO_STATE_UUID.equals(uuid))	PIO_State = gattCharacteristic;
                if (SampleGattAttributes.AIO_READ_UUID.equals(uuid))	AIO_Read = gattCharacteristic;
            }
        }
	}
	

	
	public void findCharacteristic2() {
    	// Find BLE112 service for writing to
        List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
        
        if (gattServices == null) return;
        String uuid = null;
       
        // Loops through available GATT Services.
        //������ GATT ���񽺸� ���� UUID�� ã�´�. 
        for (BluetoothGattService gattService : gattServices)
        {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                uuid = gattCharacteristic.getUuid().toString();
                if (SampleGattAttributes.PIO_READ_WRITE_UUID.equals(uuid))
                {
                	PIO_Read_Write = gattCharacteristic;
                	
                }
              
            }
        }
	}
	//BLE ��ġ���� ������ �ޱ� (����Ʈ�ڵ�)
	public void dataReceived(String  uuid, String data,byte[] row_data){}
	
	//BLE ��ġ���� ������ �ޱ� (����Ʈ�ڵ�)
	public void dataReceived1(String  uuid, String data,byte[] row_data){}

	//BLE ��ġ���� ������ �ޱ� (����Ʈ�ڵ�)
	public void setBLEstate(){}
    //������ ������
    public void sendData(byte[] data) {}
    
    //PIO ���°� ����
    public void setPIO_State(byte[] data) {	
    	if (PIO_State != null) {
    		mBluetoothLeService.writeCharacteristic(PIO_State, data);
        }
    }
    //PIO IO ���� Ȯ��
    public void setPIO_Direction(byte[] data) {
    	if (PIO_Direction != null) {
    		mBluetoothLeService.writeCharacteristic(PIO_Direction, data);
        }
    }
    //PWM �� ����
    public void setPWM_Write(byte[] data) {
    	if (PWM_Read_Write != null) {
    		mBluetoothLeService.writeCharacteristic(PWM_Read_Write, data);
        }
    }
    //UART �� ������
    public void setUART_Write(byte[] data) {
    	if (UART_Write != null) {
    		mBluetoothLeService.writeCharacteristic_NO_RESPONSE(UART_Write, data);
        }
    }
    //PWM �� �о����
    public void getPWM_Read() {
    	if (PWM_Read_Write != null) {
        	mBluetoothLeService.readCharacteristic(PWM_Read_Write);
        }
    }
    //PIO ���°� �б�
    public void getPIO_Read_Write() {
    	if (PIO_Read_Write != null) {
        	mBluetoothLeService.readCharacteristic(PIO_Read_Write);
        }
    }
    // PIO ���°� �б�
    public void getPIO_State() {
    	if (PIO_State != null) {
        	mBluetoothLeService.readCharacteristic(PIO_State);
        }
    }
    // PIO IO������ �б�
    public void getPIO_Direction() {
    	if (PIO_Direction != null) {
    		mBluetoothLeService.readCharacteristic(PIO_Direction);
        }
    }
    // AIO�� �б�
    public void getAIO_Read() {
    	if (AIO_Read != null) {
        	mBluetoothLeService.readCharacteristic(AIO_Read);
        }
    }

    @Override
	public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        
    }

    @Override
	public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
        mBluetoothLeService = null;
      
    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }    
    
    
}
