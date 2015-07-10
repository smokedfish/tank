/*
 * MyImageView.java in package com.SeewaldSolutions.ToyCollect
 * (C) 2014 Dr. Alexander K. Seewald, Seewald Solutions
 * Authors: Georg Weissinger, Alexander K. Seewald
 * License: GPLv3  http://www.gnu.org/licenses/gpl-3.0.txt
 */

/* Formatting: tabstop=2 */

package com.SeewaldSolutions.ToyCollect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.io.BufferedInputStream;
import android.graphics.Rect;

public class MyImageView extends ImageView {
	
	Paint paint1 = new Paint();
	Paint paint2 = new Paint();
	Paint paint3 = new Paint();
	Paint paint4 = new Paint();
	Paint paint5 = new Paint();
	Paint paint6 = new Paint();
	Paint paint7 = new Paint();
	Paint thinpaint = new Paint();

	Bitmap logo = null;
	 
	MainActivity context;

	public MyImageView(Context context) {
		super(context);
		this.context = (MainActivity)context;
		initPaint();
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (MainActivity)context;
		initPaint();
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = (MainActivity)context;
		initPaint();
	}

	int h;
	int w;
	 
	public void initPaint() {
		paint1.setColor(Color.RED);
		paint1.setStrokeWidth(2);
		 	
	  int alpha = 90;
	  int a = Color.argb(alpha, 255, 0, 0);
		paint2.setColor(a);
		paint2.setStrokeWidth(5);
		 
		paint3.setColor(Color.RED);
		paint3.setStyle(Paint.Style.STROKE);
	 	paint3.setStrokeWidth(5);
			 
		paint4.setColor(Color.RED);
		paint4.setStyle(Paint.Style.STROKE);
		paint4.setStrokeWidth(2);
			 
		int b = Color.argb(alpha, 255,0, 0);
		paint5.setColor(b);
		paint6.setColor(b);
		paint6.setStrokeWidth(5);
			 
		int c = Color.argb(alpha, 255,0,0);
		paint7.setColor(c);
		paint7.setStyle(Paint.Style.STROKE);
		paint7.setStrokeWidth(5);
	
		thinpaint.setColor(c);
		thinpaint.setStrokeWidth(2);

    AssetManager assets = this.context.getResources().getAssets();
		BitmapFactory.Options bo = new BitmapFactory.Options();
		bo.inDither=false;

		InputStream buffer = null;
		try {
			buffer = new BufferedInputStream((assets.open("drawable/logo.png")));
		} catch (Exception e) {
			System.out.println(e.toString()); buffer=null;
		}

		if (buffer!=null) { this.logo = BitmapFactory.decodeStream(buffer,null,bo); } else { this.logo=null; }
	}
	 
