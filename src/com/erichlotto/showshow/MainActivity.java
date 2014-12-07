package com.erichlotto.showshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Context context;
    private Intent i;
    TextView distancia_txt;
    SeekBar sb;
    CheckBox cbArtista;
    CheckBox cbMusica;
    int storedMaxDist;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.context=getApplicationContext();
		i = new Intent(context, MusicCheckerService.class);
		context.startService(i);
		
		storedMaxDist = SavedData.getStoredMaxDistance(this.context);
		cbArtista = (CheckBox)findViewById(R.id.cb_artista);
		cbMusica = (CheckBox)findViewById(R.id.cb_musica);
		distancia_txt = (TextView)findViewById(R.id.distancia_txt);
		updateSeekText(storedMaxDist);
		
		cbArtista.setChecked(SavedData.getStoredArtistDetailsFlag(context));
		cbMusica.setChecked(SavedData.getStoredMusicDetailsFlag(context));
		
		cbArtista.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SavedData.setStoredArtistDetailsFlag(context, isChecked);
			}
		});
		cbMusica.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SavedData.setStoredMusicDetailsFlag(context, isChecked);
			}
		});
		
		sb = (SeekBar)findViewById(R.id.distancia_sb);
		sb.setProgress(storedMaxDist);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int prog;
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SavedData.storeMaxDistance(context, prog);
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
		distancia_txt.setText(position+" km");
	}

}
