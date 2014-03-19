package com.lsu.vizeq;


	import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

	public class PreferenceCircle extends View
	{

		public PreferenceCircle(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		String text = "B";
		int x;
		int y;
		int radius;
		Color color;
//		string text;
		final float scale = getResources().getDisplayMetrics().density;
		final float width = getResources().getDisplayMetrics().widthPixels;
		final float height = getResources().getDisplayMetrics().heightPixels;
		int twentyFiveDP = (int) (25 * scale + 0.5f);
//		Bitmap sub4 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.quarter), twentyFiveDP, twentyFiveDP, true);
//		Bitmap sub8 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eighth), twentyFiveDP, twentyFiveDP, true);
//		Bitmap sub16 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sixteenths), twentyFiveDP, twentyFiveDP, true);
		Paint paint = new Paint();
		Paint box = new Paint();

		// Dimensions: 125dp x 30dp
//		int width = (int) (125 * scale + 0.5f);
//		int height = (int) (40 * scale + 0.5f);

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			animate().setDuration(5000);			
			animate().scaleX(5);
			animate().scaleY(5);
//			animate().translationY(height/2);
//			animate().translationX(width/2);
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);

//			int green = 100;
//			RectF rect;
			paint.setColor(Color.MAGENTA);
			canvas.drawCircle(x, y, radius, paint);
			paint.setTextSize(50);
//			if (getPaddingTop() != 0 && getPaddingBottom() != 0)
//			{
//				for (int i = 0; i <= 20; i++)
//				{
	
//				}
//				if (sub == 4)
//					canvas.drawBitmap(sub4, width - (25+10), (height - 25) / 2, paint);
//				else if (sub == 8)
//					canvas.drawBitmap(sub8, width - (25 + 10), (height - 25) / 2, paint);
//				else if (sub == 16)
//					canvas.drawBitmap(sub16, width - (25 + 10), (height - 25) / 2, paint);
			    paint.setColor(color.BLUE);
			    canvas.drawText(text, x, y, paint);
			    			    
//				canvas.drawText(text, width/10, height - 5, paint);
//			}
//			else 
//			{
//				}
//				if (sub == 4)
//					canvas.drawBitmap(sub4, width - (25 + 10), height - 30 - 7, paint);
//				else if (sub == 8)
//					canvas.drawBitmap(sub8, width - (25 + 10), height - 30 - 7, paint);
//				else if (sub == 16)
//					canvas.drawBitmap(sub16, width - (25 + 10), height - 30 - 7, paint);
//			}
			// paint.setStyle(Paint.Style.FILL);
			// canvas.drawRoundRect(rect, rx, ry, paint);
			// make the entire canvas white
			
			// canvas.drawPaint(paint);
			

			// Bitmap sub8 = BitmapFactory.decodeResource(getResources(), R.drawable.eighth);


		}

		public PreferenceCircle(Context context, int x, int y, int radius, String text)
		{
			super(context);
			super.setX(x);
			super.setY(y);
			this.text = text;
			this.x = x;
			this.y = y;
			this.radius = radius;
			//this.setTag("" + pattern + tempo + sub);
			this.setPadding(0, 0, 0, 0);
			this.setTag("P"+text);
		}

		public PreferenceCircle(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public PreferenceCircle(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}

	}


