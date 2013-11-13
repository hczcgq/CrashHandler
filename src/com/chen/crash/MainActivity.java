package com.chen.crash;

import android.os.Bundle;
import android.app.Activity;
public class MainActivity extends Activity {

	String s;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		System.out.println(s.equals("any string"));
	}


}
