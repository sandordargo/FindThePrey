package com.dargo.mignon.common;

import com.dargo.mignon.R;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;


public class SettingsMenu extends Activity {
	private AdView adView;
	private CheckBox soundCheckBox;
	private CheckBox textCheckBox;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.settings_menu);
			this.soundCheckBox = (CheckBox) findViewById(R.id.is_sound_enabled_CB);
			this.textCheckBox = (CheckBox) findViewById(R.id.is_text_enabled_CB);
			readPreferences();
			Utilities.showActionBar(this);
			this.adView = (AdView) findViewById(R.id.settings_ad);
			this.adView.loadAd(new AdRequest());
		}
		
		private void readPreferences() {
			SharedPreferences aSettings = getSharedPreferences(Utilities.PREFS_NAME, 0);
			Boolean isSoundOn = aSettings.getBoolean(Utilities.IS_SOUND_ON, true);
			Boolean isTextOn = aSettings.getBoolean(Utilities.IS_TEXT_ON, false);
	    	
			this.soundCheckBox.setChecked(isSoundOn);
			this.textCheckBox.setChecked(isTextOn);
		}

		@SuppressLint("NewApi")
		public void setBackgroundRunCB() {
			SharedPreferences settings = getSharedPreferences(Utilities.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();

			editor.putBoolean(Utilities.IS_SOUND_ON, this.soundCheckBox.isChecked())
				.putBoolean(Utilities.IS_TEXT_ON, this.textCheckBox.isChecked())
				.apply();
		}
		
		public void onBackPressed() {
			setBackgroundRunCB();
			finish();
		}
		
		public void onPause() {
			super.onPause();
			setBackgroundRunCB();
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	try	{
	    		startActivity(Utilities.clickedOnMainMenu(item, this));
	            return super.onOptionsItemSelected(item);	
	    	} catch (NullPointerException exception) {
	    		super.onBackPressed();
	    		return false;
	    	}
	    }
}
