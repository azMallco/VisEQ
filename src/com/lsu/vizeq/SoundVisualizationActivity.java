package com.lsu.vizeq;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map.Entry;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lsu.vizeq.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SoundVisualizationActivity extends Activity
{
	/**
	 * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private ReceiveColorTask rct;
	
	private String nowPlaying;
	
	private TextView textViewNowPlaying;
	
	ActionBar actionBar;
	

	private VisualizerView vizView;
	private MyApplication myapp;
	
	
	public void serverHeartbeat()
	{
		new Thread( new Runnable()
		{
			public void run()
			{
				
				DatagramSocket listenSocket, sendSocket;
				
				try {
					listenSocket = new DatagramSocket(7772);
					sendSocket = new DatagramSocket();
					boolean guesting = true;
					while(guesting)
					{
						byte [] ping = new byte[1024];
						byte [] ack = new byte[1024];
						ack = "ack".getBytes();

						DatagramPacket pingPacket = new DatagramPacket(ping, ping.length);
						listenSocket.receive(pingPacket);
						DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, pingPacket.getAddress(), 7772);
						try
						{
//							Log.d("heartbeat", "sending ack");
							sendSocket.send(ackPacket);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						
					}
					sendSocket.close();
					listenSocket.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(VisualizerView.cam != null)
		{
			VisualizerView.cam.release();
		}
		VisualizerView.cam = null;
		if (rct != null)
		rct.cancel(true);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		rct = new ReceiveColorTask();
		rct.execute();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sound_visualization);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		
		myapp = (MyApplication) this.getApplicationContext();
		//serverHeartbeat();
		
//		Log.d("oncreate", "soundviz");
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		MyApplication.doFlash = memory.getBoolean("cameraFlash", true);
		MyApplication.doBackground = memory.getBoolean("backgroundFlash", true);
		
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;		
		}
		actionBar.hide();
		
		textViewNowPlaying = (TextView)findViewById(R.id.track_info_visualizer);
		textViewNowPlaying.setSelected(true);
		textViewNowPlaying.setText(VizEQ.nowPlaying);
		
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		vizView = (VisualizerView)findViewById(R.id.visualizer_view);
		
		
		((VisualizerView)vizView).init(this, false);
		
		
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, vizView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener()
		{
			// Cached values.
			int mControlsHeight;
			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
				{
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0)
					{
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0)
					{
						mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
					}
					controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
				} else
				{
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
				}

				if (visible && AUTO_HIDE)
				{
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		vizView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (TOGGLE_ON_CLICK)
				{
					mSystemUiHider.toggle();
				} else
				{
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.search_request).setOnTouchListener(mDelayHideTouchListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (rct != null) rct.cancel(true);
	}
	
	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring behavior of controls going away while interacting with activity
	 * UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			if (AUTO_HIDE)
			{
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			Intent nextIntent = new Intent(SoundVisualizationActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
			rct.cancel(true);
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any previously scheduled calls.
	 */
	private void delayedHide(int delayMillis)
	{
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	private class ReceiveColorTask extends AsyncTask<Void, String, String>
	{
		DatagramSocket receiveSocket;
		FrameLayout FL = (FrameLayout) findViewById(R.id.light);
		@Override
		protected void onCancelled() {
			if (receiveSocket != null)
			receiveSocket.close();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				receiveSocket = new DatagramSocket(7770);
				byte[] receiveData = new byte[200];
				while (!isCancelled())
				{
					final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					receiveSocket.receive(receivePacket);
					String header = PacketParser.getHeader(receivePacket);
					final String[] args = PacketParser.getArgs(receivePacket);
					
//					Log.d("packet", PacketParser.getHeader(receivePacket));
					if(header.compareTo("freq_circle") == 0)
					{
						vizView.SetCircleStates(args);
						//color = PacketParser.getArgs(receivePacket)[0];
						//publishProgress(color);
						//Log.d("UDP","Received!"+color);
					}
					else if (header.equals("flash"))
					{
						vizView.flash = true;
					}
					else if (header.equals("track_info")) {
						Handler mainHandler = new Handler(SoundVisualizationActivity.this.getMainLooper());
						Runnable myRunnable = new Runnable() {

							@Override
							public void run() {
								textViewNowPlaying.setText(args[0]);
							}
							
						};
						mainHandler.post(myRunnable);
						
					}
				}
			} 
			catch (Exception e) 
			{
				receiveSocket.disconnect();
				receiveSocket.close();
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			receiveSocket.close();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			FL.setBackgroundColor(Color.parseColor(values[0]));
		}
		
	}
	
}
