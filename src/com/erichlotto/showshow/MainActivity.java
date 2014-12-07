package com.erichlotto.showshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    private Context context;
    private Intent i;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.context=getApplicationContext();
		i = new Intent(context, MusicCheckerService.class);
		context.startService(i);
	}

}
