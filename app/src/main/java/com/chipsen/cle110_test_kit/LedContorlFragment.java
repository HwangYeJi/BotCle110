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
import org.apache.http.impl.conn.DefaultClientConnection;

import com.chipsen.bleservice.BluetoothLeService;
import com.chipsen.bleservice.SampleGattAttributes;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class LedContorlFragment extends BleFragment{
	private final static String TAG = NavigationActivity.class.getSimpleName();
	
	boolean Test_Start=false; //TEST MODE
    public TextView data_txt;

    boolean isconncet=false;
    boolean mTestHandler_finish=true;

	TextView mtextView1;
	
	TextView mtextView_AIO0;
	TextView mtextView_AIO1;
	TextView mtextView_AIO2;
	
	TextView mtextView_PWM0;
	TextView mtextView_PWM1;
	TextView mtextView_PWM2;
	TextView mtextView_PWM3;
	
	LinearLayout linearlayout_text;
	LinearLayout linearLayout_test_view;

	boolean PIOIO_10=false; // PIO 10�? ?��출력  ?��?�� true = out, false = in 
	boolean PIOIO_11=false; // PIO 11�?  ?��출력 ?��?�� true = out, false = in

	
	Button mbutton1;
	Button mbutton2;
	
	ImageView mimageView_pio10;
	ImageView mimageView_pio11;
	
	ImageView imageView_AIO0;
	ImageView imageView_AIO1;
	ImageView imageView_AIO2;
	
	ImageView imageView_r_min;
	ImageView imageView_r_max;
	
	ImageView imageView_g_min;
	ImageView imageView_g_max;

	ImageView imageView_b_min;
	ImageView imageView_b_max;
	ImageView imageView_m_min;
	ImageView imageView_m_max;

	
	ImageView imageView_test_bt;
	ImageView imageView_test_in_uart;
	ImageView imageView_test_ex_uart;
	
	boolean imageView_test_bt_bool=false;
	int time_test_count=0;
	
	public SeekBar mseekBar_PWM0;
	public SeekBar mseekBar_PWM1;
	public SeekBar mseekBar_PWM2;
	public SeekBar mseekBar_PWM3;
	
	boolean SeekBar_change_bool=false;
	
	
	boolean PIOHiLow_10=false; // PIO 10�? 출력 ?��?�� true = High, false = Low 
	boolean PIOHiLow_11=false; // PIO 11�?  출력 ?��?�� true = High, false = Low
	
	
	boolean UART_Read_check=false;
	boolean PIO_Read_Write_check=false;
	boolean PIO_State_check=false;
	boolean PIO_Direction_check=false;
	boolean AIO_Read_check=false;
	boolean PWM_Read_check=false;

	boolean PIO_State_run=false;
	boolean PIO_Direction_run=false;
	boolean AIO_Read_run=false;
	boolean PWM_Read_run=false;
	
	boolean setPIO_STATE_loop_run=false;
	boolean setPIO_DIRECTION_loop_run=false;
	boolean setPWM_Read_Write_loop_run=false;
	int time_schedule=0;

	
    public static BluetoothGattCharacteristic mSCharacteristic;	
    SampleGattAttributes mSampleGattAttributes;
	public static final String LOG_TAG = "BlueTerm";
	
	boolean AIO_0_select=false; // 온도 센서 표시, 전압 표시 선택
	boolean AIO_1_select=false; // 빛 센서 표시, 전압 표시 선택
	boolean AIO_2_select=false; // 저항값 표시, 전압 표시 선택
	//온도
	double temp_fix[]={1.5614f,1.5345f,1.5061f,1.4759f,1.4438f,1.4096f,1.3821f,1.3532f,1.3227f,1.2906f,1.2567f,1.2293f,
			1.2006f,1.1707f,1.1393f,1.1071f,1.0808f,1.0536f,1.0252f,0.9957f,0.965f,0.9403f,0.9149f,0.8886f,0.8614f,0.8333f,
			0.8108f,0.7876f,0.7637f,0.7392f,0.714f,0.6938f,0.6731f,0.652f,0.6303f,0.6081f,0.5903f,0.5672f,0.5435f,0.5192f,
			0.5155f,0.5f,0.4843f,0.4683f,0.4521f,0.4356f,0.4223f,0.4088f,0.3951f,0.3813f,0.3673f,0.3559f,0.3445f,0.3329f,
			0.3212f,0.3094f,0.2998f,0.2902f,0.2804f,0.2706f,0.2607f,0.2526f,0.2445f,0.2363f,0.228f,0.2197f,0.2129f,0.2061f,
			0.1992f,0.1992f,0.1854f,0.1797f,0.174f,0.1682f,0.1625f,0.1567f,0.1519f,0.1471f,0.1423f,0.1375f,0.1327f,0.1287f,
			0.1247f,0.1207f,0.1167f,0.1127f,0.1093f,0.106f,0.1026f,0.0992f,0.0958f};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
        // Inflate the layout for this fragment fragment ?��?��?��?��?���? 보여주기
		View v = inflater.inflate(R.layout.fragment_led_contorl, container, false);
		Log.d(TAG, "park LedContorlFragment = onCreate0");

		Display_loop(v);
		
		mDisgetHandler.sendEmptyMessageDelayed(0, 100); // ?�람 ?�행
		
        return v;
        
    } 
	
 @Override
	public void onPause() {
        super.onPause();
        

	
	        
    }
 @Override
		public void onResume() {
	        super.onResume();
	        if(Test_Start){
	        	imageView_test_bt.setImageResource(R.drawable.test_uart_off);
	   			imageView_test_in_uart.setImageResource(R.drawable.in_uart_off);
	   			imageView_test_ex_uart.setImageResource(R.drawable.ex_uart_off);
				linearLayout_test_view.setVisibility(View.VISIBLE);
				imageView_test_bt_bool=false;
				
			}else{
				linearLayout_test_view.setVisibility(View.INVISIBLE);
				imageView_test_bt_bool=true;
				time_test_count=0;
			}
	    }
	private void Display_loop(View v) { // 
		
		
		mtextView_AIO0=(TextView)v.findViewById(R.id.textView_AIO0);
		mtextView_AIO1=(TextView)v.findViewById(R.id.textView_AIO1);
		mtextView_AIO2=(TextView)v.findViewById(R.id.textView_AIO2);
		mtextView_PWM0=(TextView)v.findViewById(R.id.textView_PWM0);
		mtextView_PWM1=(TextView)v.findViewById(R.id.textView_PWM1);
		mtextView_PWM2=(TextView)v.findViewById(R.id.textView_PWM2);
		mtextView_PWM3=(TextView)v.findViewById(R.id.textView_PWM3);

		mbutton1 =(Button)v.findViewById(R.id.button1);
		mbutton2 =(Button)v.findViewById(R.id.button2);
		mimageView_pio10=(ImageView)v.findViewById(R.id.imageView_pio10); //?�� 초록?��?��미�?       
		mimageView_pio11=(ImageView)v.findViewById(R.id.imageView_pio11); //?�� 초록?��?��미�?
		linearlayout_text =(LinearLayout) v.findViewById(R.id.linearlayout_text);
		linearLayout_test_view =(LinearLayout) v.findViewById(R.id.linearLayout_test_view);
		
		imageView_AIO0=(ImageView)v.findViewById(R.id.imageView_AIO0);
		imageView_AIO1=(ImageView)v.findViewById(R.id.imageView_AIO1);
		imageView_AIO2=(ImageView)v.findViewById(R.id.imageView_AIO2);
		
		imageView_r_min=(ImageView)v.findViewById(R.id.imageView_r_min);
		imageView_r_max=(ImageView)v.findViewById(R.id.imageView_r_max);
		
		imageView_g_min=(ImageView)v.findViewById(R.id.imageView_g_min);
		imageView_g_max=(ImageView)v.findViewById(R.id.imageView_g_max);

		imageView_b_min=(ImageView)v.findViewById(R.id.imageView_b_min);
		imageView_b_max=(ImageView)v.findViewById(R.id.imageView_b_max);
		imageView_m_min=(ImageView)v.findViewById(R.id.imageView_m_min);
		imageView_m_max=(ImageView)v.findViewById(R.id.imageView_m_max);
		
		
		imageView_test_bt=(ImageView)v.findViewById(R.id.imageView_test_bt);
		imageView_test_in_uart=(ImageView)v.findViewById(R.id.imageView_test_in_uart);
		imageView_test_ex_uart=(ImageView)v.findViewById(R.id.imageView_test_ex_uart);

		if(Test_Start){
			linearLayout_test_view.setVisibility(View.VISIBLE);
			imageView_test_bt_bool=false;
			time_test_count=0;
			mTestHandler.sendEmptyMessageDelayed(0, 1000); //
		}else{
			linearLayout_test_view.setVisibility(View.INVISIBLE);
			imageView_test_bt_bool=true;
			time_test_count=0;
		}
		
		imageView_test_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(imageView_test_bt_bool){
					imageView_test_bt.setImageResource(R.drawable.test_uart_off);
					imageView_test_bt_bool=false;
					time_test_count=1;
					
				}else{
					imageView_test_bt.setImageResource(R.drawable.test_uart_on);
					imageView_test_bt_bool=true;
					time_test_count=0;
				}
				
			}
		});
		

		
		imageView_r_min.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM0.setProgress(0);
				setPWM_Write_loop();
			}
		});
		imageView_r_max.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM0.setProgress(255);
				setPWM_Write_loop();
			}
		});
		
		imageView_g_min.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM1.setProgress(0);
				setPWM_Write_loop();
			}
		});
		imageView_g_max.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM1.setProgress(255);
				setPWM_Write_loop();
			}
		});
		imageView_b_min.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM2.setProgress(0);
				setPWM_Write_loop();
			}
		});
		imageView_b_max.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM2.setProgress(255);
				setPWM_Write_loop();
			}
		});
		imageView_m_min.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM3.setProgress(0);
				setPWM_Write_loop();
			}
		});
		imageView_m_max.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mseekBar_PWM3.setProgress(255);
				setPWM_Write_loop();
			}
		});
		
		
		imageView_AIO0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AIO_0_select){
					AIO_0_select=false;
					imageView_AIO0.setImageResource(R.drawable.analog_temperature_small );
				}else{
					AIO_0_select=true;
					imageView_AIO0.setImageResource(R.drawable.analog_voltae_small);
				}
				
			}
		});
				
		imageView_AIO1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(AIO_1_select){
							AIO_1_select=false;
							imageView_AIO1.setImageResource(R.drawable.analog_light_small );
						}else{
							AIO_1_select=true;
							imageView_AIO1.setImageResource(R.drawable.analog_voltae_small);
						}
						
					}
				});
				
		imageView_AIO2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AIO_2_select){
					AIO_2_select=false;
					imageView_AIO2.setImageResource(R.drawable.analog_resistance_small );
				}else{
					AIO_2_select=true;
					imageView_AIO2.setImageResource(R.drawable.analog_voltae_small);
				}
				
			}
		});

		
		
		
		
		mimageView_pio10.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(!PIOIO_10){
					PIOIO_10=true;
					mimageView_pio10.setImageResource(R.drawable.gpio_out);
				}else{
					PIOIO_10=false;
					mimageView_pio10.setImageResource(R.drawable.gpio_in);
				}
				
				setPIO_DIRECTION_loop();
				
			}
		});
		mimageView_pio11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!PIOIO_11){
					PIOIO_11=true;	
					mimageView_pio11.setImageResource(R.drawable.gpio_out);
				}else{
					PIOIO_11=false;
					mimageView_pio11.setImageResource(R.drawable.gpio_in);
				}
				setPIO_DIRECTION_loop();
			}
		});
		
		
		mbutton1.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("Wakelock")
			public void onClick(View v) {
				if(PIOHiLow_10){
					PIOHiLow_10=false;
				}else{
					PIOHiLow_10=true;
				}
				setPIO_STATE_loop_run=true;
				setPIO_STATE_loop();
			}
		});
		mbutton2.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("Wakelock")
			public void onClick(View v) {
				if(PIOHiLow_11){
					PIOHiLow_11=false;
				}else{
					PIOHiLow_11=true;
				}
				setPIO_STATE_loop_run=true;
				setPIO_STATE_loop();
			}
		});
		mseekBar_PWM0=(SeekBar)v.findViewById(R.id.seekBar_PWM0);
		mseekBar_PWM1=(SeekBar)v.findViewById(R.id.seekBar_PWM1);
		mseekBar_PWM2=(SeekBar)v.findViewById(R.id.seekBar_PWM2);
		mseekBar_PWM3=(SeekBar)v.findViewById(R.id.seekBar_PWM3);
		mseekBar_PWM0.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=false;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=true;   
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                 // TODO Auto-generated method stub
            	if(SeekBar_change_bool)setPWM_Write_loop();
            }	
       });
        
		mseekBar_PWM1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=false;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=true;   
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                 // TODO Auto-generated method stub
            	if(SeekBar_change_bool)setPWM_Write_loop();
            }
       });
		mseekBar_PWM2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=false;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=true;   
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                 // TODO Auto-generated method stub
            	if(SeekBar_change_bool)setPWM_Write_loop();
            }
       });
		mseekBar_PWM3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=false;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub
            	SeekBar_change_bool=true;   
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                 // TODO Auto-generated method stub
            	if(SeekBar_change_bool)setPWM_Write_loop();
            }
       });
	}
	
	
	private final Handler mDisgetHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			Log.d(TAG, "park LedContorlFragment = onCreate-- "+linearlayout_text.getHeight());
			mtextView_AIO0.setTextSize(TypedValue.COMPLEX_UNIT_PX, linearlayout_text.getHeight()/2.5f);
			mtextView_AIO1.setTextSize(TypedValue.COMPLEX_UNIT_PX, linearlayout_text.getHeight()/2.5f);
			mtextView_AIO2.setTextSize(TypedValue.COMPLEX_UNIT_PX, linearlayout_text.getHeight()/2.5f);
		}
	};
	
	public void dataReceived1(String  uudi_data, String data_string,byte[] row_data){ 
		// TODO Auto-generated method stub
		if(Test_Start){
			if(SampleGattAttributes.UART_READ_UUID.equals(uudi_data)){
				String str2 = new String(row_data);
				if(str2.equals("IN_PORT_OK")){
					imageView_test_in_uart.setImageResource(R.drawable.in_uart_on);
				}
				if(str2.equals("EX_PORT_OK")){
					imageView_test_ex_uart.setImageResource(R.drawable.ex_uart_on);
				}
				Log.d(TAG, "park LedContorlFragmentdataReceived1 = "+str2);
			}
		}
		
	}
	private final Handler mTestHandler = new Handler() {
		int delay_count=0;
		@Override
		public void handleMessage(Message msg) {
			
			if(NavigationActivity.isConnected==true){
				
				if(isconncet==false){
					isconncet=true;
					time_test_count=1;
				}
			}else{
				isconncet=false;
				time_test_count=0;
			}
			
			if(time_test_count==1){
				String strbuff="Test_ACK\r";
				setUART_Write(strbuff.getBytes());
			}
			switch (time_test_count) {
			case 1: 
				
				mseekBar_PWM0.setProgress(255);
				setPWM_Write_loop();
				time_test_count=2;
				break;
			case 2: 
				mseekBar_PWM1.setProgress(255);
				setPWM_Write_loop();
				time_test_count=3;
				break;

			case 3: 
				mseekBar_PWM2.setProgress(255);
				setPWM_Write_loop();
				time_test_count=4;
				break;
			case 4: 
				mseekBar_PWM3.setProgress(150);
				setPWM_Write_loop();
				time_test_count=5;
				break;

			case 5: 
				mseekBar_PWM0.setProgress(0);
				setPWM_Write_loop();
				time_test_count=6;
				break;
			case 6: 
				mseekBar_PWM1.setProgress(0);
				setPWM_Write_loop();
				time_test_count=7;
				break;

			case 7: 
				mseekBar_PWM2.setProgress(0);
				setPWM_Write_loop();
				time_test_count=8;
				break;
			case 8: 
				mseekBar_PWM3.setProgress(0);
				setPWM_Write_loop();
				time_test_count=9;
				break;

			case 9: 
				time_test_count=1;
				break;

			}
			
			if(mTestHandler_finish){
			mTestHandler.sendEmptyMessageDelayed(0, 400); //
			}
		}
	};
	private final Handler mStateHandler = new Handler() {
		int delay_count=0;
		@Override
		public void handleMessage(Message msg) {
			
			switch (time_schedule) {
			case 1: // PIO ?��출력 ?��?��
				if(PIO_Direction_run){
					PIO_Direction_check=false;
					delay_count=0;
					getPIO_Direction();
					time_schedule=2;
				}else{
					time_schedule=21;
				}
			case 2:
				if(PIO_Direction_check){
					PIO_Direction_check=false;
					PIO_Direction_run=false;
					time_schedule=21;
				}else{
					delay_count++;
					if(delay_count>=30){
						PIO_Direction_run=false;
						time_schedule=21;
					}
				}
			break;
			
			case 21: //PIO 
					if(PIO_State_run){
						PIO_State_check=false;
						delay_count=0;
						getPIO_State();
						time_schedule=22;
					}else{
						time_schedule=31;
					}
			break;
			case 22:
				if(PIO_State_check){
					PIO_State_check=false;
					time_schedule=31;
				}else{
					delay_count++;
					if(delay_count>=30){
						time_schedule=31;
					}
				}
			break;
			case 31://AIO ?��?�� ?��?��
				if(AIO_Read_run){
					AIO_Read_check=false;
					delay_count=0;
					getAIO_Read();
					time_schedule=32;
				}else{
					time_schedule=41;
				}
			break;
			case 32: //AIO ?��?�� ?��?��
				if(AIO_Read_check){
					AIO_Read_check=false;
					time_schedule=41;
				}else{
					delay_count++;
					if(delay_count>=30){
						time_schedule=41;
					}
				}
				
			break;
			case 41://PWM ?��?�� ?��?��
				if(PWM_Read_run){
					PWM_Read_check=false;
					delay_count=0;
					getPWM_Read();
					time_schedule=42;
				}else{
					time_schedule=100;
				}
				
			break;
			case 42: //PWM ?��?�� ?��?��
				if(PWM_Read_check){
					PWM_Read_check=false;
					time_schedule=100;
				}else{
					delay_count++;
					if(delay_count>=30){
						time_schedule=1;
					}
				}
			break;
			case 100: 
			time_schedule=1;
			break;
			case 500:
			break;

			}
			mStateHandler.sendEmptyMessageDelayed(0, 200); // 200ms 반복 ?��?��
		}
	};

	//PIO 방향�? ?��?��
	void setPIO_DIRECTION_loop() {
			 byte[] pio_data0 = {(byte) 0x01,  (byte) 0xEC };
			 byte[] pio_data1 = {(byte) 0x05,  (byte) 0xEC };
			 byte[] pio_data2 = {(byte) 0x09,  (byte) 0xEC };
			 byte[] pio_data3 = {(byte) 0x0D,  (byte) 0xEC };
	
			 if((!PIOIO_10)&(!PIOIO_11)){
				 setPIO_Direction(pio_data0);
				 mbutton1.setEnabled(false);
	 			 mbutton2.setEnabled(false);}
			 else if((PIOIO_10)&(!PIOIO_11)){
				 setPIO_Direction(pio_data1);
				 mbutton1.setEnabled(true);
				 mbutton2.setEnabled(false);}
			 else if((!PIOIO_10)&(PIOIO_11)){
				 setPIO_Direction(pio_data2);
				 mbutton1.setEnabled(false);
	 			 mbutton2.setEnabled(true);}
			 else if((PIOIO_10)&(PIOIO_11)){
				 setPIO_Direction(pio_data3);
				 mbutton1.setEnabled(true);
	 			 mbutton2.setEnabled(true);}
			 try {
					Thread.sleep(100);
				} catch (Exception ex) {
				}
			 
		}
	// PWM 출력
	 void setPWM_Write_loop() { 
		 byte[] PWM_Write_byte = new byte[4];
		 PWM_Write_byte[0]= (byte) (mseekBar_PWM3.getProgress() >>> 0 & 0xff);
		 PWM_Write_byte[1]= (byte) (mseekBar_PWM2.getProgress() >>> 0 & 0xff);
		 PWM_Write_byte[2]= (byte) (mseekBar_PWM1.getProgress() >>> 0 & 0xff);
		 PWM_Write_byte[3]= (byte) (mseekBar_PWM0.getProgress() >>> 0 & 0xff);
		 
		 mtextView_PWM0.setText(String.valueOf(mseekBar_PWM0.getProgress()));
		 mtextView_PWM1.setText(String.valueOf(mseekBar_PWM1.getProgress()));
		 mtextView_PWM2.setText(String.valueOf(mseekBar_PWM2.getProgress()));
		 mtextView_PWM3.setText(String.valueOf(mseekBar_PWM3.getProgress()));
		 setPWM_Write(PWM_Write_byte);
		 try {
				Thread.sleep(50);
			} catch (Exception ex) {
			}
		 
	}
	// ?��출력 방향 ?��?��
	 void setPIO_STATE_loop() { 
		 
		 byte[] pio_data0 = {(byte) 0x00,  (byte) 0x1B };
		 byte[] pio_data1 = {(byte) 0x04,  (byte) 0x1B };
		 byte[] pio_data2 = {(byte) 0x08,  (byte) 0x1B };
		 byte[] pio_data3 = {(byte) 0x0C,  (byte) 0x1B };
		 
		 if((!PIOHiLow_10)&(!PIOHiLow_11)){
			 setPIO_State(pio_data0);
			 if(mbutton1.isEnabled())mbutton1.setBackgroundResource(R.drawable.gpio_low_on);
			 if(mbutton2.isEnabled())mbutton2.setBackgroundResource(R.drawable.gpio_low_on);
			 
			 }
		 else if((PIOHiLow_10)&(!PIOHiLow_11)){
			 setPIO_State(pio_data1);
			 if(mbutton1.isEnabled())mbutton1.setBackgroundResource(R.drawable.gpio_high_on);
			 if(mbutton2.isEnabled())mbutton2.setBackgroundResource(R.drawable.gpio_low_on);
			}
		 else if((!PIOHiLow_10)&(PIOHiLow_11)){
			 setPIO_State(pio_data2);
			 if(mbutton1.isEnabled())mbutton1.setBackgroundResource(R.drawable.gpio_low_on);
			 if(mbutton2.isEnabled())mbutton2.setBackgroundResource(R.drawable.gpio_high_on);
 			
			 }
		 else if((PIOHiLow_10)&(PIOHiLow_11)){
			 setPIO_State(pio_data3);
			 if(mbutton1.isEnabled())mbutton1.setBackgroundResource(R.drawable.gpio_high_on);
			 if(mbutton2.isEnabled())mbutton2.setBackgroundResource(R.drawable.gpio_high_on);
			}
		 try {
				Thread.sleep(50);
			} catch (Exception ex) {
			}
	}
		
	
	//BLE?��치에?�� ?��?��?�� 받기
	@Override
	public void dataReceived(String  uudi_data, String data_string,byte[] row_data){
		if(SampleGattAttributes.PIO_READ_WRITE_UUID.equals(uudi_data)){
			PIO_Read_Write_check=true;
		}else if(SampleGattAttributes.PWM_READ_WRITE_UUID.equals(uudi_data)){
			PWM_Read_check=true;
			PWM_READ_loop(row_data);
		}else if(SampleGattAttributes.PIO_DIRECTION_UUID.equals(uudi_data)){
			PIO_Direction_check=true;
			PIO_DIRECTION_loop(data_string);
		}else if(SampleGattAttributes.PIO_STATE_UUID.equals(uudi_data)){
			PIO_State_check=true;
			PIO_STATE_loop(data_string);
			
		}else if(SampleGattAttributes.AIO_READ_UUID.equals(uudi_data)){
			AIO_Read_check=true;
			AIO_READ_loop(row_data);
		}
	}
	//BLE state read
	public void setBLEstate() {	
		PIO_Direction_run=true; //PIO ?��출력 ?��?�� ?��?��
		PIO_State_run=true;		//PIO High/Low ?��?��
		AIO_Read_run=true;		//AIO ?��?���? ?��?��
		PWM_Read_run=true;		//PWM ?��?���? ?��?��
		time_schedule=1;
		mStateHandler.sendEmptyMessageDelayed(0, 200);
	}
	// PWM ?��?���? ?���?
	void PWM_READ_loop (byte[] byte_data) {	
		 int mPWM3_int = byte_data[0] & 0xFF;
		 int mPWM2_int = byte_data[1] & 0xFF;
		 int mPWM1_int = byte_data[2] & 0xFF;
		 int mPWM0_int = byte_data[3] & 0xFF;
		 mseekBar_PWM0.setProgress(mPWM0_int);
		 mseekBar_PWM1.setProgress(mPWM1_int);
		 mseekBar_PWM2.setProgress(mPWM2_int);
		 mseekBar_PWM3.setProgress(mPWM3_int);
		 mtextView_PWM0.setText(String.valueOf(mPWM0_int));
		 mtextView_PWM1.setText(String.valueOf(mPWM1_int));
		 mtextView_PWM2.setText(String.valueOf(mPWM2_int));
		 mtextView_PWM3.setText(String.valueOf(mPWM3_int));
	    }
	//AIO ?��?�� �? ?���?
	 void AIO_READ_loop (byte[] byte_data) {	
		 
		 byte[] mAIO2_byte = new byte[2];
		 mAIO2_byte[0]=byte_data[0];
		 mAIO2_byte[1]=byte_data[1];
		 byte[] mAIO1_byte = new byte[2];
		 mAIO1_byte[0]=byte_data[2];
		 mAIO1_byte[1]=byte_data[3];
		 byte[] mAIO0_byte = new byte[2];
		 mAIO0_byte[0]=byte_data[4];
		 mAIO0_byte[1]=byte_data[5];
		
		 
		 String str_AIO0= String.format("%.2f" , byte2Int(mAIO0_byte)*0.001)+"V"; //AIO 0 HEX -> int -> string �??��
		 String str_AIO1= String.format("%.2f" , byte2Int(mAIO1_byte)*0.001)+"V";
		 String str_AIO2= String.format("%.2f" , byte2Int(mAIO2_byte)*0.001)+"V";
		 
		 
		 if(AIO_0_select){
			 mtextView_AIO0.setText(str_AIO0);
		 }else{
			 AIO_0_temp(byte2Int(mAIO0_byte)*0.001);
		 }
		if(AIO_1_select){
			mtextView_AIO1.setText(str_AIO1);
		}else{
			AIO_1_light(byte2Int(mAIO1_byte)*0.001);
		}
		
		if(AIO_2_select){
			mtextView_AIO2.setText(str_AIO2);
		}else{
			AIO_2_reg(byte2Int(mAIO2_byte)*0.0001);
		}
	 }

	 void AIO_2_reg(double volt) { //빛 센서 표시
		 double temp0=(volt/1.26*100);
		 mtextView_AIO2.setText(String.format("%.1f"+"㏀" ,temp0));
	 }

	 void AIO_1_light(double volt) { //빛 센서 표시
		 double temp0=100-(volt/1.280*100);
		 mtextView_AIO1.setText((int)temp0+"%");
	 }

	 void AIO_0_temp(double volt) { //온도 센서 표시
		 
		 
		 for (int i = 0; i <= temp_fix.length-2; i++) {
			 
			 if((temp_fix[i]>=volt)&(volt>=temp_fix[i+1])){
				 double temp0 = temp_fix[i]-temp_fix[i+1];
				 double temp1 = temp_fix[i]-volt;
				 double temp2 =  temp1/temp0*10;
				 int tempint = (int)temp2; 
				 mtextView_AIO0.setText(i+"."+tempint+"˚");
				 break;
			 }
		 }
	 }
	 
	 public static int byte2Int(byte[] src) {
	        int s1 = src[0] & 0xFF;
	        int s2 = src[1] & 0xFF;
	        return ((s1 << 8) + (s2 << 0));
	}
	 
	//PIO ?��?���? ?��?��
	 void PIO_STATE_loop (String data) {	 
	    	String str_buff = data.substring(0, 2);
    	switch(str_buff){
    		case "00": //PIO (0000) 11:0,10:0 (0=LOW,1=HIGH)
    		
    			PIOHiLow_10=false;
    			PIOHiLow_11=false;
    			if(mbutton1.isEnabled()){
    				 mbutton1.setBackgroundResource(R.drawable.gpio_low_on);
    			}else{
    				mbutton1.setBackgroundResource(R.drawable.gpio_low_off);
    			}
    			if(mbutton2.isEnabled()){
    				mbutton2.setBackgroundResource(R.drawable.gpio_low_on);
    			}else{
    				mbutton2.setBackgroundResource(R.drawable.gpio_low_off);
    			}

    			
    			
    		break;
    		case "04": //PIO (0100) 11:0,10:1 (0=LOW,1=HIGH)
    			PIOHiLow_10=true;
    			PIOHiLow_11=false;
    			
    			if(mbutton1.isEnabled()){
    				mbutton1.setBackgroundResource(R.drawable.gpio_high_on);
	   			}else{
	   				mbutton1.setBackgroundResource(R.drawable.gpio_high_off);
	   			}
	   			if(mbutton2.isEnabled()){
	  				mbutton2.setBackgroundResource(R.drawable.gpio_low_on);
	   			}else{
	  				mbutton2.setBackgroundResource(R.drawable.gpio_low_off);
	   			}

    		break;
    		case "08": //PIO (1000) 11:1,10:0 (0=LOW,1=HIGH)
    			PIOHiLow_10=false;
    			PIOHiLow_11=true;
    			if(mbutton1.isEnabled()){
      				 mbutton1.setBackgroundResource(R.drawable.gpio_low_on);
   	   			}else{
   	   				mbutton1.setBackgroundResource(R.drawable.gpio_low_off);
   	   			}
   	   			if(mbutton2.isEnabled()){
   	  				 mbutton2.setBackgroundResource(R.drawable.gpio_high_on);
   	   			}else{
   	  				mbutton2.setBackgroundResource(R.drawable.gpio_high_off);
   	   			}


    			
    		break;
    		case "0C": //PIO (1100) 11:1,10:1 (0=LOW,1=HIGH)
    			PIOHiLow_10=true;
    			PIOHiLow_11=true;
      			if(mbutton1.isEnabled()){
     				 mbutton1.setBackgroundResource(R.drawable.gpio_high_on);
  	   			}else{
  	   				mbutton1.setBackgroundResource(R.drawable.gpio_high_off);
  	   			}
  	   			if(mbutton2.isEnabled()){
  	  				 mbutton2.setBackgroundResource(R.drawable.gpio_high_on);
  	   			}else{
  	  				mbutton2.setBackgroundResource(R.drawable.gpio_high_off);
  	   			}


    		break;
    	}
    }
	 
	//PIO ?��출력 ?��?��  
	 void PIO_DIRECTION_loop (String data) {	
	    	String str_buff = data.substring(0, 2);
	    	switch(str_buff){
	    		case "01": //PIO (0001) 11:0,10:0 (0=IN,1=OUT)
	    			PIOIO_10=false;
	    			PIOIO_11=false;
	    			PIOHiLow_10=false;
	    			PIOHiLow_11=false;
	    			mimageView_pio10.setImageResource(R.drawable.gpio_in);
	    			mimageView_pio11.setImageResource(R.drawable.gpio_in);
	    			mbutton1.setEnabled(false);
	    			mbutton2.setEnabled(false);
	    		break;
	    		case "05": //PIO (0101) 11:0,10:1 (0=IN,1=OUT)
	    			PIOIO_10=true;
	    			PIOIO_11=false;
	    			PIOHiLow_10=false;
	    			PIOHiLow_11=true;
	    			mimageView_pio10.setImageResource(R.drawable.gpio_out);
	    			mimageView_pio11.setImageResource(R.drawable.gpio_in);
	    			mbutton1.setEnabled(true);
	    			mbutton2.setEnabled(false);
	    			
	    		break;
	    		case "09": //PIO (1001) 11:1,10:0 (0=IN,1=OUT)
	    			PIOIO_10=false;
	    			PIOIO_11=true;
	    			PIOHiLow_10=true;
	    			PIOHiLow_11=false;
	    			mimageView_pio10.setImageResource(R.drawable.gpio_in);
	    			mimageView_pio11.setImageResource(R.drawable.gpio_out);
	    			mbutton1.setEnabled(false);
	    			mbutton2.setEnabled(true);
	    		break;
	    		case "0D": //PIO (1101) 11:1,10:1 (0=IN,1=OUT)
	    			PIOIO_10=true;
	    			PIOIO_11=true;
	    			PIOHiLow_10=true;
	    			PIOHiLow_11=true;
	    			mimageView_pio10.setImageResource(R.drawable.gpio_out);
	    			mimageView_pio11.setImageResource(R.drawable.gpio_out);
	    			mbutton1.setEnabled(true);
	    			mbutton2.setEnabled(true);

	    		break;
	    	}
	    	
	    }
	
	// string->byte (read)
	public void sendString(String string) {
		byte[] data = string.getBytes();
		sendData(data);
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "park LedContorlFragment =onDestroy ");
        time_test_count=0;
        time_schedule=0;
        mTestHandler_finish=false;
        try {
			Thread.sleep(600);
		} catch (Exception ex) {
		}
        try {
        	mStateHandler.removeMessages(0);
    	} catch (Exception e) {
    	}
        try {
        	mTestHandler.removeMessages(0);
    	} catch (Exception e) {
    	}
        try {
        	mDisgetHandler.removeMessages(0);
		} catch (Exception e) {
		}
        
    }
}

