package com.erichlotto.showshow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Thread t = new Thread(){
			public void run(){
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(i);
				}
			}
		};
		t.start();
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
