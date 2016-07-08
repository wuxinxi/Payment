package com.szxb.handler;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;

public class BaseHandler extends Handler {

	/**
	 * @author TangRen
	 * @param args
	 * @time 2016-7-5
	 */
	private WeakReference<Activity> reference = null;

	public BaseHandler(Activity activity) {
		reference = new WeakReference<Activity>(activity);
	}
}
