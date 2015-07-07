package edu.xidian.logtofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static String TAG = "LOG2FILE";
	
	private LogcatHelper loghelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//String log_dir = "applog";
		String log_dir = "Others";
		loghelper = LogcatHelper.getInstance(this,log_dir,getString(R.string.app_name)+".log");
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		 
		loghelper.start("log2file:v");  

		Log.d(TAG,"onCreate(),logfile test");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		Log.d(TAG,"onCreateOptionsMenu(),logfile test");
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG,"onDestroy(),logfile test");
		loghelper.stop();  
		
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG,"onNewIntent(),logfile test");
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		Log.d(TAG,"onPause(),logfile test");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.d(TAG,"onRestart(),logfile test");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG,"onResume(),logfile test");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.d(TAG,"onStart(),logfile test");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(TAG,"onStop(),logfile test");
		super.onStop();
	}
	
	

}
