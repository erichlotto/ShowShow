package com.erichlotto.showshow;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastManager {
	
	private ArrayList<String>frases;
	private Handler handler;
	private Runnable updateData;
	private Context ctx;
	private static Handler h;
	private int anterior = -1;
	boolean showToast;
	
	public ToastManager(Context _ctx){
		this.ctx = _ctx;
		h = new Handler(ctx.getMainLooper());
		frases = new ArrayList<String>();
		handler = new Handler();
		showToast = false;
		updateData = new Runnable() {
			public void run() {
				int index = -1;
				if((frases.size()> 0) && (showToast == true)){
					Random r = new Random();
					index = r.nextInt(frases.size());
					while ( index == anterior ){
						index = r.nextInt(frases.size());
					}
					showToast(ctx, frases.get(index));
				}
				handler.postDelayed(updateData, 10000);
			}
		};
		handler.post(updateData);
	}
	
	private static void showToast(final Context ctx, final String message){
		   h.post(new Runnable(){
		       @Override
		       public void run(){
		            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
		       }
		   });
		}
	
	public void adicionaFrases(String[] _frases){
		for(String frase:_frases){
			frases.add(frase);
		}
		System.out.println(this.frases.toString());
	}
	
	public void stop(){
	
		showToast = false;
		
	}
	
	public void start(){
		
		showToast = true;
		
	}

	public void limpaFrases() {
		this.frases = new ArrayList<String>();
	}
}
