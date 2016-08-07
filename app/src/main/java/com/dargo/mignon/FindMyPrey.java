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

	private ImageView background;
	private Context context;
	private int screenHeight = 0;
	private int screenWidth = 0;
	private Prey prey = null;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	public static final int QUACKQUACK = R.raw.click;
	public static final int HAPHAP = R.raw.minion_ta_daa;
	public boolean isSoundOn;
	public boolean isTextOn;

	//public int myAdHeight = 0; // there is no ad for the moment
	private int score = 0;
	private int highScore = 0;
	private TextView scoreTextView;
	private TextView highScoreTextView;
	private SharedPreferences settings;
	private ToastWrapper toast;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_my_duck);
		this.settings = getSharedPreferences(Utilities.PREFS_NAME, 0);
		this.highScoreTextView = (TextView) findViewById(R.id.high_score_value_TV);
		this.scoreTextView = (TextView) findViewById(R.id.current_score_value_TV);
		this.context = this;
		this.toast = new ToastWrapper(this.context);
		readPreferences();
		this.highScoreTextView.setText(String.valueOf(this.highScore));
		initSounds();
		Utilities.showActionBar(this);

		this.background = (ImageView) findViewById(R.id.imageView1);
		this.background.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && !prey.myIsFound)	{
					int positionX = (int) event.getX();
					int positionY = (int) event.getY();
					score++;
					scoreTextView.setText(String.valueOf(score));
					handleTap(positionX, positionY);
					return true;
				}
			return false;
			}
		});
	}

	private void readPreferences() {
    	isSoundOn = settings.getBoolean(Utilities.IS_SOUND_ON, true);
    	isTextOn = settings.getBoolean(Utilities.IS_TEXT_ON, false);
    	highScore = settings.getInt(Utilities.HIGH_SCORE, 0);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void initSounds() {
		this.soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		this.soundPoolMap = new HashMap(3);
		this.soundPoolMap.put(FindMyPrey.QUACKQUACK, this.soundPool.load(this, R.raw.click, 1));
		this.soundPoolMap.put(FindMyPrey.HAPHAP, this.soundPool.load(this, R.raw.minion_ta_daa, 2));
	}

	public void handleTap(int posX, int posY) {
		//FOR DEBUG
		//double aDistance = calculateDistance(posX, posY);
		//int aTapLevel = calculateTapLevel(aDistance, prey.myMaxDistance); // quality of tapping based on distance
		if (isDuckThere(posX, posY)) {
			if (this.score < this.highScore || this.highScore == 0) {
				this.toast.makeText("Congratulations! You have just beaten your high score!");
		    	SharedPreferences.Editor anEditor = this.settings.edit();
	    		anEditor.putInt(Utilities.HIGH_SCORE, this.score).apply();
			}
			this.prey.myIsFound = true;
			this.background.setImageResource(R.raw.last_pic);
			if (this.isSoundOn) {
				this.soundPool.play((Integer) this.soundPoolMap.get(FindMyPrey.HAPHAP), 1f, 1f, 1, 0, 1f);
			}
		} else {
			int tapQuality = calculateTapLevel(calculateDistance(posX, posY), this.prey.myMaxDistance);
			if (this.isSoundOn) {
				createSoundNotification(tapQuality);
			}
			if (this.isTextOn) {
				createTextNotification(tapQuality);
			}
			//FOR DEBUG
	    	//Toast.makeText(context, "You tapped at X: " + String.valueOf(posX) + "/" + String.valueOf(this.screenWidth) +
	    	//		" Y: " + String.valueOf(posY) + "/" + String.valueOf(this.screenHeight) + " distance: " + aDistance + " maxD: " + prey.myMaxDistance + " qual: " + String.valueOf(aTapLevel), Toast.LENGTH_SHORT).show();

		}
	}

	public boolean isDuckThere(int positionX, int positionY) {
		return (Math.abs(positionX - this.prey.myXCoordinate) < 0.02 * this.screenWidth && Math.abs(positionY - this.prey.myYCoordinate) < 0.02 * this.screenHeight);
	}

	public void createSoundNotification(int tapQuality) {
		float volume = (float) (tapQuality * 0.2);
		switch (tapQuality) {
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
		this.soundPool.play(this.soundPoolMap.get(FindMyPrey.QUACKQUACK), volume, volume, 1, 0, 1f);
	}

	public void createTextNotification(int tapQuality) {
		String message = "";
		switch (tapQuality) {
			case 1 : message = "Far from it...";
						break;
			case 2 : message = "Keep searching!";
						break;
			case 3 : message = "You are on the track!";
						break;
			case 4 : message = "You almost caught it!";
						break;
			case 5 : message = "HOT!!!HOT!!!HOT!!!";
						break;
		}
		this.toast.makeText(message);
	}

	public int calculateTapLevel(double distance, double maxDistance) {
		double tapLevel = distance / maxDistance;
		int retVal;
		if (tapLevel < 0.1) {
			retVal = 5;
		} else if (tapLevel < 0.25) {
			retVal = 4;
		} else if (tapLevel < 0.45) {
			retVal = 3;
		} else if (tapLevel < 0.70) 	{
			retVal = 2;
		} else {
			retVal = 1;
		}
		return retVal;
	}

	public double calculateDistance(int tappedX, int tappedY) {
		//calculate distance of Prey and tapped
		//d = sqrt (sqr(x2-x1) + sqr(y2-y1))
		return Math.sqrt(Math.pow(prey.myXCoordinate - tappedX, 2) + Math.pow(prey.myYCoordinate - tappedY, 2));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_view);
		this.screenHeight = layout.getHeight();
		this.screenWidth = layout.getWidth();

		if (this.prey == null) {
			this.prey = new Prey(Utilities.getRandomNumber(0, this.screenWidth), Utilities.getRandomNumber(0, this.screenHeight), this.screenWidth, this.screenHeight);
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
