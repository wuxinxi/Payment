package com.szxb.application;

import com.yolanda.nohttp.NoHttp;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		NoHttp.initialize(this);
	}
}
