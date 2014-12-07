package com.erichlotto.showshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Context context;
    private Intent i;
    TextView distancia_txt;
    SeekBar sb;
    int storedMaxDist;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.context=getApplicationContext();
		i = new Intent(context, MusicCheckerService.class);
		context.startService(i);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int defaultMaxDistance = getResources().getInteger(R.integer.max_distance);
		storedMaxDist = sharedPref.getInt("STORED_MAX_DIST", defaultMaxDistance);
		
		distancia_txt = (TextView)findViewById(R.id.distancia_txt);
		updateSeekText(storedMaxDist);
		
		sb = (SeekBar)findViewById(R.id.distancia_sb);
		sb.setProgress(storedMaxDist);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int prog;
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt("STORED_MAX_DIST", prog);
				editor.commit();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				prog=progress;
				updateSeekText(progress);
				
			}
		});
	}
	
	private void updateSeekText(int position){
		distancia_txt.setText(Math.round(position/1000)+" m");
	}

}
