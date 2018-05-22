package com.bennyhawk.camera1api;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.intentfilter.androidpermissions.PermissionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.Collections.singleton;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,TextToSpeech.OnInitListener {
	Camera camera;

	SurfaceView surfaceView;

	SurfaceHolder surfaceHolder;
	
	View mBallView = null;
	int mScrWidth, mScrHeight;
	android.graphics.PointF mBallPos, mBallSpd;
	LinearLayout overlay;
	Context context;
	MainActivity activity;

	TextToSpeech tts;
	Button classifier;
	Button depth;
	
	boolean isClassifierActive = true;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tts = new TextToSpeech(this, this);
		classifier = findViewById(R.id.button3);
		depth = findViewById(R.id.button4);
		classifier.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(android.view.View view) {
				isClassifierActive = true;
			}
		});
		depth.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(android.view.View view) {
				isClassifierActive = false;
			}
		});
		
		
		
		
		
		
		context = this;
		activity = this;
		
		
		
		Display display = getWindowManager().getDefaultDisplay();
		mScrWidth = display.getWidth();
		mScrHeight = display.getHeight();
		mBallPos = new android.graphics.PointF();
		mBallSpd = new android.graphics.PointF();

//create variables for ball position and speed
		mBallPos.x = mScrWidth / 2;
		mBallPos.y = mScrHeight / 2;
		mBallSpd.x = 0;
		mBallSpd.y = 0;
		mBallView = new View(this, mBallPos.x, mBallPos.y, 20);
		 overlay = findViewById(R.id.overlay);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(activity);
		surfaceHolder.setFormat(SurfaceHolder.SURFACE_TYPE_GPU);
		 
		
		PermissionManager permissionManager = PermissionManager.getInstance(this);
		int permissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.CAMERA);
		if(permissionCheck!= PackageManager.PERMISSION_GRANTED) {
			
			permissionManager.checkPermissions(singleton(Manifest.permission.CAMERA), new PermissionManager.PermissionRequestListener() {
				@Override
				public void onPermissionGranted() {
					startActivity(new Intent(context, MainActivity.class));
					finish();
					
				}
				
				@Override
				public void onPermissionDenied() {
					Toast.makeText(context, "App cannot work without camera permission", Toast.LENGTH_LONG).show();
					tts.speak("App cannot work without camera permission", TextToSpeech.QUEUE_FLUSH, null, "1");
					
				}
			});
		}
		else{
			tts.speak("Instructions. Click on the top part of the screen to get description, bottom part for navigation",TextToSpeech.QUEUE_FLUSH,null,"1");
			
		}
		permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
			@Override
			public void onPermissionGranted() {
			
			}
			
			@Override
			public void onPermissionDenied() {
				Toast.makeText(context, "App cannot work without write storage permission", Toast.LENGTH_LONG).show();
				tts.speak("App cannot work without write storage permission",TextToSpeech.QUEUE_FLUSH,null,"1");
				
			}
		});
		
		
		
		
	}
	int i = 0;
	Integer counter = 0;
	private final int CHECK_CODE = 0x1;
	Speaker speaker;
	
	
	@Override
	public void surfaceCreated(final SurfaceHolder surfaceHolder) {
		Log.d("Surface","Surface Created");
		try {
			
			camera = Camera.open();
			camera.setPreviewCallback(new Camera.PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] bytes, Camera arg1) {
					Log.d("TAG",String.valueOf(isClassifierActive));
					
					if(i==350){
						Log.d("DANG",String.valueOf(bytes.length / 1024));
						YuvImage yuvimage = new YuvImage(bytes, ImageFormat.NV21,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height,null);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						yuvimage.compressToJpeg(new Rect(0,0,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height), 100, baos);
						
						try {
							String path = Environment.getExternalStorageDirectory().toString();
							OutputStream fOut = null;
							counter++;
							
							File file = new File(path, "Yay.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
							fOut = new FileOutputStream(file);
							Log.d("FIle",file.toString());
							fOut.write(baos.toByteArray());
							fOut.flush();
							fOut.close();
							
							final RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
							MultipartBody.Part body = MultipartBody.Part.createFormData("sampleFile", file.getName(), reqFile);
							RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "sampleFile");
							if(isClassifierActive){
								
								APICLient.getEggAPIInterface().postImage(body,name).enqueue(new Callback<Model>() {
									@Override
									public void onResponse(Call<Model> call, Response<Model> response) {
										Log.d("Pro",response.body().getData());
										
										
										tts.speak(response.body().getData(),TextToSpeech.QUEUE_FLUSH,null,"1");
										
									}
									
									@Override
									public void onFailure(Call<Model> call, Throwable t) {
										Log.d("Pro",t.getCause().getMessage());
									}
								});
								
							}
							else{
								APICLient.getEggAPI2Interface().postImageDepth(body,name).enqueue(new Callback<Model>() {
									@Override
									public void onResponse(Call<Model> call, Response<Model> response) {
										Log.d("Pro",response.body().getData());
										
										
										tts.speak(response.body().getData(),TextToSpeech.QUEUE_FLUSH,null,"1");
										
									}
									
									@Override
									public void onFailure(Call<Model> call, Throwable t) {
										Log.d("Pro",t.getMessage());
									}
								});
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						i=0;
					}
					else {
						i++;
					}
					
					//overlay.removeAllViews();
					//overlay.addView(mBallView); //add ball to main screen
					//mBallView.invalidate();
					
				}
			});
			
		} catch (RuntimeException e) {
			
			System.err.println(e);
			
			return;
			
		}
		
		Camera.Parameters param;
		
		param = camera.getParameters();
		List<int[]> fps = param.getSupportedPreviewFpsRange();
		for(int[] d:fps){
			Log.d("FPS", String.valueOf(d[0]) + " * " + String.valueOf(d[1]));
		}
		
		//param.setPreviewFpsRange(15000,15000);
		param.setPreviewFpsRange(30000,30000);
		
		param.setVideoStabilization(true);
		
		List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
		//Camera.Size r = previewSizes.get(previewSizes.size()-10);
		//Camera.Size r = previewSizes.get(0);
		
		
		for(Camera.Size d:previewSizes){
			Log.d("SIZE", String.valueOf(d.width) + " * " + String.valueOf(d.height));
		}
		
		
		
		
		
		param.setPreviewSize(640,480);
		param.setVideoStabilization(true);
		
		camera.setParameters(param);
		
		try {
			
			camera.setPreviewDisplay(surfaceHolder);
			camera.setDisplayOrientation(90);
			
			camera.startPreview();
			
		} catch (Exception e) {
			
			System.err.println(e);
			
			
		}
	}
	int q =0;
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
		
		Log.d("Surface","Surface Changed");
		
		try {
			
			camera = Camera.open();
			camera.setPreviewCallback(new Camera.PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] bytes, Camera arg1) {
					
					if(q==350){
						Log.d("DANG",String.valueOf(bytes.length / 1024));
						YuvImage yuvimage = new YuvImage(bytes, ImageFormat.NV21,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height,null);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						yuvimage.compressToJpeg(new Rect(0,0,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height), 100, baos);
						
						try {
							String path = Environment.getExternalStorageDirectory().toString();
							OutputStream fOut = null;
							counter++;
							
							File file = new File(path, "Yay.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
							fOut = new FileOutputStream(file);
							Log.d("FIle",file.toString());
							fOut.write(baos.toByteArray());
							fOut.flush();
							fOut.close();
							
							final RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
							MultipartBody.Part body = MultipartBody.Part.createFormData("sampleFile", file.getName(), reqFile);
							RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "sampleFile");
							
							if(isClassifierActive){
								APICLient.getEggAPIInterface().postImage(body,name).enqueue(new Callback<Model>() {
									@Override
									public void onResponse(Call<Model> call, Response<Model> response) {
										Log.d("Pro",response.body().getData());
										
										
										tts.speak(response.body().getData(),TextToSpeech.QUEUE_FLUSH,null,"1");
										
									}
									
									@Override
									public void onFailure(Call<Model> call, Throwable t) {
										Log.d("Pro",t.getMessage());
									}
								});
								
							}
							else{
								APICLient.getEggAPI2Interface().postImageDepth(body,name).enqueue(new Callback<Model>() {
									@Override
									public void onResponse(Call<Model> call, Response<Model> response) {
										Log.d("Pro",response.body().getData());
										
										
										tts.speak(response.body().getData(),TextToSpeech.QUEUE_FLUSH,null,"1");
										
									}
									
									@Override
									public void onFailure(Call<Model> call, Throwable t) {
										Log.d("Pro",t.getMessage());
									}
								});
							}
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						q=0;
					}
					else {
						q++;
					}
					
					//overlay.removeAllViews();
					//overlay.addView(mBallView); //add ball to main screen
					//mBallView.invalidate();
					
				}
			});
			
		} catch (RuntimeException e) {
			
			System.err.println(e);
			
			return;
			
		}
		
		Camera.Parameters param;
		
		param = camera.getParameters();
		List<int[]> fps = param.getSupportedPreviewFpsRange();
		for(int[] d:fps){
			Log.d("FPS", String.valueOf(d[0]) + " * " + String.valueOf(d[1]));
		}
		
		//param.setPreviewFpsRange(15000,15000);
		param.setPreviewFpsRange(30000,30000);
		
		param.setVideoStabilization(true);
		
		List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
		//Camera.Size r = previewSizes.get(previewSizes.size()-12);
		
		//Camera.Size r = previewSizes.get(0);
		
		
		for(Camera.Size d:previewSizes){
			Log.d("SIZE", String.valueOf(d.width) + " * " + String.valueOf(d.height));
		}
		
		
		
		
		
		param.setPreviewSize(640,480);
		param.setVideoStabilization(true);
		
		camera.setParameters(param);
		
		try {
			
			camera.setPreviewDisplay(surfaceHolder);
			camera.setDisplayOrientation(90);
			
			camera.startPreview();
			
		} catch (Exception e) {
			
			System.err.println(e);
			
			
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	Log.d("Surface","Surface Destroyed");
	}
	
	
	@Override
	public void onInit(int i) {
		if (i == TextToSpeech.SUCCESS) {
			
			int result = tts.setLanguage(Locale.US);
			
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
			
			}
			
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}
}

