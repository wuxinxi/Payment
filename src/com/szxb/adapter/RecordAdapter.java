package com.szxb.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.swept_pay.R;
import com.szxb.bean.RecordBean;
public class RecordAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context context;

	private List<RecordBean> list_beans = new ArrayList<RecordBean>();

	private LayoutInflater inflater;

	@SuppressWarnings("static-access")
	public RecordAdapter(Context context, List<RecordBean> list_beaBeans) {

		this.context = context;
		this.list_beans = list_beaBeans;
		inflater = inflater.from(context);
	}

	@Override
	public int getCount() {
		return list_beans.size();
	}

	@Override
	public Object getItem(int position) {
		return list_beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.record_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.idd);
			holder.textView2 = (TextView) convertView
					.findViewById(R.id.out_tradeno);
			holder.textView3 = (TextView) convertView
					.findViewById(R.id.transactionid);
			holder.textView4 = (TextView) convertView
					.findViewById(R.id.timeend);
			holder.textView5 = (TextView) convertView
					.findViewById(R.id.money);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RecordBean bean = (RecordBean) getItem(position);
		holder.textView1.setText(bean.getId() + "");
		holder.textView2.setText(bean.getOuttradeno());
		holder.textView3.setText(bean.getTransactionid());
		holder.textView4.setText(bean.getTimeend());
		double money=Double.valueOf(bean.getMoney())/100;
		holder.textView5.setText(money+""+Html.fromHtml("<font color=\"#EA1F39\">￥</font>"));
		
		return convertView;
	}

	class ViewHolder {
		TextView textView1, textView2, textView3, textView4,textView5;
	}


}
