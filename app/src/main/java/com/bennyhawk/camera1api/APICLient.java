package com.bennyhawk.camera1api;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by bennyhawk on 1/18/18.
 */

public class APICLient {
	private static Retrofit retrofit = null;
	private static Retrofit retrofitEgg = null;
	
//	private static final String URL2 = "https://smarti.azurewebsites.net/";
//	private static final String URL = "https://smarti.azurewebsites.net/";
//
	private static final String URL2 = "http://52.224.217.196:3000/";
	private static final String URL = "http://52.224.217.196:3000/";
	
	
	public static APIInterface getEggAPIInterface(){
		
		if (retrofitEgg == null){
			
			OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
					.writeTimeout(60, TimeUnit.SECONDS)
					.build();
			retrofitEgg = new Retrofit.Builder()
					.baseUrl(URL)
					.client(okHttpClient)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
			
			
			
			
		}
		
		return retrofitEgg.create(APIInterface.class);
	}
	public static APIInterface getEggAPI2Interface(){
		
		if (retrofitEgg == null){
			retrofitEgg = new Retrofit.Builder().baseUrl(URL2)
					.addConverterFactory(GsonConverterFactory.create())
					.addConverterFactory(ScalarsConverterFactory.create())
					.build();
			
		}
		
		return retrofitEgg.create(APIInterface.class);
	}
	
	public interface APIInterface{
		@Multipart
		@POST("upload")
		Call<Model> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
		
		@Multipart
		@POST("upload1")
		Call<Model> postImageDepth(@Part MultipartBody.Part image, @Part("name") RequestBody name);
		
		
		
	}
	
	
}
