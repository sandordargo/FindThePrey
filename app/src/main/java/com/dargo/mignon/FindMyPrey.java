package com.dargo.mignon;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dargo.mignon.common.ToastWrapper;
import com.dargo.mignon.common.Utilities;

public class FindMyPrey extends Activity {

	private ImageView myBackground;
	private Context myContext;
	private int myScreenHeight = 0;
	private int myScreenWidth = 0;
	private Prey myPrey = null;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	public static final int QUACKQUACK = R.raw.click;
	public static final int HAPHAP = R.raw.minion_ta_daa;
	public boolean myIsSoundOn;
	public boolean myIsTextOn;

	//public int myAdHeight = 0; // there is no ad for the moment
	private int myScore = 0;
	private int myHighScore = 0;
	private TextView myScoreTV;
	private TextView myHighScoreTV;
	private SharedPreferences mySettings;
	private ToastWrapper toast;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_my_duck);
		this.mySettings = getSharedPreferences(Utilities.PREFS_NAME, 0);
		this.myHighScoreTV = (TextView) findViewById(R.id.high_score_value_TV);
		this.myScoreTV = (TextView) findViewById(R.id.current_score_value_TV);
		this.myContext = this;
		this.toast = new ToastWrapper(this.myContext);
		readPreferences();
		this.myHighScoreTV.setText(String.valueOf(this.myHighScore));
		initSounds();
		Utilities.showActionBar(this);

		this.myBackground = (ImageView) findViewById(R.id.imageView1);
		this.myBackground.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	
			if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && !myPrey.myIsFound)	{
				int posX;
				int posY;
				posX = (int) event.getX();
				posY = (int) event.getY();
				myScore++;
				myScoreTV.setText(String.valueOf(myScore));
				handleTap(posX,posY);
				return true;
			}
			return false;
		}
		});
	}
	
	private void readPreferences() {
    	myIsSoundOn = mySettings.getBoolean(Utilities.IS_SOUND_ON, true);
    	myIsTextOn = mySettings.getBoolean(Utilities.IS_TEXT_ON, false);
    	myHighScore = mySettings.getInt(Utilities.HIGH_SCORE, 0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initSounds() {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap(3);    
		soundPoolMap.put( QUACKQUACK, soundPool.load(this, R.raw.click, 1) );
		soundPoolMap.put( HAPHAP, soundPool.load(this, R.raw.minion_ta_daa, 2) );
	}
	
	public void handleTap(int posX, int posY) {
		//FOR DEBUG
		//double aDistance = calculateDistance(posX, posY);
		//int aTapLevel = calculateTapLevel(aDistance, myPrey.myMaxDistance); // quality of tapping based on distance
		if (isDuckThere(posX, posY)) {
			if (myScore < myHighScore || myHighScore == 0) {
				this.toast.makeText(myContext, "Congratulations! You have just beaten your high score!");
		    	SharedPreferences.Editor anEditor = mySettings.edit();
	    		anEditor.putInt(Utilities.HIGH_SCORE, myScore).apply();
			}
			myPrey.myIsFound = true;
			myBackground.setImageResource(R.raw.last_pic);
			if (myIsSoundOn) {
				soundPool.play((Integer) soundPoolMap.get(HAPHAP), 1f, 1f, 1, 0, 1f);
			}
		} else 	{
			int aTapQuality = calculateTapLevel(calculateDistance(posX, posY), myPrey.myMaxDistance);
			if (myIsSoundOn) {
			createSoundNotification(aTapQuality);
			}
			if (myIsTextOn) {
			createTextNotification(aTapQuality);	
			}
			//FOR DEBUG
	    	//Toast.makeText(myContext, "You tapped at X: " + String.valueOf(posX) + "/" + String.valueOf(myScreenWidth) + 
	    	//		" Y: " + String.valueOf(posY) + "/" + String.valueOf(myScreenHeight) + " distance: " + aDistance + " maxD: " + myPrey.myMaxDistance + " qual: " + String.valueOf(aTapLevel), Toast.LENGTH_SHORT).show();
	
		}
	}
	
	public boolean isDuckThere(int iPosX, int iPosY) {
		if (Math.abs(iPosX - myPrey.myXCoordinate) < 0.02 * myScreenWidth && Math.abs(iPosY- myPrey.myYCoordinate) < 0.02 * myScreenHeight) {
			return true;
		} else {
			return false;
		}
	}
	
	public void createSoundNotification(int iTapQuality) {
		float volume = (float) (iTapQuality * 0.2);
		switch (iTapQuality) {
			case 1	: 	volume =  0.1f;
						break;
			case 2	: 	volume =  0.25f;
						break;
			case 3	:	volume =  0.45f;
						break;
			case 4	:	volume =  0.7f;
						break;
			case 5  :	volume =  1f;
						break;
		}
		soundPool.play((Integer) soundPoolMap.get(QUACKQUACK), volume, volume, 1, 0, 1f);
	}
	
	public void createTextNotification(int iTapQuality) {
		String aMsg = "";
		switch (iTapQuality) {
			case 1	: 	aMsg = "Far from it...";
						break;
			case 2	: 	aMsg = "Keep searching!";
						break;
			case 3	:	aMsg = "You are on the track!";
						break;
			case 4	:	aMsg = "You almost caught it!";
						break;
			case 5  :	aMsg = "HOT!!!HOT!!!HOT!!!";
						break;
		}
		this.toast.makeText(myContext, aMsg);
	}
	
	public int calculateTapLevel(double iDistance, double iMaxDistance) {
		double anAbsTapLevel = iDistance / iMaxDistance;
		int retVal = 0;
		if (anAbsTapLevel < 0.1) {
			retVal = 5;
		} else if (anAbsTapLevel < 0.25) {
			retVal = 4;
		} else if (anAbsTapLevel < 0.45) {
			retVal = 3;
		} else if (anAbsTapLevel < 0.70) 	{
			retVal = 2;
		} else {
			retVal = 1;
		}
		return retVal;
	}
	
	public double calculateDistance(int iTappedX, int iTappedY) {
		//calculate distance of Prey and tapped
		//d = sqrt (sqr(x2-x1) + sqr(y2-y1))
		
		return Math.sqrt(Math.pow(myPrey.myXCoordinate - iTappedX, 2) + Math.pow(myPrey.myYCoordinate - iTappedY, 2));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		RelativeLayout aLayout = (RelativeLayout) findViewById(R.id.main_view);
		myScreenHeight = aLayout.getHeight();
		myScreenWidth = aLayout.getWidth();
		 
		if (myPrey == null) {
			 myPrey = new Prey(Utilities.randInt(0, myScreenWidth), Utilities.randInt(0, myScreenHeight), myScreenWidth, myScreenHeight);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		readPreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	try {
    		startActivity(Utilities.clickedOnMainMenu(item, this));
            return super.onOptionsItemSelected(item);	
    	} catch (NullPointerException ex) {
    		super.onBackPressed();
    		return false;
    	}
    }
}