	@Override
	protected void onDraw(Canvas canvas) {
		h=canvas.getHeight();
		w=canvas.getWidth();
		 
		this.context.breite = w;
		this.context.hohe = h;
		 
		this.context.kreisx = w/2;
		this.context.kreisy = h/2;
		 
		int r;
		if (h<w) {
			r = (int) Math.round((h*0.66)/2.0);
		} else {
			r = (int) Math.round((w*0.66)/2.0);
		}
		 
		this.context.radius = r;
		 
		canvas.drawCircle(w/2, h/2, r, paint3);
		canvas.drawCircle(w/2, h/2, r*4/23, paint2);
		
		canvas.drawLine(w/2, h/2-r, w/2, h/2+r, paint1);		//horizontal
		
		canvas.drawCircle(w/2-r*85/100, h/2, r*2/23, paint4);
		canvas.drawLine(w/2-r*85/100, h/2-r*3/23, w/2-r*90/100, h/2-r*2/23, paint1); //Drehkreis links1

		canvas.drawLine(w/2-r*85/100, h/2-r/23, w/2-r*90/100, h/2-r*2/23, paint1);
	
		canvas.drawLine(w/2-r*90/100, h/2+r*28/230, w/2-r*85/100, h/2+r*2/23, paint1);		//Drehkreis links2
		canvas.drawLine(w/2-r*90/100, h/2+r*8/230, w/2-r*85/100, h/2+r*2/23, paint1);
		
		canvas.drawCircle(w/2+r*85/100, h/2, r*2/23, paint4);
		canvas.drawLine(w/2+r*82/100, h/2-r*3/23, w/2+r*87/100, h/2-r*2/23, paint1);		//Drehkreis rechts
		canvas.drawLine(w/2+r*82/100, h/2-r/23, w/2+r*87/100, h/2-r*2/23, paint1);
		
		canvas.drawLine(w/2+r*87/100, h/2+r*3/23, w/2+r*82/100, h/2+r*2/23, paint1);		//Drehkreis links1
		canvas.drawLine(w/2+r*87/100, h/2+r/23, w/2+r*82/100, h/2+r*2/23, paint1);
		
		canvas.drawLine(w/2, h/2-r, w/2+r/23, h/2-r*87/100, paint1);		//Vorwaertspfeil
		canvas.drawLine(w/2, h/2-r, w/2-r/23, h/2-r*87/100, paint1);
		
		canvas.drawLine(w/2, h/2+r, w/2+r/23, h/2+r*87/100, paint1);		//Rueckwaertspfeil
		canvas.drawLine(w/2, h/2+r, w/2-r/23, h/2+r*87/100, paint1);
		
		canvas.drawLine(w/2-r, h/2-r*22/100, w/2+r, h/2+r*22/100, paint1);		//schief
		canvas.drawLine(w/2-r, h/2+r*22/100, w/2+r, h/2-r*22/100, paint1);		
		
		//LED links
		
		canvas.drawCircle(38*r/100,38*r/100, r*22/230, paint7);
		canvas.drawLine(38*r/100-2*r*22/230,38*r/100,38*r/100-r*22/230,38*r/100, paint7);	
		canvas.drawLine(38*r/100+2*r*22/230,38*r/100,38*r/100+r*22/230,38*r/100, paint7);
		canvas.drawLine(38*r/100,38*r/100+r*22/230,38*r/100,38*r/100+2*r*22/230, paint7);
		canvas.drawLine(38*r/100,38*r/100-r*22/230,38*r/100,38*r/100-2*r*22/230, paint7);
		
		//LED rechts
		canvas.drawCircle(38*r/100+2*r+30*r/100,38*r/100, r*22/230, paint6);
		canvas.drawLine(38*r/100-2*r*22/230+2*r+30*r/100,38*r/100,38*r/100-r*22/230+2*r+30*r/100,38*r/100, paint6);	
		canvas.drawLine(38*r/100+2*r*22/230+2*r+30*r/100,38*r/100,38*r/100+r*22/230+2*r+30*r/100,38*r/100, paint6);
		canvas.drawLine(38*r/100+2*r+30*r/100,38*r/100+r*22/230,38*r/100+2*r+30*r/100,38*r/100+2*r*22/230, paint6);
		canvas.drawLine(38*r/100+2*r+30*r/100,38*r/100-r*22/230,38*r/100+2*r+30*r/100,38*r/100-2*r*22/230, paint6);
		
		canvas.drawLine(38*r/100,38*r/100+2*r*22/230,38*r/100+2*r+30*r/100,38*r/100+2*r*22/230,thinpaint);
		canvas.drawLine(38*r/100,38*r/100-2*r*22/230,38*r/100+2*r+30*r/100,38*r/100-2*r*22/230,thinpaint);

		if (this.logo!=null) {
			canvas.drawBitmap(this.logo,new Rect(0,0,this.logo.getWidth(),this.logo.getHeight()),new Rect(w/3,h-r/3,w-w/3,h),thinpaint);
		}

		invalidate();	
	}
}
