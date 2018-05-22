package com.bennyhawk.camera1api;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by bennyhawk on 1/18/18.
 */

public class View extends android.view.View{
	public float x;
	public float y;
	private final int r;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	public View(Context context, float x, float y, int r) {
		super(context);
		mPaint.setColor(0xFF00FF00);
		mPaint.setStrokeWidth(10);
		mPaint.setStyle(Paint.Style.STROKE);//not transparent. color is green
		this.x = x;
		this.y = y;
		this.r = r;  //radius
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(400,400,600,600,mPaint);
	}
	
	public void setXY(float x, float y){
		this.x = x;
		this.y = y;
	}
}
