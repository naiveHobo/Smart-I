package com.bennyhawk.camera1api;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by bennyhawk on 1/23/18.
 */

public class Speaker implements TextToSpeech.OnInitListener {
	
	private TextToSpeech tts;
	
	private boolean ready = false;
	
	private boolean allowed = false;
	
	public Speaker(Context context){
		tts = new TextToSpeech(context, this);
	}
	
	public boolean isAllowed(){
		return allowed;
	}
	
	public void allow(boolean allowed){
		this.allowed = allowed;
	}
	
	@Override
	public void onInit(int i) {
		Log.d("Speak",String.valueOf(i));
		
		if(i == TextToSpeech.SUCCESS){
			// Change this to match your
			// locale
			tts.setLanguage(Locale.US);
			ready = true;
			Log.d("Speak",String.valueOf(ready));
		}else{
			ready = false;
			Log.d("Speak",String.valueOf(i));
		}
	
	}
	
	public void speak(String text){
		
		// Speak only if the TTS is ready
		// and the user has allowed speech
		Log.d("Speak",String.valueOf(ready)+String.valueOf(allowed));
		
		if(allowed) {
			tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	
	public void pause(int duration){
		tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
	}
	
	// Free up resources
	public void destroy(){
		tts.shutdown();
	}
}
