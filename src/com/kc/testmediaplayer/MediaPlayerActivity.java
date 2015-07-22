package com.kc.testmediaplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MediaPlayerActivity extends ActionBarActivity implements OnPreparedListener, SurfaceHolder.Callback{

	MediaPlayer mediaPlayer;
	SurfaceView sView;
	SurfaceHolder sHolder;
	SeekBar sBar;
//	String url = "http://media.heywatch.com.s3.amazonaws.com/hls/big_buck_bunny/big_buck_bunny.m3u8"; // your URL here
	String url = "http://rnyoovideooutput.s3.amazonaws.com/bbhls2/bbhls.m3u8"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_player);//
		//String url = "http://media.heywatch.com.s3.amazonaws.com/hls/big_buck_bunny/big_buck_bunny.m3u8"; // your URL here
//		String url = "http://www.nasa.gov/multimedia/nasatv/NTV-Public-IPS.m3u8"; // your URL here
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		  getWindow().setFormat(PixelFormat.UNKNOWN);
		  sView = (SurfaceView)findViewById(R.id.surfaceViewMain);
		  sHolder = sView.getHolder();
//		  sHolder.setFixedSize(800, 480);
		  sHolder.addCallback(this);
		  sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		  sBar = (SeekBar) findViewById(R.id.seekBarVideo);
		  
		  sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	        }
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	        }
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            if(fromUser) {
	            	mediaPlayer.seekTo(progress);
	            }
	        }
		  });
		  
		  Button bPlay = (Button)findViewById(R.id.buttonPlay);
		  bPlay.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				try {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
					}else {
						mediaPlayer.start();
						sBar.postDelayed(OnUpdate, 100);
					}
					
				}catch(Exception ex) {
					
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.media_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.setDisplay(holder);
		try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();
			setVideoSize();
			sBar.setMax(mediaPlayer.getDuration());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//mediaPlayer.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	private void setVideoSize() {

        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        android.view.ViewGroup.LayoutParams lp = sView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
            
            if (lp.height > screenHeight) {
            	lp.height = screenHeight;
            	lp.width = (int) (lp.height * videoProportion);
            }
            
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
            
            if (lp.width > screenWidth){
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth / videoProportion);
            }
        }
        sView.setLayoutParams(lp);
    }
	
	private Runnable OnUpdate = new Runnable() {

	    @Override
	    public void run() {
	        if(sBar != null) {
	            sBar.setProgress(mediaPlayer.getCurrentPosition());
	        }
	        if(mediaPlayer.isPlaying()) {
	            sBar.postDelayed(OnUpdate, 100);
	        }
	    }
	};
}
