/*<���۱�>
�� ���α׷� �� �ҽ��ڵ�� (��)Ĩ���� ���۹��Դϴ�.
�� ���α׷� �� �ҽ��ڵ�� (��)Ĩ���� ������� ��ǰ�� ������ �?���� ����Ǵ� �� �Դϴ�.
����� ������� ��ǰ�� Ȱ���� ���� �̿��� �뵵�� ����ϴ� ���� �����մϴ�.

<License>
The program or internal source codes was created by CHIPSEN Co.,Ltd.
In order to use the program or internal source codes, you must buy CHIPSEN's Bluetooth products.
You are not allowed to use it for purposes other than CHIPSEN's Bluetooth Products

Copyright 2015. CHIPSEN all rights reserved.*/

package com.chipsen.cle110_test_kit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class chipsen_logo extends Activity {
    protected static final String LOG_TAG = null;
	private Handler mHandler;

    private Runnable mRunnable;

	 /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chipsen_logo);
        
        
        mRunnable = new Runnable() {
            @Override
            public void run() {
            	Log.e(LOG_TAG, "parkchipsen_logo=+++ ON CREATE +++1_1");

                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        };
         
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable,1000);
        
		  
      
        // TODO Auto-generated method stub
    }
    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

}
