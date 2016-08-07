package com.dargo.mignon.common;

import java.lang.reflect.Field;
import java.util.Random;

import com.dargo.mignon.FindMyPrey;
import com.dargo.mignon.R;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.ViewConfiguration;

public class Utilities 
{
	public final static String PREFS_NAME 	= 	"FindTheDuckPrefFile";
	public final static String IS_SOUND_ON 	= 	"IS_SOUND_ON";
	public final static String IS_TEXT_ON 	= 	"IS_TEXT_ON";
	public final static String HIGH_SCORE 	=	"HIGH_SCORE";	 
	
	public static int randInt(int iMin, int iMax) {

	    Random aRand = new Random();

	    int aRandomNum = aRand.nextInt((iMax - iMin) + 1) + iMin;

	    return aRandomNum;
	}
	
	
	static public void showActionBar(Context iContext)
    {
        try {
        	ViewConfiguration config = ViewConfiguration.get(iContext);
        	Field menuKeyField = ViewConfiguration.class
        	.getDeclaredField("sHasPermanentMenuKey");
        	if (menuKeyField != null) 
        	{
        		menuKeyField.setAccessible(true);
        		menuKeyField.setBoolean(config, false);
        	}
        } 
        catch (Exception e) {
        	e.printStackTrace();
        	}
    }
	
	static public Intent clickedOnMainMenu(MenuItem item, Context iContext) {
        switch (item.getItemId()) 
        {
	    	case R.id.settings_MI:
	    		return new Intent(iContext, SettingsMenu.class);
	    	case R.id.new_game_MI:
	    		return new Intent(iContext, FindMyPrey.class);
	    	case android.R.id.home:
	    		return null;
        }
        return null;
    }
    
	


}
