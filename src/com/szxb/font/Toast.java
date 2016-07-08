package com.szxb.font;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.swept_pay.R;

public class Toast extends android.widget.Toast {
	/**
	 * @author TangRen
	 * @param args
	 * @time 2016-7-5
	 */

	public Toast(Context context) {
		super(context);
	}

	@SuppressLint("InflateParams")
	public static Toast makeToast(Context context,CharSequence text,int duration){
		
		Toast toast=new Toast(context);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.toast, null);
		TextView textView=(TextView) view.findViewById(R.id.toast_text);
		textView.setText(text);	
		toast.setView(view);
		toast.setGravity(Gravity.BOTTOM, 0, PixelFormat.formatDipToPx(context, 70));
		toast.setDuration(duration);
		return toast;
		
	}
}
